import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Classe représentant le serveur de chat.
 */
public class ChatServer {
    private final int port;
    private final MessageDatabase database;
    private final MessageBroadcaster broadcaster;
    private static int nextUserId = 1;

    /**
     * Constructeur de la classe ChatServer.
     * 
     * @param port Port du serveur
     */
    public ChatServer(int port) {
        this.port = port;
        this.database = new MessageDatabase();
        this.broadcaster = new MessageBroadcaster();
    }

    /**
     * Démarrer le serveur.
     */
    public void start() {
        if (!database.Connection()) return; // Vérifier la connexion à la base de données

        try {
            // Charger le keystore contenant le certificat serveur
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("/home/walle/git/Instant-Messaging-App/Java App/server.keystore"), "password".toCharArray());

            // Initialiser le KeyManagerFactory avec le keystore
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, "password".toCharArray());

            // Initialiser le contexte SSL
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            // Créer la SSLServerSocketFactory
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

    /**
     * Gérer la connexion d'un client.
     * 
     * @param clientSocket Socket du client
     */
    private void handleClient(Socket clientSocket) {
        int userId = nextUserId++; // Incrémenter l'ID utilisateur

        try ( 
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // Lire les messages du client
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true) // Envoyer des messages au client
        ) {
            broadcaster.addClient(out); // Ajouter le client à la liste des clients
            database.sendChatHistory(out); // Envoyer l'historique des messages au client

            String nickname = Optional.ofNullable(in.readLine()).orElse("User#" + userId); // Lire le pseudo du client
            out.println("Welcome, " + nickname + "!"); // Envoyer un message de bienvenue

            String message; // Message reçu du client
            while ((message = in.readLine()) != null) { // Lire les messages du client
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); // Horodatage du message
                if (database.storeMessage(userId, nickname, message, timestamp)) { // Stocker le message dans la base de données
                    broadcaster.broadcastMessage(userId, nickname, message, timestamp); // Diffuser le message à tous les clients
                } 
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally { // Supprimer le client de la liste des clients
            try { 
                broadcaster.removeClient(clientSocket); 
                clientSocket.close(); 
            } catch (IOException e) { 
                System.err.println("Failed to close client socket."); 
            } 
        }
    }
}
