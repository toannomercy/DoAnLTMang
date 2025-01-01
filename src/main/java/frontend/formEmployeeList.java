/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package frontend;

import backend.SignalingClient;
import database.ClientService;
import database.DBAccess;
import database.EmployeeService;
import java.awt.Desktop;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 *
 * @author htoan
 */

public class formEmployeeList extends javax.swing.JFrame {
    private String userId;
    private SignalingClient signalingClient = new SignalingClient();
    /**
     * Creates new form formEmployeeList
     */
    public formEmployeeList(String userId) {
        this.userId = userId; 
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Danh sách nhân viên trực tuyến");

        // Đặt tiêu đề cột cho JTable
        tblEmployees.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "Họ và Tên", "Trạng Thái"}
        ));

        // Kết nối WebSocket
        signalingClient.setTableModel((DefaultTableModel) tblEmployees.getModel());
        signalingClient.connect();

        // Gửi tín hiệu REGISTER với userId thực tế
        signalingClient.sendMessage("REGISTER " + userId);

        // Tải dữ liệu ban đầu từ cơ sở dữ liệu
        loadEmployeesToTable();

        // Xử lý sự kiện đóng form
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Gửi tín hiệu STATUS_UPDATE offline trước khi đóng form
                signalingClient.sendMessage("STATUS_UPDATE " + userId + " offline");
                signalingClient.disconnect();
                System.out.println("Đóng form và cập nhật trạng thái offline cho: " + userId);
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

        jLabel1 = new javax.swing.JLabel();
        btnCall = new javax.swing.JButton();
        jpane = new javax.swing.JScrollPane();
        tblEmployees = new javax.swing.JTable();
        btnBack = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("CHỌN NHÂN VIÊN ĐỂ THỰC HIỆN CUỘC GỌI");

        btnCall.setText("GỌI");
        btnCall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCallActionPerformed(evt);
            }
        });

        tblEmployees.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jpane.setViewportView(tblEmployees);

        btnBack.setText("THOÁT");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(29, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnCall)
                        .addGap(18, 18, 18)
                        .addComponent(btnBack))
                    .addComponent(jpane, javax.swing.GroupLayout.PREFERRED_SIZE, 433, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28))
            .addGroup(layout.createSequentialGroup()
                .addGap(125, 125, 125)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jpane, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCall, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCallActionPerformed
        int selectedRow = tblEmployees.getSelectedRow();
    if (selectedRow >= 0) {
        String targetId = tblEmployees.getValueAt(selectedRow, 0).toString(); // ID người nhận
        try {
            // Gửi tín hiệu CALL tới server signaling
            signalingClient.sendMessage("CALL " + targetId + " " + userId);
            System.out.println("Đang gọi tới: " + targetId);

            // Hiển thị trạng thái chờ phản hồi
            JOptionPane.showMessageDialog(this, 
                "Đang chờ phản hồi từ: " + targetId, 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Không thể gửi tín hiệu gọi.", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(this, 
            "Vui lòng chọn một tài khoản để gọi!", 
            "Thông báo", 
            JOptionPane.WARNING_MESSAGE);
    }
    }//GEN-LAST:event_btnCallActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // Xóa trạng thái khỏi Map
        SignalingClient.removeUserStatus(userId);

        // Đóng form hiện tại và quay lại formHome
        this.dispose();
        new formHome().setVisible(true);
    }//GEN-LAST:event_btnBackActionPerformed

    private void startVideoCall(int employeeId, String employeeName) {
        JOptionPane.showMessageDialog(this, "Gửi tín hiệu gọi video tới nhân viên: " + employeeName, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        // Tích hợp WebSocket hoặc logic gọi video tại đây
    }

    public void loadEmployeesToTable() {
        DefaultTableModel model = (DefaultTableModel) tblEmployees.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        // Lấy danh sách tất cả tài khoản ngoại trừ userId hiện tại
        List<Object[]> accounts = ClientService.getAllAccountsExcept(userId);

        for (Object[] account : accounts) {
            String accountId = account[0].toString(); // ID tài khoản
            String username = account[1].toString(); // Tên người dùng
            String status = SignalingClient.getUserStatus(accountId); // Lấy trạng thái từ Map

            // Thêm tài khoản vào bảng với trạng thái
            model.addRow(new Object[]{accountId, username, status});
        }
    }
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
            java.util.logging.Logger.getLogger(formEmployeeList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formEmployeeList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formEmployeeList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formEmployeeList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
            new formLogin().setVisible(true);
        }
    });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnCall;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jpane;
    private javax.swing.JTable tblEmployees;
    // End of variables declaration//GEN-END:variables
}
