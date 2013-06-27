package chat;



import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.SocketException;
import java.util.LinkedHashMap;

/**
 * This class represents the server of the node, which
 * will be constantly listening for requests. 
 * @author Jaime Campano, Roberto Garcia, Gonzalo Maldonado
 */
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
    
    /**
     * Excecution of this thread.
     */
    public void run(){
      try {
              DatagramSocket socket = new DatagramSocket(port);
              while (true) {

                  byte[] buffer = new byte[65507];        			
                  DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                  socket.receive(packet); 
                  String message = new String(packet.getData(),0,packet.getLength());

                  //if a user is connecting or disconnecting
                  if(message.startsWith("bcst")){
                      String data[] = message.split("-");
                      String userName = data[2];
                      String ipUser = data[3];
                      
                      if(data[1].equals("in")){ //a new user has connected
                          if(!ipUser.equals(ip)){
                              System.out.println(userName+" has connected");
                              clients.put(userName,new Value(ipUser,""));
                              Utilities.sendMessage(ipUser, "ip-"+name+"-"+ip+"-"+id,port);
                          }


                      }
                      else{
                          //if an user has disconnected then i update the
                          //succesor and notify the change
                          System.out.println(userName+" has disconnected");
                          if(succesor.equals(ipUser)){
                             succesor = data[4]; 
                             ChatClient.succesor = succesor;
                          }
                          clients.remove(data[2]);
                      }
                      ChatClient.clients = clients;
                  }
                  
                  /**
                   * When a new node connects, it waits for a message 
                   * telling him which nodes are on the network. Then
                   * the node updates it's id due to the number of 
                   * notifications that receipts.
                   */
                  else if(message.startsWith("ip-")){
                      String data[] = message.split("-");
                      String nameUser = data[1];
                      String ipUser = data[2];
                      String idUser = data[3];
                      if(!ipUser.equals(this.ip)){
                          clients.put(nameUser,new Value(ipUser,idUser)); 
                          this.id+= Integer.parseInt(idUser);
                          ChatClient.id = id;
                      }

                  }
                  /**
                   * Updates a new node with its new id
                   */
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

                  /**
                   * Updates the succesor of a node
                   */
                  else if(message.startsWith("update")){
                      String data[] = message.split("-");
                      String newSuccesor = data[1];
                      succesor = newSuccesor;
                      ChatClient.succesor = newSuccesor;
                  }

                  /**
                   * Receives a message, then check if the message belongs 
                   * to him or if it should be send to his succesor. This 
                   * works with the ring TOPOLOGY!. 
                   */
                  else if (message.startsWith("msg")){
                      String data[] = message.split("-");
                      String rcvdIp = data[1];
                      String rcvdUser = data[2];
                      String rcvdMsg = data[3];
                      if(rcvdIp.equals(this.ip)){
                         System.out.println(rcvdUser + " says: "+rcvdMsg); 
                      }
                      else{
                          System.out.println("Sending message through this node to "+rcvdIp);
                          Utilities.sendMessage(succesor,"msg-"+rcvdIp+"-"+rcvdUser+"-"+rcvdMsg,port);
                          
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
  

}
