/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
/**
 *
 * @author htoan
 */
@ServerEndpoint("/signaling")
public class SignalingServer {
    // Quản lý danh sách các kết nối, sử dụng ID làm key
    private static final ConcurrentHashMap<String, Session> clients = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        clients.put(session.getId(), session);
        System.out.println("New connection: " + session.getId());
    }

//    @OnMessage
//    public void onMessage(String message, Session session) {
//        System.out.println("Received message from " + session.getId() + ": " + message);
//
//        // Phân tích tín hiệu
//        if (message.startsWith("CALL")) {
//            String[] parts = message.split(" ");
//            if (parts.length == 3) {
//                String targetId = parts[1]; // ID của client đích
//                String senderId = session.getId();
//
//                // Gửi tín hiệu gọi đến client đích
//                Session targetSession = clients.get(targetId);
//                if (targetSession != null && targetSession.isOpen()) {
//                    try {
//                        String callMessage = "INCOMING_CALL FROM " + senderId;
//                        targetSession.getBasicRemote().sendText(callMessage);
//                        System.out.println("Forwarded call from " + senderId + " to " + targetId);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    try {
//                        session.getBasicRemote().sendText("CALL_FAILED: Client " + targetId + " not available");
//                        System.out.println("Call failed: Client " + targetId + " not available");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message from " + session.getId() + ": " + message);

        try {
            // Phân tích tín hiệu
            if (message.startsWith("CALL")) {
                String[] parts = message.split(" ");
                if (parts.length == 3) {
                    String targetId = parts[1]; // ID của client đích
                    String senderId = session.getId();

                    // Gửi tín hiệu gọi đến client đích
                    Session targetSession = clients.get(targetId);
                    if (targetSession != null && targetSession.isOpen()) {
                        String callMessage = "INCOMING_CALL FROM " + senderId;
                        targetSession.getBasicRemote().sendText(callMessage);
                        System.out.println("Forwarded call from " + senderId + " to " + targetId);
                    } else {
                        String failureMessage = "CALL_FAILED: Client " + targetId + " not available";
                        session.getBasicRemote().sendText(failureMessage);
                        System.out.println("Call failed: Client " + targetId + " not available");
                    }
                } else {
                    System.out.println("Invalid CALL message format: " + message);
                }
            } else if (message.startsWith("ACCEPT_CALL")) {
                String[] parts = message.split(" ");
                String callerId = parts[1];
                Session callerSession = clients.get(callerId);

                if (callerSession != null && callerSession.isOpen()) {
                    callerSession.getBasicRemote().sendText("CALL_ACCEPTED");
                    System.out.println("Call accepted by " + session.getId() + ", notified caller: " + callerId);
                }
            } else if (message.startsWith("REJECT_CALL")) {
                String[] parts = message.split(" ");
                String callerId = parts[1];
                Session callerSession = clients.get(callerId);

                if (callerSession != null && callerSession.isOpen()) {
                    callerSession.getBasicRemote().sendText("CALL_REJECTED");
                    System.out.println("Call rejected by " + session.getId() + ", notified caller: " + callerId);
                }
            } else {
                System.out.println("Unknown message type: " + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                session.getBasicRemote().sendText("ERROR: An internal server error occurred.");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


    @OnClose
    public void onClose(Session session) {
        clients.remove(session.getId());
        System.out.println("Connection closed: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error in session " + session.getId() + ": " + throwable.getMessage());
    }
}
