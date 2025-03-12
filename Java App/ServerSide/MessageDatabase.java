import java.io.PrintWriter;
import java.sql.*;

/**
 * Classe représentant la base de données des messages.
 */
public class MessageDatabase {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/chatapp";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    /**
     * Connexion à la base de données.
     * 
     * @return true si la connexion est réussie, false sinon
     */
    public boolean Connection() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Connected to the database successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Stocker les messages dans la base de données.
     * 
     * @param userId ID de l'utilisateur
     * @param nickname Pseudo de l'utilisateur
     * @param message Message à stocker
     * @param timestamp Horodatage du message
     * @return true si le message est stocké avec succès, false sinon
     */
    public boolean storeMessage(int userId, String nickname, String message, String timestamp) {
        String sql = "INSERT INTO messages (user_id, nickname, message, timestamp) VALUES (?, ?, ?, ?)"; // Requête SQL
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); // Connexion à la base de données
             PreparedStatement stmt = connection.prepareStatement(sql)) { // Préparer la requête SQL
            stmt.setInt(1, userId); 
            stmt.setString(2, nickname); 
            stmt.setString(3, message);
            stmt.setString(4, timestamp); // 
            stmt.executeUpdate(); // Exécuter la requête SQL
            return true;
        } catch (SQLException e) { 
            System.err.println("Error storing message: " + e.getMessage());
            return false;
        }
    }

    /**
     * Envoyer l'historique des messages au client.
     * 
     * @param out PrintWriter pour envoyer les messages au client
     */
    public void sendChatHistory(PrintWriter out) {
        String sql = "SELECT user_id, nickname, message, timestamp FROM messages ORDER BY timestamp ASC"; // Requête SQL
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); // Connexion à la base de données
             PreparedStatement stmt = connection.prepareStatement(sql); // Préparer la requête SQL
             ResultSet rs = stmt.executeQuery()) { // Exécuter la requête SQL
            out.println("Chat History:"); 
            while (rs.next()) { 
                out.printf("[%s] (User #%d) %s: %s%n", 
                        rs.getString("timestamp"), 
                        rs.getInt("user_id"), 
                        rs.getString("nickname"), 
                        rs.getString("message")); 
            }
        } catch (SQLException e) { 
            System.err.println("Error retrieving chat history: " + e.getMessage());
        }
    }
}
