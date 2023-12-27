import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientGUI extends JFrame implements ClientInterface {
    public static final int WIDTH = 400;
    public static final int HEIGHT = 300;

    private final ServerInterface server;
    private boolean connected;
    private String name;

    private JTextArea log;
    private JTextField tfLogin;
    private JTextField tfMessage;
    private JPanel headerPanel;

    public ClientGUI(ServerInterface server) {
        this.server = server;

        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setTitle("Chat");
        setLocation(server.getX() - 500, server.getY());

        createPanel();

        setVisible(true);
    }

    @Override
    public void answer(String text) {
        appendLog(text);
    }

    private void connectToServer() {
        if (server.connectUser(this)) {
            appendLog("You have successfully connected!\n");
            headerPanel.setVisible(false);
            connected = true;
            name = tfLogin.getText();
            String serverLog = server.getLog();
            if (serverLog != null) {
                appendLog(serverLog);
            }
        } else {
            appendLog("Connection failed");
        }
    }

    @Override
    public void disconnectFromServer() {
        if (connected) {
            headerPanel.setVisible(true);
            connected = false;
            server.disconnectUser(this);
            appendLog("You have been disconnected from the server!");
        }
    }

    @Override
    public void message() {
        if (connected) {
            String text = tfMessage.getText();
            if (!text.equals("")) {
                server.message(name + ": " + text);
                tfMessage.setText("");
            }
        } else {
            appendLog("No connection to server");
        }
    }

    private void appendLog(String text) {
        log.append(text + "\n");
    }

    private void createPanel() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createLog());
        add(createFooter(), BorderLayout.SOUTH);
    }

    private Component createHeaderPanel() {
        headerPanel = new JPanel(new GridLayout(2, 3));
        JTextField tfIPAddress = new JTextField("127.0.0.1");
        JTextField tfPort = new JTextField("8189");
        tfLogin = new JTextField("Ivan");
        JPasswordField password = new JPasswordField("123456");
        JButton btnLogin = new JButton("login");
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        });

        headerPanel.add(tfIPAddress);
        headerPanel.add(tfPort);
        headerPanel.add(new JPanel());
        headerPanel.add(tfLogin);
        headerPanel.add(password);
        headerPanel.add(btnLogin);

        return headerPanel;
    }

    private Component createLog() {
        log = new JTextArea();
        log.setEditable(false);
        return new JScrollPane(log);
    }

    private Component createFooter() {
        JPanel panel = new JPanel(new BorderLayout());
        tfMessage = new JTextField();
        tfMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    message();
                }
            }
        });
        JButton btnSend = new JButton("send");
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                message();
            }
        });
        panel.add(tfMessage);
        panel.add(btnSend, BorderLayout.EAST);
        return panel;
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            disconnectFromServer();
        }
        super.processWindowEvent(e);
    }
}