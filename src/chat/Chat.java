package chat;

/**
 * This class represents a P2P machine, which has a server and a client, 
 * both are threads.
 * @author Jaime Campano, Roberto Garcia, Gonzalo Maldonado
 */

public class Chat extends Thread {
    
    private ChatServer server;
    private ChatClient client;
    private static final int TIMEOUT = 5000;
    private static final int PORT = 10000;
    private static final String NAME = "gonzalo";
    private static final String IP = "192.168.1.105";
    
    public Chat(String args[]) throws Exception{
        server = new ChatServer(PORT,TIMEOUT,NAME,IP);
        client = new ChatClient(PORT,TIMEOUT,NAME,IP);
    }

    public static void main(String args[]) throws Exception {
        Chat chat = new Chat(args);
    }
}