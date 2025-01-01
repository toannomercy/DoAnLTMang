/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;
import java.sql.*;
import javax.swing.JOptionPane;
import java.util.logging.*;
/**
 *
 * @author htoan
 */
public class MyConnection {
    private static final Logger logger = Logger.getLogger(MyConnection.class.getName());
    public Connection getConnection() {
        Connection con = null;
        try {
            // Kết nối với MySQL
            
            // Nạp driver của MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Cấu hình kết nối (tắt SSL)
            String url = "jdbc:mysql://localhost:3306/quanlytaikhoan?useSSL=false";
            String username = "root"; // Tên đăng nhập của MySQL
            String password = "toan";
            
            // Tạo kết nối
            con = DriverManager.getConnection(url, username, password);
            
            // Kiểm tra nếu kết nối thành công
            if (con == null)
                JOptionPane.showMessageDialog(null, "Không thể kết nối tới cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            


            // Kết nối với SQL Server
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//
//            // Cấu hình kết nối SQL Server
//            String url = "jdbc:sqlserver://localhost:1433;databaseName=quanlytaikhoan;encrypt=false;trustServerCertificate=true";
//            String username = "sa"; // Tên đăng nhập SQL Server
//            String password = "toanlatui";
//            
//            // Tạo kết nối
//            con = DriverManager.getConnection(url, username, password);
//            
//            // Kiểm tra nếu kết nối thành công
//            if (con == null)
//                JOptionPane.showMessageDialog(null, "Không thể kết nối tới cơ sở dữ liệu SQL Server!", "Lỗi", JOptionPane.ERROR_MESSAGE);

        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Không tìm thấy driver SQL Server: ", e);
            JOptionPane.showMessageDialog(null, "Không tìm thấy driver SQL Server: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi kết nối: ", e);
            JOptionPane.showMessageDialog(null, "Lỗi kết nối SQL Server: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); 
        }
        return con;
    } 
}


