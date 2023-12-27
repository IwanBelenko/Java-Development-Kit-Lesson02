public interface ServerInterface {
    boolean connectUser(ClientInterface client);

    void message(String text);

    void disconnectUser(ClientGUI clientGUI);

    String getLog();

    int getX();

    int getY();
}