/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

/**
 *
 * @author gmaldonado
 */
public class Value {
    
    private String ip;
    private String id;
    
    public Value(String ip, String id){
        this.ip = ip;
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Value{" + "ip=" + ip + ", id=" + id + '}';
    }
    
    
    
    
}
