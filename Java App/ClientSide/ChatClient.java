import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.util.Scanner;

public class ChatClient {

    private final String serverAddress;
    private final int serverPort;

    public ChatClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void start() {
        try {
            // Load the keystore containing the client certificate
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("client.keystore"), "password".toCharArray());

            // Initialize the key manager factory with the keystore
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, "password".toCharArray());

            // Initialize the SSL context
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            // Create SSLSocketFactory
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            try (SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(serverAddress, serverPort);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 Scanner scanner = new Scanner(System.in)) {

                System.out.println("Connected to the SSL chat server");

                new Thread(() -> {
                    try {
                        String message;
                        while ((message = in.readLine()) != null) {
                            System.out.println(message);
                        }
                    } catch (IOException e) {
                        System.err.println("Error reading from server: " + e.getMessage());
                    }
                }).start();

                while (true) {
                    String message = scanner.nextLine();
                    out.println(message);
                }

            }
        } catch (Exception e) {
            System.err.println("Error connecting to the server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 9999;
        ChatClient client = new ChatClient(serverAddress, serverPort);
        client.start();
    }
}
