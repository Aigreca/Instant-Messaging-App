/**
 * Classe principale pour lancer le serveur de chat.
 */
public class Main {
    /**
     * Méthode principale pour lancer le serveur de chat.
     * 
     * @param args Arguments de la ligne de commande
     */
    public static void main(String[] args) {
        // Définir le port du serveur
        int port = 9999;
        // Créer une instance du serveur de chat
        ChatServer server = new ChatServer(port);
        // Démarrer le serveur
        server.start();
    }
}