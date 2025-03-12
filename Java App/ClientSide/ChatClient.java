import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.util.Scanner;

public class ChatClient {
    private final String serverAddress;
    private final int serverPort;
    private final String username;

    public ChatClient(String serverAddress, int serverPort, String username) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.username = username;
    }

    public void start() {
        try {
            // Charger le keystore contenant le certificat client
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("/home/walle/git/Instant-Messaging-App/Java App/client.keystore"), "password".toCharArray());

            // Initialiser le KeyManagerFactory avec le keystore
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, "password".toCharArray());

            // Charger le truststore contenant le certificat serveur
            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream("/home/walle/git/Instant-Messaging-App/Java App/client.truststore"), "password".toCharArray());

            // Initialiser le TrustManagerFactory avec le truststore
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(trustStore);

            // Initialiser le contexte SSL
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            // Créer la SSLSocketFactory
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            try (SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(serverAddress, serverPort);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 Scanner scanner = new Scanner(System.in)) {

                System.out.println("Connected to the SSL chat server");

                // Thread pour lire les messages du serveur
                new Thread(() -> { 
                    try { 
                        String message; // Message reçu du serveur
                        while ((message = in.readLine()) != null) { // Lire les messages du serveur
                            System.out.println(message); // Afficher le message
                        }
                    } catch (IOException e) { 
                        System.err.println("Error reading from server: " + e.getMessage());
                    }
                }).start();

                out.println(username); // Envoyer le nom d'utilisateur au serveur

                while (true) { 
                    String message = scanner.nextLine(); // Lire le message de l'utilisateur
                    out.println(message); // Envoyer le message au serveur
                } 
            }
        } catch (Exception e) {
            System.err.println("Error connecting to the server: " + e.getMessage());
        }
    }

    public static void main(String[] args) { 
        String serverAddress = "localhost"; 
        int serverPort = 9999;
        ChatClient client = new ChatClient(serverAddress, serverPort, "DefaultUser");
        client.start();
    }
}
