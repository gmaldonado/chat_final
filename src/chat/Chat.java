package chat;

public class Chat extends Thread {
    
    private ChatServer server;
    private ChatClient client;
    private static final int TIMEOUT = 5000;
    private static final int PORT = 10000;
    private static final String NAME = "gonzalo";
    private static final String IP = "172.17.50.250";
    
    public Chat(String args[]) throws Exception{
        server = new ChatServer(PORT,TIMEOUT,NAME,IP);
        client = new ChatClient(PORT,TIMEOUT,NAME,IP);
    }

    public static void main(String args[]) throws Exception {
        Chat chat = new Chat(args);
    }
}