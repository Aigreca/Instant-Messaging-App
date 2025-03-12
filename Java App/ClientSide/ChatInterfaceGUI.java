import javax.net.ssl.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.*;
import java.security.KeyStore;

public class ChatInterfaceGUI extends JFrame { 
    private JTextArea chatArea; // Zone de texte pour afficher les messages
    private JTextField messageField; // Champ de texte pour saisir les messages
    private PrintWriter out; // Écriture des messages

    public ChatInterfaceGUI(String serverAddress, int serverPort, String username) {
        setTitle("ALEAU"); // Titre de la fenêtre
        setSize(600, 500); // Taille de la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Fermer l'application lors de la fermeture de la fenêtre
        setLocationRelativeTo(null); // Centrer la fenêtre

        // Zone de texte pour afficher les messages
        chatArea = new JTextArea(); // Zone de texte
        chatArea.setEditable(false); // Désactiver l'édition
        chatArea.setLineWrap(true); // Saut de ligne automatique
        chatArea.setBorder(new LineBorder(Color.GRAY, 1, true)); // Bordures arrondies
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10)); // Ajouter des marges
        scrollPane.setBackground(new Color(173, 216, 230)); // Couleur de fond bleu pastel

        // Champ de texte pour saisir les messages
        messageField = new JTextField();
        messageField.setBorder(new LineBorder(Color.GRAY, 1, true)); // Bordures arrondies
        messageField.addActionListener(e -> { // Envoyer le message en appuyant sur Entrée
            sendMessage(messageField.getText());  // Zone de texte pour saisir les messages
        });

        // Bouton pour envoyer les messages
        JButton sendButton = new JButton("Send"); // Bouton d'envoi
        sendButton.addActionListener(e -> { // Envoyer le message en cliquant sur le bouton
            sendMessage(messageField.getText()); // Envoyer le message
            messageField.setText(""); // Effacer le champ de texte après l'envoi
        });

        // Panneau pour le champ de texte et le bouton
        JPanel panel = new JPanel(new BorderLayout()); // Panneau avec un gestionnaire de disposition BorderLayout
        panel.add(messageField, BorderLayout.CENTER); // Ajouter le champ de texte au centre
        panel.add(sendButton, BorderLayout.EAST); // Ajouter le bouton à droite
        panel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Ajouter des marges
        panel.setBackground(new Color(173, 216, 230)); // Couleur de fond bleu pastel

        add(scrollPane, BorderLayout.CENTER); // Ajouter la zone de texte au centre
        add(panel, BorderLayout.SOUTH); // Ajouter le panneau avec le champ de texte et le bouton en bas

        connectToServer(serverAddress, serverPort, username); // Connexion au serveur
    }

    // Connexion au serveur
    private void connectToServer(String serverAddress, int serverPort, String username) {
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

            // Créer la socket SSL
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Thread pour lire les messages du serveur
            new Thread(() -> { 
                try {
                    String message; // Message reçu du serveur
                    while ((message = in.readLine()) != null) { // Lire les messages du serveur
                        chatArea.append(message + "\n"); // Ajouter le message à la zone de texte
                        chatArea.setCaretPosition(chatArea.getDocument().getLength()); // Faire défiler vers le bas
                    }
                } catch (IOException e) { 
                    e.printStackTrace(); 
                }
            }).start(); 

            out.println(username); // Envoyer le nom d'utilisateur au serveur
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Envoyer un message au serveur
    private void sendMessage(String message) { 
        if (out != null) { 
            out.println(message); 
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatInterfaceGUI("localhost", 9999, "DefaultUser").setVisible(true));
    } // Créer une instance de l'interface graphique du client et l'afficher
}
