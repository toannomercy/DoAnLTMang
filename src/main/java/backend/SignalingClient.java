/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend;

import javax.websocket.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author htoan
 */
@ClientEndpoint
public class SignalingClient {
    private static final ConcurrentHashMap<String, String> userStatuses = new ConcurrentHashMap<>();
    private Session session;
    private final List<Object[]> callers = new ArrayList<>();
    private DefaultTableModel tableModel;

    public void setTableModel(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, new URI("ws://10.147.19.95:8081/signaling"));
            System.out.println("Connected to signaling server");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Message received: " + message);

        if (message.startsWith("STATUS_UPDATE")) {
            handleStatusUpdate(message);
        } else if (message.startsWith("INCOMING_CALL")) {
            handleIncomingCall(message);
        } else if (message.startsWith("ANSWER")) {
            handleAnswer(message);
        } else if (message.startsWith("CANDIDATE")) {
            handleCandidate(message);
        }
    }

    private void handleStatusUpdate(String message) {
    String[] parts = message.split(" ", 3);
    String userId = parts[1];
    String status = parts[2];

    if (tableModel != null) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).toString().equals(userId)) {
                    tableModel.setValueAt(status, i, 2); // Cập nhật trạng thái
                    return;
                }
            }
        });
    }
}

    private void handleIncomingCall(String message) {
        String[] parts = message.split(" ");
        String callerId = parts[1];

        if (tableModel != null) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (tableModel.getValueAt(i, 0).toString().equals(callerId)) {
                        tableModel.setValueAt("Đang gọi", i, 2); // Cập nhật trạng thái
                        return;
                    }
                }
            });
        }
    }

    private void handleAnswer(String message) {
        System.out.println("Answer received: " + message);
    }

    private void handleCandidate(String message) {
        System.out.println("Candidate received: " + message);
    }

    public void sendMessage(String message) {
        try {
            if (session != null && session.isOpen()) {
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getUserStatus(String userId) {
        return userStatuses.getOrDefault(userId, "offline");
    }
    
    public static void removeUserStatus(String userId) {
        userStatuses.remove(userId);
    }
    public static void updateUserStatus(String userId, String status) {
        userStatuses.put(userId, status);
    }
}