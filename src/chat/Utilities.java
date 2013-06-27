package chat;



import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * This is a class for utility methods.
 * @author Jaime Campano, Roberto Garcia, Gonzalo Maldonado
 */
public class Utilities {
    
    /**
     * Sends a message to a given ip
     * @param ip ip to send the message
     * @param message message to send
     * @param port port to send the message
     */
    public static void sendMessage(String ip, String message, int port){
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
    
    
//    public void sendAck(DatagramPacket packet, DatagramSocket socket){
//        try {
//            String message="1";
//            byte[] buffer = message.getBytes();
//            InetAddress address = packet.getAddress();
//            packet = new DatagramPacket(buffer,buffer.length,address,packet.getPort());
//            socket.send(packet);
//        } 
//        catch (IOException ex) {
//            System.out.println("chat error"+ex);
//        }
//    }
    
    
}
