public class Main {
    public static void main(String[] args) {
        // Définir le port du serveur
        int port = 9999;
        // Créer une instance du serveur de chat
        ChatServer server = new ChatServer(port);
        // Démarrer le serveur
        server.start();
    }
}