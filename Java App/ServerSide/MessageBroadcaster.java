import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MessageBroadcaster {

    private final List<PrintWriter> clients = new ArrayList<>();

    public void addClient(PrintWriter client) {
        synchronized (clients) {
            clients.add(client);
        }
    }

    public void removeClient(Socket clientSocket) {
        synchronized (clients) {
            clients.removeIf(writer -> writer.checkError());
        }
    }

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
