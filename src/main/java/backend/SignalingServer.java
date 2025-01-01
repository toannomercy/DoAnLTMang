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
import java.util.concurrent.ConcurrentHashMap;
/**
 *
 * @author htoan
 *
 */

//@ServerEndpoint("/signaling")
//public class SignalingServer {
//    private static final ObjectMapper objectMapper = new ObjectMapper();
//    private static final ConcurrentHashMap<String, Session> clients = new ConcurrentHashMap<>();
//    private static final ConcurrentHashMap<String, String> userStatuses = new ConcurrentHashMap<>();
//
//    @OnOpen
//    public void onOpen(Session session) {
//        System.out.println("New connection: " + session.getId());
//
//        // Gửi trạng thái hiện tại của tất cả user tới client mới
//        try {
//            for (String userId : clients.keySet()) {
//                String status = userStatuses.getOrDefault(userId, "offline");
//                session.getBasicRemote().sendText("STATUS_UPDATE " + userId + " " + status);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @OnMessage
//public void onMessage(String message, Session session) {
//    System.out.println("Received message: " + message);
//
//    try {
//        if (message.trim().startsWith("{") && message.trim().endsWith("}")) {
//            // Nếu message là JSON hợp lệ
//            JsonNode jsonNode = objectMapper.readTree(message);
//            String type = jsonNode.get("type").asText();
//
//            switch (type) {
//                case "register":
//                    handleRegister(session, jsonNode.get("userId").asText());
//                    break;
//                case "call":
//                case "offer": // Xử lý "offer" giống như "call"
//                    handleCall(session, jsonNode);
//                    break;
//                case "answer":
//                    handleAnswer(session, jsonNode);
//                    break;
//                case "candidate":
//                    handleCandidate(session, jsonNode);
//                    break;
//                case "status_update":
//                    handleStatusUpdate(jsonNode);
//                    break;
//                default:
//                    System.out.println("Unknown command: " + type);
//            }
//        } else {
//            // Nếu message không phải JSON, xử lý như chuỗi đơn giản
//            String[] parts = message.split(" ", 2);
//            String command = parts[0];
//            String payload = parts.length > 1 ? parts[1] : "";
//
//            switch (command.toUpperCase()) {
//                case "REGISTER":
//                    handleRegister(session, payload);
//                    break;
//                case "CALL":
//                    handleCallNonJson(session, payload);
//                    break;
//                default:
//                    System.out.println("Unknown non-JSON command: " + command);
//            }
//        }
//    } catch (Exception e) {
//        e.printStackTrace();
//    }
//}
//
//    private void handleCallNonJson(Session senderSession, String payload) {
//        String[] parts = payload.split(" ");
//        if (parts.length < 2) {
//            sendError(senderSession, "CALL_FAILED", "Invalid call format");
//            return;
//        }
//        String targetId = parts[0];
//        String senderId = getUserIdBySession(senderSession);
//
//        // Cập nhật trạng thái người gọi và người nhận
//        updateUserStatus(senderId, "Đang gọi");
//        updateUserStatus(targetId, "Đang gọi");
//
//        Session targetSession = clients.get(targetId);
//
//        if (targetSession != null && targetSession.isOpen()) {
//            try {
//                targetSession.getBasicRemote().sendText("INCOMING_CALL " + senderId);
//                System.out.println("Forwarded CALL to " + targetId);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            sendError(senderSession, "CALL_FAILED", "Target not available: " + targetId);
//        }
//    }
//
//    @OnClose
//    public void onClose(Session session) {
//        String userId = getUserIdBySession(session);
//
//        if (userId != null) {
//            clients.remove(userId);
//            updateUserStatus(userId, "offline");
//            System.out.println("Connection closed for userId: " + userId);
//        } else {
//            System.out.println("Connection closed: " + session.getId());
//        }
//    }
//
//    @OnError
//    public void onError(Session session, Throwable throwable) {
//        System.err.println("Error in session " + session.getId() + ": " + throwable.getMessage());
//    }
//
//    private void handleRegister(Session session, String userId) {
//        if (clients.containsKey(userId)) {
//            sendError(session, "REGISTER_FAILED", "UserId already registered: " + userId);
//            return;
//        }
//
//        clients.put(userId, session);
//        updateUserStatus(userId, "online");
//        System.out.println("Registered userId: " + userId);
//    }
//
//    private void forwardMessage(Session senderSession, String targetId, JsonNode jsonNode) {
//        Session targetSession = clients.get(targetId);
//        if (targetSession != null && targetSession.isOpen()) {
//            try {
//                targetSession.getBasicRemote().sendText(jsonNode.toString());
//                System.out.println("Forwarded message to " + targetId);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            sendError(senderSession, "message_failed", "Target not available: " + targetId);
//        }
//    }
//
//    private void handleCall(Session senderSession, JsonNode jsonNode) {
//        String targetId = jsonNode.get("target").asText();
//        String senderId = getUserIdBySession(senderSession);
//
//        // Cập nhật trạng thái người gọi và người nhận
//        updateUserStatus(senderId, "Đang gọi");
//        updateUserStatus(targetId, "Đang gọi");
//
//        // Forward tin nhắn tới người nhận
//        forwardMessage(senderSession, targetId, jsonNode);
//    }
//
//    private void handleAnswer(Session senderSession, JsonNode jsonNode) {
//        String targetId = jsonNode.get("target").asText();
//        String sdpAnswer = jsonNode.get("sdp").asText();
//        forwardMessage(senderSession, targetId, jsonNode);
//    }
//
//    private void handleCandidate(Session senderSession, JsonNode jsonNode) {
//        String targetId = jsonNode.get("target").asText();
//        forwardMessage(senderSession, targetId, jsonNode);
//    }
//
//    private void handleStatusUpdate(JsonNode jsonNode) {
//        String userId = jsonNode.get("userId").asText();
//        String status = jsonNode.get("status").asText();
//        updateUserStatus(userId, status);
//    }
//
//    private void updateUserStatus(String userId, String status) {
//        userStatuses.put(userId, status);
//
//        // Thông báo trạng thái tới tất cả client
//        for (Session session : clients.values()) {
//            try {
//                session.getBasicRemote().sendText("STATUS_UPDATE " + userId + " " + status);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        System.out.println("Updated status: " + userId + " is now " + status);
//    }
//
//    private String getUserIdBySession(Session session) {
//        return clients.entrySet().stream()
//                .filter(entry -> entry.getValue().equals(session))
//                .map(entry -> entry.getKey())
//                .findFirst()
//                .orElse(null);
//    }
//
//    private void sendError(Session session, String errorType, String message) {
//        try {
//            session.getBasicRemote().sendText(errorType + " " + message);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    
//}
@ServerEndpoint("/signaling")
public class SignalingServer {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ConcurrentHashMap<String, Session> clients = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> userStatuses = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("New connection: " + session.getId());
        try {
            for (String userId : clients.keySet()) {
                String status = userStatuses.getOrDefault(userId, "offline");
                session.getBasicRemote().sendText("STATUS_UPDATE " + userId + " " + status);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                    case "status_update":
                        handleStatusUpdate(jsonNode);
                        break;
                    default:
                        System.out.println("Unknown command: " + type);
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
            e.printStackTrace();
        }
    }

