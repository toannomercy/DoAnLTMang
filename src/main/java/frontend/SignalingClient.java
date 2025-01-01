/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package frontend;

import javax.websocket.*;
import java.net.URI;
/**
 *
 * @author htoan
 */
@ClientEndpoint
public class SignalingClient {
    private Session session;

    public void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, new URI("ws://localhost:8081/signaling"));
            System.out.println("Connected to signaling server");
        } catch (Exception e) {
            e.printStackTrace();  
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connection opened: " + session.getId());
    }

//    @OnMessage
//    public void onMessage(String message) {
//        System.out.println("Received message: " + message);
//
//        // Phân tích và xử lý tín hiệu
//        if (message.startsWith("INCOMING_CALL")) {
//            String senderId = message.split(" ")[2];
//            System.out.println("Incoming call from: " + senderId);
//
//            // Hiển thị thông báo cho người dùng
//            javax.swing.SwingUtilities.invokeLater(() -> {
//                javax.swing.JOptionPane.showMessageDialog(null,
//                        "Cuộc gọi đến từ client: " + senderId,
//                        "Thông báo", javax.swing.JOptionPane.INFORMATION_MESSAGE);
//            });
//        } else if (message.startsWith("CALL_FAILED")) {
//            javax.swing.SwingUtilities.invokeLater(() -> {
//                javax.swing.JOptionPane.showMessageDialog(null,
//                        "Cuộc gọi thất bại: " + message,
//                        "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
//            });
//        }
//    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received message: " + message);

        // Phân tích và xử lý tín hiệu
        if (message.startsWith("INCOMING_CALL")) {
            String senderId = message.split(" ")[2];
            System.out.println("Incoming call from: " + senderId);

            // Hiển thị thông báo cho người dùng
            javax.swing.SwingUtilities.invokeLater(() -> {
                int response = javax.swing.JOptionPane.showConfirmDialog(null,
                        "Cuộc gọi đến từ client: " + senderId + "\nBạn có muốn chấp nhận không?",
                        "Thông báo",
                        javax.swing.JOptionPane.YES_NO_OPTION,
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);

                if (response == javax.swing.JOptionPane.YES_OPTION) {
                    sendMessage("ACCEPT_CALL " + senderId);
                    System.out.println("Call accepted for client: " + senderId);
                } else {
                    sendMessage("REJECT_CALL " + senderId);
                    System.out.println("Call rejected for client: " + senderId);
                }
            });
        } else if (message.startsWith("CALL_FAILED")) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Cuộc gọi thất bại: " + message,
                        "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
            });
        } else if (message.equals("CALL_ACCEPTED")) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Cuộc gọi đã được chấp nhận.",
                        "Thông báo", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            });
            System.out.println("Call accepted by the receiver.");
        } else if (message.equals("CALL_REJECTED")) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Cuộc gọi đã bị từ chối.",
                        "Thông báo", javax.swing.JOptionPane.WARNING_MESSAGE);
            });
            System.out.println("Call rejected by the receiver.");
        } else {
            System.out.println("Unknown message type: " + message);
        }
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Connection closed: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error: " + throwable.getMessage());
    }

    public void sendMessage(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
