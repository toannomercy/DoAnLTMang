/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend;
import org.glassfish.tyrus.server.Server;
/**
 *
 * @author htoan
 */
public class SignalingServerApp {
    public static void main(String[] args) {
//        Server server = new Server("10.147.19.95", 8081, "/", null, SignalingServer.class);
        Server server = new Server("10.147.19.217", 8081, "/", null, SignalingServer.class);

        try {
            server.start();
            System.out.println("WebSocket server is running on ws://fe80::802c:abc:442d:395a%17:8081/signaling");
            System.in.read(); // Nhấn Enter để dừng server
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}
