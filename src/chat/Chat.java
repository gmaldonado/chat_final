package chat;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static  String NAME;
    private static  String IP;
    
    public Chat(String args[]) throws Exception{
        Scanner scan = new Scanner(System.in);
        System.out.print("User: ");
        NAME = scan.nextLine();
        System.out.print("Ip: ");
        IP = scan.nextLine();
        while(!validate(IP))
        {
            System.out.print("Ip: ");
            IP = scan.nextLine();
        }
	String broadcast = "255.255.255.255";  
        server = new ChatServer(PORT,TIMEOUT,NAME,IP);
        client = new ChatClient(PORT,TIMEOUT,NAME,IP,broadcast);
    }

    public static void main(String args[]) throws Exception {
        Chat chat = new Chat(args);
    }
    
    private static final String PATTERN = 
        "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    public static boolean validate(final String ip){          

          Pattern pattern = Pattern.compile(PATTERN);
          Matcher matcher = pattern.matcher(ip);
          return matcher.matches();             
    }
    
    
}
