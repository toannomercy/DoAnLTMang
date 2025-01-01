/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
/**
 *
 * @author htoan
 *
 */

@ServerEndpoint("/signaling")
public class SignalingServer {
private static final ConcurrentHashMap<String, List<String>> peerMLineOrders = new ConcurrentHashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ConcurrentHashMap<String, Session> clients = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> userStatuses = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> activeCalls = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("New connection: " + session.getId());
        try {
            for (String userId : clients.keySet()) {
                String status = userStatuses.getOrDefault(userId, "offline");
                session.getBasicRemote().sendText("STATUS_UPDATE " + userId + " " + status);
            }
        } catch (IOException e) {
            System.err.println("Error sending status updates on open: " + e.getMessage());
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message: " + message);

    try {
        if (message.trim().startsWith("{") && message.trim().endsWith("}")) {
            JsonNode jsonNode = objectMapper.readTree(message);
            String type = jsonNode.get("type").asText();

            switch (type) {
                case "register":
                    handleRegister(session, jsonNode.get("userId").asText());
                    break;
                case "call":
                case "offer":
                    handleCall(session, jsonNode);
                    break;
                case "answer":
                    handleAnswer(session, jsonNode);
                    break;
                case "candidate":
                    handleCandidate(session, jsonNode);
                    break;
                case "end_call":
                    handleEndCall(jsonNode);
                    break;
                case "status_update":
                    handleStatusUpdate(jsonNode);
                    break;
                default:
                    System.out.println("Unknown command type: " + type);
            }
        } else {
            String[] parts = message.split(" ", 2);
            String command = parts[0];
            String payload = parts.length > 1 ? parts[1] : "";

            switch (command.toUpperCase()) {
                case "REGISTER":
                    handleRegister(session, payload);
                    break;
                case "CALL":
                    handleCallNonJson(session, payload);
                    break;
                default:
                    System.out.println("Unknown non-JSON command: " + command);
            }
        }
    } catch (Exception e) {
        System.err.println("Error processing message: " + e.getMessage());
        e.printStackTrace();
    }
    }

