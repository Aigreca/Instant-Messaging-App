import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatClientGUI extends JFrame {
    private JTextField serverAddressField;
    private JTextField serverPortField;
    private JTextField usernameField;

    public ChatClientGUI() {
        setTitle("Chat Client");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Server Address:"));
        serverAddressField = new JTextField("localhost");
        panel.add(serverAddressField);

        panel.add(new JLabel("Server Port:"));
        serverPortField = new JTextField("9999");
        panel.add(serverPortField);

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serverAddress = serverAddressField.getText();
                int serverPort = Integer.parseInt(serverPortField.getText());
                String username = usernameField.getText();
                ChatClient client = new ChatClient(serverAddress, serverPort, username);
                client.start();
                dispose();
            }
        });
        panel.add(connectButton);

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChatClientGUI().setVisible(true);
            }
        });
    }
}
