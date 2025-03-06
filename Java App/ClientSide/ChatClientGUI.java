import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatClientGUI extends JFrame {
    private JTextField serverAddressField;
    private JTextField serverPortField;
    private JTextField usernameField;

    public ChatClientGUI() {
        setTitle("ALEAU");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set the water drop logo
        ImageIcon logo = new ImageIcon("/home/walle/git/Instant-Messaging-App/Java App/water_drop.png");
        setIconImage(logo.getImage());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(173, 216, 230)); // Set background color to pastel blue
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Server Address:"), gbc);

        gbc.gridx = 1;
        serverAddressField = new JTextField("localhost", 15);
        panel.add(serverAddressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Server Port:"), gbc);

        gbc.gridx = 1;
        serverPortField = new JTextField("9999", 15);
        panel.add(serverPortField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serverAddress = serverAddressField.getText();
                int serverPort = Integer.parseInt(serverPortField.getText());
                String username = usernameField.getText();
                new ChatInterfaceGUI(serverAddress, serverPort, username).setVisible(true);
                dispose();
            }
        });
        panel.add(connectButton, gbc);

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
