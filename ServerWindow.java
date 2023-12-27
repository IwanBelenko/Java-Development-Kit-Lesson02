import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ServerWindow extends JFrame implements ServerInterface {
    public static final int WIDTH = 400;
    public static final int HEIGHT = 300;
    public static final String LOG_PATH = "src/server/log.txt";

    private List<ClientInterface> clientList;
    private JTextArea log;
    private JButton btnStart, btnStop;
    private boolean work;

    public ServerWindow() {
        clientList = new ArrayList<>();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setTitle("Chat server");
        setLocationRelativeTo(null);

        createPanel();

        setVisible(true);
    }

    @Override
    public boolean connectUser(ClientInterface client) {
        if (!work) {
            return false;
        }
        clientList.add(client);
        return true;
    }

    @Override
    public void message(String text) {
        if (!work) {
            return;
        }
        text += "";
        appendLog(text);
        answerAll(text);
        saveInLog(text);
    }

    @Override
    public void disconnectUser(ClientGUI clientGUI) {

    }

    @Override
    public String getLog() {
        return null;
    }

    private void answerAll(String text) {
        for (ClientInterface client : clientList) {
            client.answer(text);
        }
    }

    private void saveInLog(String text) {
        try (FileWriter writer = new FileWriter(LOG_PATH, true)) {
            writer.write(text + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readLog() {
        StringBuilder stringBuilder = new StringBuilder();
        try (FileReader reader = new FileReader(LOG_PATH)) {
            int c;
            while ((c = reader.read()) != -1) {
                stringBuilder.append((char) c);
            }
            if (stringBuilder.length() > 0) {
                int length = Math.min(stringBuilder.length(), 1);
                stringBuilder.setLength(length);
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void appendLog(String text) {
        log.append(text + "\n");
    }

    private void createPanel() {
        log = new JTextArea();
        add(log);
        add(createButtons(), BorderLayout.SOUTH);
    }

    private Component createButtons() {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        btnStart = new JButton("Start");
        btnStop = new JButton("Stop");

        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (work) {
                    appendLog("The server has already been started");
                } else {
                    work = true;
                    appendLog("The server is running!");
                }
            }
        });

        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!work) {
                    appendLog("The server has already been stopped");
                } else {
                    work = false;
                    while (!clientList.isEmpty()) {
                        disconnectUser((ClientGUI) clientList.get(clientList.size() - 1));
                    }
                    appendLog("The server has stopped!");
                }
            }
        });

        panel.add(btnStart);
        panel.add(btnStop);
        return panel;
    }
}
