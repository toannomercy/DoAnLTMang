/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend;

import frontend.formIncomingCall;
import java.awt.Desktop;
import javax.websocket.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
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

    public void disconnect() {
        try {
            if (session != null && session.isOpen()) {
                session.close();
                System.out.println("Đã ngắt kết nối WebSocket.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//            session = container.connectToServer(this, new URI("ws://10.147.19.95:8081/signaling"));
            session = container.connectToServer(this, new URI("ws://10.147.19.217:8081/signaling"));
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
    } else if (message.startsWith("START_CALL")) {
        handleStartCall(message);
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
        String callerId = parts[1]; // ID of the caller
        String targetId = parts[2]; // ID of the receiver

        SwingUtilities.invokeLater(() -> {
            // Show incoming call form
            formIncomingCall incomingCallForm = new formIncomingCall(callerId, targetId, this);
            incomingCallForm.setVisible(true);
        });
    }

    private void handleStartCall(String message) {
    String[] parts = message.split(" ");
    String callerId = parts[1]; // ID của người gọi
    String targetId = parts[2]; // ID của người nhận

    // Đảm bảo mở giao diện WebRTC cho cả người gọi và người nhận
    javax.swing.SwingUtilities.invokeLater(() -> {
        openWebRTCInterface(callerId, targetId);  // Cho người gọi
        openWebRTCInterface(targetId, callerId);  // Cho người nhận
    });
}

    private void openWebRTCInterface(String callerId, String targetId) {
        try {
            String url = "http://localhost:8080/webrtc.html?callerId=" + callerId + "&targetId=" + targetId;
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Không thể mở giao diện gọi.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
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
