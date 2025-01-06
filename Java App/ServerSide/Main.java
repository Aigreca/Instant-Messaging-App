public class Main {
    public static void main(String[] args) {
        int port = 9999;
        ChatServer server = new ChatServer(port);
        server.start();
    }
}