    @OnClose
    public void onClose(Session session) {
        String userId = getUserIdBySession(session);

        if (userId != null) {
            clients.remove(userId);
            updateUserStatus(userId, "offline");
            endCall(userId);
            System.out.println("Connection closed for userId: " + userId);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error in session " + session.getId() + ": " + throwable.getMessage());
    }

    private void handleRegister(Session session, String userId) {
        if (clients.containsKey(userId)) {
            sendError(session, "REGISTER_FAILED", "UserId already registered: " + userId);
            return;
        }
        clients.put(userId, session);
        updateUserStatus(userId, "online");
        System.out.println("Registered userId: " + userId);
    }

    private void handleCall(Session senderSession, JsonNode jsonNode) {
    String targetId = jsonNode.get("target").asText();
    String senderId = getUserIdBySession(senderSession);

    // Đảm bảo thứ tự m-line cho cả người gọi và người nhận
    List<String> mLineOrder = new ArrayList<>();
    mLineOrder.add("video");
    mLineOrder.add("audio");

    peerMLineOrders.put(senderId, mLineOrder);
    peerMLineOrders.put(targetId, mLineOrder);

    if (activeCalls.containsKey(targetId)) {
        sendError(senderSession, "CALL_FAILED", "Target is busy");
        return;
    }

    activeCalls.put(senderId, targetId);
    activeCalls.put(targetId, senderId);
    updateUserStatus(senderId, "Calling " + targetId);
    updateUserStatus(targetId, "Incoming call from " + senderId);

    // Gửi yêu cầu START_CALL cho cả người gọi và người nhận
    forwardMessage(senderSession, targetId, jsonNode);
    sendStartCallNotification(senderId, targetId);
}
private void sendStartCallNotification(String senderId, String targetId) {
    try {
        // Gửi thông báo mở WebRTC cho người gọi (caller)
        String startCallMessageForCaller = "START_CALL " + senderId + " " + targetId;
        Session callerSession = clients.get(senderId);
        if (callerSession != null && callerSession.isOpen()) {
            callerSession.getBasicRemote().sendText(startCallMessageForCaller);
        }

        // Gửi thông báo mở WebRTC cho người nhận (target)
        String startCallMessageForTarget = "START_CALL " + targetId + " " + senderId;
        Session targetSession = clients.get(targetId);
        if (targetSession != null && targetSession.isOpen()) {
            targetSession.getBasicRemote().sendText(startCallMessageForTarget);
        }
    } catch (IOException e) {
        System.err.println("Error sending START_CALL notification: " + e.getMessage());
    }
}
    private void handleAnswer(Session senderSession, JsonNode jsonNode) {
    String targetId = jsonNode.get("target").asText();
    String senderId = getUserIdBySession(senderSession);

    // Check if m-line order matches
    if (!peerMLineOrders.containsKey(senderId) || !peerMLineOrders.containsKey(targetId)) {
        sendError(senderSession, "ANSWER_FAILED", "M-line order mismatch");
        return;
    }

    // Send answer to the target with proper m-line consistency
    forwardMessage(senderSession, targetId, jsonNode);
}

    private void handleCandidate(Session senderSession, JsonNode jsonNode) {
        String targetId = jsonNode.get("target").asText();
        forwardMessage(senderSession, targetId, jsonNode);
    }

    private void handleEndCall(JsonNode jsonNode) {
        String userId = jsonNode.get("userId").asText();
        endCall(userId);
    }

    private void handleStatusUpdate(JsonNode jsonNode) {
        String userId = jsonNode.get("userId").asText();
        String status = jsonNode.get("status").asText();
        updateUserStatus(userId, status);
    }

    private void handleCallNonJson(Session senderSession, String payload) {
        String[] parts = payload.split(" ");
        if (parts.length < 2) {
            sendError(senderSession, "CALL_FAILED", "Invalid call format");
            return;
        }
        String targetId = parts[0];
        String senderId = getUserIdBySession(senderSession);

        activeCalls.put(senderId, targetId);
        activeCalls.put(targetId, senderId);
        updateUserStatus(senderId, "Đang gọi " + targetId);
        updateUserStatus(targetId, "Đang được gọi bởi " + senderId);

        Session targetSession = clients.get(targetId);
        if (targetSession != null && targetSession.isOpen()) {
            try {
                targetSession.getBasicRemote().sendText("INCOMING_CALL " + senderId + " " + targetId);
                System.out.println("Forwarded CALL to " + targetId);
            } catch (IOException e) {
                System.err.println("Error forwarding call: " + e.getMessage());
            }
        } else {
            sendError(senderSession, "CALL_FAILED", "Target not available: " + targetId);
        }
    }

    private void endCall(String userId) {
        String counterpart = activeCalls.get(userId);
        if (counterpart != null) {
            activeCalls.remove(userId);
            activeCalls.remove(counterpart);

            updateUserStatus(userId, "online");
            updateUserStatus(counterpart, "online");

            System.out.println("Call ended between " + userId + " and " + counterpart);
        }
    }

    private void forwardMessage(Session senderSession, String targetId, JsonNode jsonNode) {
        Session targetSession = clients.get(targetId);
        if (targetSession != null && targetSession.isOpen()) {
            try {
                targetSession.getBasicRemote().sendText(jsonNode.toString());
                System.out.println("Forwarded message to " + targetId);
            } catch (IOException e) {
                System.err.println("Error forwarding message: " + e.getMessage());
            }
        } else {
            sendError(senderSession, "message_failed", "Target not available: " + targetId);
        }
    }

    private void updateUserStatus(String userId, String status) {
        userStatuses.put(userId, status);
        for (Session session : clients.values()) {
            try {
                session.getBasicRemote().sendText("STATUS_UPDATE " + userId + " " + status);
            } catch (IOException e) {
                System.err.println("Error updating status for " + userId + ": " + e.getMessage());
            }
        }
        System.out.println("Updated status: " + userId + " is now " + status);
    }

    private String getUserIdBySession(Session session) {
        return clients.entrySet().stream()
                .filter(entry -> entry.getValue().equals(session))
                .map(entry -> entry.getKey())
                .findFirst()
                .orElse(null);
    }

    private void sendError(Session session, String errorType, String message) {
        try {
            session.getBasicRemote().sendText(errorType + " " + message);
        } catch (IOException e) {
            System.err.println("Error sending error message: " + e.getMessage());
        }
    }
}