    private void handleCallNonJson(Session senderSession, String payload) {
        String[] parts = payload.split(" ");
        if (parts.length < 2) {
            sendError(senderSession, "CALL_FAILED", "Invalid call format");
            return;
        }
        String targetId = parts[0];
        String senderId = getUserIdBySession(senderSession);

        if (senderId == null || targetId == null) {
            System.err.println("Invalid senderId or targetId");
            return;
        }

        updateUserStatus(senderId, "Đang gọi");
        updateUserStatus(targetId, "Đang gọi");

        Session targetSession = clients.get(targetId);

        if (targetSession != null && targetSession.isOpen()) {
            try {
                targetSession.getBasicRemote().sendText("INCOMING_CALL " + senderId);
                System.out.println("Forwarded CALL to " + targetId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            sendError(senderSession, "CALL_FAILED", "Target not available: " + targetId);
        }
    }

    private void handleRegister(Session session, String userId) {
        if (userId == null || userId.isEmpty()) {
            sendError(session, "REGISTER_FAILED", "Invalid userId");
            return;
        }

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

        // Cập nhật trạng thái người gọi và người nhận
        updateUserStatus(senderId, "Đang gọi");
        updateUserStatus(targetId, "Đang gọi");

        // Forward tin nhắn tới người nhận
        forwardMessage(senderSession, targetId, jsonNode);
    }

//    private void handleAnswer(Session senderSession, JsonNode jsonNode) {
//        String targetId = jsonNode.get("target").asText();
//        String sdpAnswer = jsonNode.get("sdp").asText();
//        forwardMessage(senderSession, targetId, jsonNode);
//    }

    private void handleCandidate(Session senderSession, JsonNode jsonNode) {
    String targetId = jsonNode.get("target").asText();
    if (targetId == null || !clients.containsKey(targetId)) {
        System.err.println("Invalid targetId for candidate");
        return;
    }

    forwardMessage(senderSession, targetId, jsonNode);
}

    
    private void forwardMessage(Session senderSession, String targetId, JsonNode jsonNode) {
        Session targetSession = clients.get(targetId);
        if (targetSession != null && targetSession.isOpen()) {
            try {
                targetSession.getBasicRemote().sendText(jsonNode.toString());
                System.out.println("Forwarded message to " + targetId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            sendError(senderSession, "message_failed", "Target not available: " + targetId);
        }
    }

    private void handleStatusUpdate(JsonNode jsonNode) {
        String userId = jsonNode.get("userId").asText();
        String status = jsonNode.get("status").asText();
        updateUserStatus(userId, status);
    }
    
    private void handleOffer(Session senderSession, JsonNode jsonNode) {
    String targetId = jsonNode.get("target").asText();
    if (targetId == null || !clients.containsKey(targetId)) {
        System.err.println("Invalid targetId for offer");
        return;
    }

    forwardMessage(senderSession, targetId, jsonNode);
}

private void handleAnswer(Session senderSession, JsonNode jsonNode) {
    String targetId = jsonNode.get("target").asText();
    if (targetId == null || !clients.containsKey(targetId)) {
        System.err.println("Invalid targetId for answer");
        return;
    }

    forwardMessage(senderSession, targetId, jsonNode);
}


    private void updateUserStatus(String userId, String status) {
        if (userId == null || status == null) {
            System.err.println("Invalid userId or status");
            return;
        }

        userStatuses.put(userId, status);

        for (Session session : clients.values()) {
            try {
                session.getBasicRemote().sendText("STATUS_UPDATE " + userId + " " + status);
            } catch (IOException e) {
                e.printStackTrace();
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
            e.printStackTrace();
        }
    }
}
