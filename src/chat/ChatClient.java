
package chat;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class represents a chat client
 * @author Jaime Campano, Roberto Garcia, Gonzalo Maldonado
 */
public class ChatClient extends Thread{
    private final static Scanner scanner = new Scanner(System.in);
    private int port;
    private int timeout;
    private String name;
    private String ip;
    private boolean connected;
    private String predecessor;
    public static String succesor;
    public static LinkedHashMap<String,Value> clients;
    public static int id;
    
    public ChatClient(int port, int timeout,String name,String ip){
        this.port = port;
        this.timeout = timeout;
        this.name = name;
        this.ip = ip;
        this.connected = false;
        clients = new LinkedHashMap<String, Value>();
        id = 1;
        start();
    }
    /**
     * Excecution of this thread.
     */   
    public void run(){
      doBroadcast("bcst-in"); //When an user runs for the first time then connects
      updateSuccesor(); //Asign a succesor to this node and notifies them all to update their id's
      while(true){
            try {
                String line = scanner.nextLine();
                
                /**
                 * To send a message the user should write:
                 * msg-username-message
                 * Example: msg-juan-hello
                 */
                if(line.startsWith("msg") && connected){
                    String data[] = line.split("-");
                    String user = data[1];
                    String message = data[2];
                    Value clientValue = clients.get(user);
                    String clientIp = "";
                    
                    if(clientValue!= null){
                        clientIp = clientValue.getIp();
                        sendMessage(succesor,"msg-"+clientIp+"-"+name+"-"+message);
                    }
                    else{
                        System.out.println("No connected user with that username");
                    }
                }
                
                //This is just for debugging, if you want to know your succesor IP.
                else if(line.equals("suc")){
                    System.out.println("your suc is "+succesor);
                }
                
                //List all the connected users by username
                else if(line.equals("connected") && connected){
                    getConnectedUsers();
                }

                //Disconnects the user and sends a message to everyone
                else if(line.equals("disconnect") && connected){
                    doBroadcast("bcst-out");
                    System.exit(1);
                }
                
                else{
                    System.out.println("No such option");
                }

            }
            catch (Exception e) {
            }
        }
    }
    
    /**
     * It updates the succesor of a node in function of the nodes in the 
     * system. Takes the first node (minimum id) and then update the 
     * ip of the succesor.
     */
    private void updateSuccesor(){
        int minId = Integer.MAX_VALUE;
        int maxId = Integer.MIN_VALUE;
        String finalNodeIp="";
        
        if(clients.isEmpty()){
            succesor = this.ip;
            ChatServer.succesor = succesor;
            return;
        }
        for (Map.Entry entry : clients.entrySet()) {
            int currentId = Integer.parseInt(((Value)entry.getValue()).getId());
            String currentIp = ((Value)entry.getValue()).getIp();
            
            if(currentId < minId){
                this.succesor = currentIp;
                minId = currentId;
                ChatServer.succesor = succesor;
            }
            if(currentId > maxId){
                maxId = currentId;
                finalNodeIp = currentIp;
            }
        }
        
        
        
        try { 
            byte[] buffer = ("update-"+this.ip).getBytes();
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket out = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(finalNodeIp), port);
            socket.send(out);
        } 
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        
    }
    
    /**
     * Get all connected users by username.
     */
    public void getConnectedUsers(){
        if(clients.isEmpty()){
            System.out.println("No connected users");
            return;
        }
        for (Map.Entry entry : clients.entrySet()) {
            System.out.println(entry.getKey());
        }   
    }
  
    /**
     * Sends a message to the given ip.
     * @param ip ip to send the message
     * @param message the message to send
     */
    public void sendMessage(String ip, String message){
        try {
            String user="someone";
            for (Map.Entry entry : clients.entrySet()) {
                if(((Value)entry.getValue()).getIp().equals(ip)){
                    user = entry.getKey().toString();
                }
            } 
            System.out.println("Sending message to "+user);
            byte[] buffer = message.getBytes();
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket out = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), port);
            socket.send(out);
            
        } 
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        
    }
    
    public void doBroadcast(String option){
        try {
            String data = option+"-"+name + "-" + ip+"-"+succesor;
            byte[] buffer = data.getBytes();
            DatagramSocket socket = new DatagramSocket();
            String broadcastIp = getBroadcast();
            InetAddress groupAddress = InetAddress.getByName(broadcastIp);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, groupAddress, port);
            socket.send(packet);
            
            if(option.equals("bcst-in")){
                connected = true;//esto es solo porque comente lo de abajo
                System.out.println("Connecting, please wait...");
                Thread.sleep(timeout);
                Utilities.sendMessage(getBroadcast(), "id-"+name+"-"+ip+"-"+id,port);
                System.out.println("Welcome");

            }
            
        } 
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public String getBroadcast(){
        return "192.168.1.0";
    }
    

  
}


    