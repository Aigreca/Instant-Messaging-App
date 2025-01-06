import java.io.PrintWriter;
import java.sql.*;

public class MessageDatabase {
	
    // MySQL CONFIGURATION ----> Store the messages
    private static final String DB_URL = "jdbc:mysql://localhost:3306/chatapp";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Connection to the Database
    public boolean Connection() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Connected to the database successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }

    // Storing the messages in the table
    public boolean storeMessage(int userId, String nickname, String message, String timestamp) {
        String sql = "INSERT INTO messages (user_id, nickname, message, timestamp) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, nickname);
            stmt.setString(3, message);
            stmt.setString(4, timestamp);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error storing message: " + e.getMessage());
            return false;
        }
    }

    
    public void sendChatHistory(PrintWriter out) {
        String sql = "SELECT user_id, nickname, message, timestamp FROM messages ORDER BY timestamp ASC";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
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
