import javax.swing.*;
import java.awt.*;

/**
 * Classe représentant l'interface graphique pour se connecter au serveur de chat.
 */
public class ChatClientGUI extends JFrame {
    private JTextField serverAddressField;
    private JTextField serverPortField;
    private JTextField usernameField;

    /**
     * Constructeur de la classe ChatClientGUI.
     */
    public ChatClientGUI() {
        setTitle("ALEAU");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Définir le logo de la goutte d'eau
        ImageIcon logo = new ImageIcon("/home/walle/git/Instant-Messaging-App/Java App/water_drop.png");
        setIconImage(logo.getImage());

        JPanel panel = new JPanel(new GridBagLayout()); // Panneau avec un gestionnaire de disposition GridBagLayout
        panel.setBackground(new Color(173, 216, 230)); // Couleur de fond bleu pastel
        GridBagConstraints gbc = new GridBagConstraints(); // Contraintes de disposition
        gbc.insets = new Insets(5, 5, 5, 5); // Marge intérieure de 5 pixels
        gbc.fill = GridBagConstraints.HORIZONTAL; // Remplissage horizontal

        gbc.gridx = 0; 
        gbc.gridy = 0; 
        panel.add(new JLabel("Server Address:"), gbc); // Case "Server Address"

        gbc.gridx = 1;
        serverAddressField = new JTextField("localhost", 15); // Champ de texte pour l'adresse du serveur
        panel.add(serverAddressField, gbc); // Ajouter le champ de texte à la grille

        gbc.gridx = 0; 
        gbc.gridy = 1; 
        panel.add(new JLabel("Server Port:"), gbc); // Case "Server Port"

        gbc.gridx = 1;
        serverPortField = new JTextField("9999", 15); // Champ de texte pour le port du serveur prerempli avec 9999
        panel.add(serverPortField, gbc); 

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Username:"), gbc); // Case "Username"
 
        gbc.gridx = 1;
        usernameField = new JTextField(15); // Champ de texte pour le nom d'utilisateur
        panel.add(usernameField, gbc); 

        gbc.gridx = 0; 
        gbc.gridy = 3; 
        gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.CENTER; // Centrer le bouton
        JButton connectButton = new JButton("Connect"); // Bouton de connexion
        connectButton.addActionListener(e -> { // Connexion au serveur en cliquant sur le bouton
            String serverAddress = serverAddressField.getText(); // Adresse du serveur
            int serverPort = Integer.parseInt(serverPortField.getText()); // Port du serveur
            String username = usernameField.getText(); // Nom d'utilisateur
            new ChatInterfaceGUI(serverAddress, serverPort, username).setVisible(true); // Ouvrir l'interface graphique du client
            dispose(); // Fermer la fenêtre de connexion
        });
        panel.add(connectButton, gbc); // Ajouter le bouton de connexion à la grille

        add(panel); // Ajouter le panneau à la fenêtre
    }

    /**
     * Méthode principale pour lancer l'interface graphique du client de chat.
     * 
     * @param args Arguments de la ligne de commande
     */
    public static void main(String[] args) { 
        SwingUtilities.invokeLater(() -> new ChatClientGUI().setVisible(true)); // Créer une instance de l'interface graphique du client
    } 
}
