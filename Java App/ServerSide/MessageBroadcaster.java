import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant le diffuseur de messages.
 */
public class MessageBroadcaster {
    private final List<PrintWriter> clients = new ArrayList<>();

    /**
     * Ajouter un client à la liste.
     * 
     * @param client PrintWriter du client
     */
    public void addClient(PrintWriter client) {
        synchronized (clients) { // Synchroniser l'accès à la liste des clients
            clients.add(client);
        }
    }

    /**
     * Supprimer un client de la liste.
     * 
     * @param clientSocket Socket du client
     */
    public void removeClient(Socket clientSocket) { 
        synchronized (clients) { 
            clients.removeIf(PrintWriter::checkError);
        }
    }

    /**
     * Diffuser un message à tous les clients.
     * 
     * @param userId ID de l'utilisateur
     * @param nickname Pseudo de l'utilisateur
     * @param message Message à diffuser
     * @param timestamp Horodatage du message
     */
    public void broadcastMessage(int userId, String nickname, String message, String timestamp) {
        String formattedMessage = String.format("[%s] (User #%d) %s: %s", timestamp, userId, nickname, message);
        synchronized (clients) {
            for (PrintWriter writer : clients) {
                writer.println(formattedMessage);
            }
        }
        System.out.println("Broadcasted: " + formattedMessage);
    }
}
