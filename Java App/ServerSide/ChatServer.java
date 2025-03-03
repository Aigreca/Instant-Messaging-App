// Handles client connections and coordinates database and broadcasting operations.

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChatServer {

    private final int port;
    private final MessageDatabase database;
    private final MessageBroadcaster broadcaster;
    private static int nextUserId = 1;

    // Initialization of MessageDatabase & MessageBroadcaster
    public ChatServer(int port) {
        this.port = port;
        this.database = new MessageDatabase();
        this.broadcaster = new MessageBroadcaster();
    }

    // Start the server
    public void start() {
        // Checks if the Database is reachable using a method from "MessageDatabase.java"
        if (!database.Connection()) return;

        try {
            // Load the keystore containing the server certificate
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("/home/walle/git/Instant-Messaging-App/Java App/server.keystore"), "password".toCharArray());

            // Initialize the key manager factory with the keystore
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, "password".toCharArray());

            // Initialize the SSL context
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            // Create SSLServerSocketFactory
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            try (SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port)) {
                System.out.println("SSL Server is running on port " + port);

                while (true) {
                    SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                    new Thread(() -> handleClient(clientSocket)).start();
                }
            }
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        // Increment of the userId in the Database for every connection
        int userId = nextUserId++;
        
        // Read client inputs and send messages back
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            
            broadcaster.addClient(out);
            database.sendChatHistory(out);

            out.println("Enter your nickname:");
            String nickname = Optional.ofNullable(in.readLine()).orElse("User#" + userId);
            out.println("Welcome, " + nickname + "!");

            String message;
            while ((message = in.readLine()) != null) {
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                if (database.storeMessage(userId, nickname, message, timestamp)) {
                    broadcaster.broadcastMessage(userId, nickname, message, timestamp);
                }
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            try {
                broadcaster.removeClient(clientSocket);
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Failed to close client socket.");
            }
        }
    }
}
