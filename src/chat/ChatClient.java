/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class ChatClient extends Thread{
    private final static Scanner scanner = new Scanner(System.in);
    private int port;
    private int timeout;
    private String name;
    private String ip;
    private boolean connected;
    private String predecessor;
    public static String succesor;
    //username, <ip, id>
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
       
    public void run(){
      doBroadcast("bcst-in");
      updateSuccesor();
      while(true){
            try {
                String line = scanner.nextLine();
                if(line.startsWith("msg") && connected){
                    String data[] = line.split("-");
                    //Ejemplo: msg-juan-hola como estas
                    String user = data[1];
                    String message = data[2];
                    String clientIp = clients.get(user).getIp();
                    if(clientIp != null){
                        sendMessage(succesor,"msg-"+clientIp+"-"+user+"-"+message);
                    }
                    else{
                        System.out.println("No connected user with that username");
                    }
                }
                else if(line.equals("suc")){
                    System.out.println("your suc is "+succesor);
                }
                else if(line.equals("connected") && connected){ //lista todos los usuarios conectados
                    getConnectedUsers();
                }

                else if(line.equals("disconnect") && connected){
                    doBroadcast("bcst-out");
                    System.exit(1);
                }
                else if(!connected){
                    System.out.println("Please connect");
                }
                else{
                    System.out.println("No such option");
                }

            }
            catch (Exception e) {
                System.out.println("Error sending datagram " + e);
            }
        }
    }
    
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
    
    public void getConnectedUsers(){
        if(clients.isEmpty()){
            System.out.println("No connected users");
            return;
        }
        for (Map.Entry entry : clients.entrySet()) {
            System.out.println(entry.getKey()+"-"+((Value)entry.getValue()).getId());
        }   
    }
  
    public void sendMessage(String ip, String message){
        try {
            String user="someone";
            for (Map.Entry entry : clients.entrySet()) {
                if(entry.getValue().equals(ip)){
                    user = entry.getKey().toString();
                }
            } 
            System.out.println("Sending message to "+user);
            byte[] buffer = message.getBytes();
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket out = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), port);
            socket.send(out);
            
            socket.setSoTimeout(timeout);
            out = new DatagramPacket (buffer, buffer.length);
            socket.receive(out);
            String received = new String(out.getData(), 0, out.getLength());
            
            if(received.equals("1")){
                System.out.println("Message received");
            }
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
                sendMessage2(getBroadcast(), "id-"+name+"-"+ip+"-"+id);
                System.out.println("Welcome");

            }
            
        } 
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public String getBroadcast(){
        return "192.168.2.0";
    }
    
    public void sendMessage2(String ip, String message){
        try {
            
            byte[] buffer = message.getBytes();
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket out = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), port);
            socket.send(out);

        } 
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        
    }
  
}


    