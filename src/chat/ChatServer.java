/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedHashMap;

public class ChatServer extends Thread{
    
    private int port;
    private int timeout;
    private String name; 
    private String ip;
    private LinkedHashMap<String,Value> clients;
    private int id;
    public static String succesor;

    
    public ChatServer(int port, int timeout,String name, String ip){
        this.port = port;
        this.timeout = timeout;
        this.name = name;
        this.ip = ip;
        this.clients = new LinkedHashMap<String, Value>();
        id=1;
        start();
    }
    
  public void run(){
    try {
            DatagramSocket socket = new DatagramSocket(port);
            while (true) {
                
                byte[] buffer = new byte[65507];        			
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); 
                String message = new String(packet.getData(),0,packet.getLength());
               
                if(message.startsWith("bcst")){
                    String data[] = message.split("-");
                    String userName = data[2];
                    String ipUser = data[3];
                    if(data[1].equals("in")){ //si se esta conectando
                        if(!ipUser.equals(ip)){
                            System.out.println(userName+" has connected");
                            clients.put(userName,new Value(ipUser,""));
                            //sendAck(packet, socket);
                            sendMessage(ipUser, "ip-"+name+"-"+ip+"-"+id);
                        }
                        
                        
                    }
                    else{ //si se esta desconectando actualizo el sucesor y notifico
                        System.out.println(userName+" has disconnected");
                        System.out.println("my suc is "+succesor+" and has disc "+ipUser);
                        if(succesor.equals(ipUser)){
                           succesor = data[4]; 
                           ChatClient.succesor = succesor;
                        }
                        clients.remove(data[2]);
                        //sendAck(packet, socket);
                    }
                    ChatClient.clients = clients;
                }
                else if(message.startsWith("ip-")){
                    String data[] = message.split("-");
                    String nameUser = data[1];
                    String ipUser = data[2];
                    String idUser = data[3];
                    System.out.println(message);
                    if(!ipUser.equals(this.ip)){
                        clients.put(nameUser,new Value(ipUser,idUser)); 
                        this.id++;
                        ChatClient.id = id;
                        //sendMessage(ipUser,"id-"+this.name+"-"+this.ip+"-"+this.id);
                    }

                }
                else if(message.startsWith("id-")){
                    String data[] = message.split("-");
                    String username = data[1];
                    String ipUser = data[2];
                    String idUser = data[3];
                    
                    if(!ipUser.equals(ip)){
                      clients.remove(username);
                      clients.put(username,new Value(ipUser, idUser));
                      ChatClient.clients = clients;  
                    }
                    
                }
                
                else if(message.startsWith("update")){
                    String data[] = message.split("-");
                    String newSuccesor = data[1];
                    succesor = newSuccesor;
                    ChatClient.succesor = newSuccesor;
                }
                
                
                else if (message.startsWith("msg")){
                    String data[] = message.split("-");
                    String rcvdIp = data[1];
                    String rcvdUser = data[2];
                    String rcvdMsg = data[3];
                    if(rcvdIp.equals(this.ip)){
                       System.out.println(rcvdUser + " says: "+rcvdMsg); 
                    }
                    else{
                        sendMessage(succesor,"msg-"+rcvdIp+"-"+rcvdUser+"-"+rcvdMsg);
                    }

                }
            }
        }
        catch (SocketException se) {
            System.err.println("chat error " + se); 
            
        }
        catch (IOException se) {
            System.err.println("chat error " + se);
        }
        System.exit(1); 
  }
  
  
  public void sendMessage(String ip, String message){
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
  
  
  public void sendAck(DatagramPacket packet, DatagramSocket socket){
        try {
            String message="1";
            byte[] buffer = message.getBytes();
            InetAddress address = packet.getAddress();
            //si pongo solo port se va a la b!!!
            packet = new DatagramPacket(buffer,buffer.length,address,packet.getPort());
            socket.send(packet);
        } 
        catch (IOException ex) {
            System.out.println("chat error"+ex);
        }
  }

}