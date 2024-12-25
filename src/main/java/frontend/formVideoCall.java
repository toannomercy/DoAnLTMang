/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package frontend;
import java.io.File;
import java.net.URI;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.embed.swing.JFXPanel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
/**
 *
 * @author htoan
 */
public class formVideoCall extends javax.swing.JFrame {

    /**
     * Creates new form formVideoCall
     */


    private JFXPanel jfxVideoPanel;
    private Session signalingSession;
    public formVideoCall() {
        initComponents();
        setTitle("Cuộc gọi video");
        setLocationRelativeTo(null); // Hiển thị giữa màn hình
        connectToSignalingServer();
        initializeVideoPanel();
    }
    private void connectToSignalingServer() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            signalingSession = container.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    signalingSession = session;
                    System.out.println("Connecting to Signaling Server.");
                    // Gửi tín hiệu bắt đầu gọi
                    sendMessage("CALL_REQUEST");
                }

                @Override
                public void onClose(Session session, CloseReason closeReason) {
                    System.out.println("Canceling connection to Signaling Server: " + closeReason.getReasonPhrase());
                }
            }, new URI("ws://localhost:8081/signaling")); // URL của Signaling Server
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối Signaling Server", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void sendMessage(String message) {
        try {
            if (signalingSession != null && signalingSession.isOpen()) {
                signalingSession.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initializeVideoPanel() {
        jfxVideoPanel = new JFXPanel();
        videoPanel.setLayout(new java.awt.BorderLayout());
        videoPanel.add(jfxVideoPanel, java.awt.BorderLayout.CENTER);

        Platform.runLater(() -> {
            try {
                WebView webView = new WebView();
                WebEngine webEngine = webView.getEngine();
                String webRtcUrl = "localhost:8080/webrtc.html";
                System.out.println("Loading URL: " + webRtcUrl);
                webEngine.load(webRtcUrl);
//                    String fileUrl = new File("D:\\WorkSpaces\\LTMang\\Nhom01_DoAnMonLapTrinhMang\\src\\main\\java\\frontend\\webrtc.html").toURI().toString();
//                    System.out.println("Loading URL: " + fileUrl);
//                    webEngine.load(fileUrl);


                // Thêm WebView vào JFXPanel
                jfxVideoPanel.setScene(new Scene(webView));
                System.out.println("WebRTC interface loaded into JFXPanel.");

                // Theo dõi trạng thái WebEngine
                webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                    System.out.println("WebEngine state changed from " + oldState + " to " + newState);
                    if (newState == javafx.concurrent.Worker.State.FAILED) {
                        System.err.println("WebEngine failed to load.");
                        Throwable exception = webEngine.getLoadWorker().getException();
                        if (exception != null) {
                            System.err.println("Reason for failure: " + exception.getMessage());
                            exception.printStackTrace();
                        } else {
                            System.err.println("No specific exception was reported.");
                        }
                    }
                    if (newState == javafx.concurrent.Worker.State.CANCELLED) {
                        System.err.println("WebEngine load was cancelled.");
                        // Ghi thêm log khi bị hủy
                        Throwable exception = webEngine.getLoadWorker().getException();
                        if (exception != null) {
                            System.err.println("Cancellation reason: " + exception.getMessage());
                            exception.printStackTrace();
                        }
                    }
                    if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                        System.out.println("WebEngine successfully loaded content.");
                    }
                });

            } catch (Exception e) {
                System.err.println("Exception occurred in Platform.runLater:");
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Không thể tải giao diện gọi video. Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    





    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        videoPanel = new javax.swing.JPanel();
        btnMute = new javax.swing.JButton();
        btnToggleVideo = new javax.swing.JButton();
        btnEndCall = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        videoPanel.setPreferredSize(new java.awt.Dimension(800, 600));

        btnMute.setText("Bật Tắt Mic");
        btnMute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMuteActionPerformed(evt);
            }
        });

        btnToggleVideo.setText("Bật Tắt Camera");
        btnToggleVideo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToggleVideoActionPerformed(evt);
            }
        });

        btnEndCall.setText("Kết thúc");
        btnEndCall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEndCallActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout videoPanelLayout = new javax.swing.GroupLayout(videoPanel);
        videoPanel.setLayout(videoPanelLayout);
        videoPanelLayout.setHorizontalGroup(
            videoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(videoPanelLayout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(btnMute)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 216, Short.MAX_VALUE)
                .addComponent(btnToggleVideo)
                .addGap(206, 206, 206)
                .addComponent(btnEndCall)
                .addGap(48, 48, 48))
        );
        videoPanelLayout.setVerticalGroup(
            videoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(videoPanelLayout.createSequentialGroup()
                .addContainerGap(558, Short.MAX_VALUE)
                .addGroup(videoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnMute, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, videoPanelLayout.createSequentialGroup()
                        .addGroup(videoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnToggleVideo, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEndCall, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(videoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(videoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMuteActionPerformed
        // Bật/tắt micro
        Platform.runLater(() -> {
            JOptionPane.showMessageDialog(this, "Micro đã được bật/tắt", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });
    }//GEN-LAST:event_btnMuteActionPerformed

    private void btnToggleVideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToggleVideoActionPerformed
        // Bật/tắt camera
        Platform.runLater(() -> {
            JOptionPane.showMessageDialog(this, "Camera đã được bật/tắt", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });
    }//GEN-LAST:event_btnToggleVideoActionPerformed

    private void btnEndCallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEndCallActionPerformed
        sendMessage("END_CALL"); // Gửi tín hiệu kết thúc
        JOptionPane.showMessageDialog(this, "Cuộc gọi đã kết thúc.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        new formEmployeeList().setVisible(true);
        this.dispose(); // Đóng form
    }//GEN-LAST:event_btnEndCallActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(formVideoCall.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formVideoCall.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formVideoCall.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formVideoCall.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new formVideoCall().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEndCall;
    private javax.swing.JButton btnMute;
    private javax.swing.JButton btnToggleVideo;
    private javax.swing.JPanel videoPanel;
    // End of variables declaration//GEN-END:variables
}
