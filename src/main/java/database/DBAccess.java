/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

/**
 *
 * @author htoan
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBAccess {
    private Connection con;
    private Statement stmt;

    // Constructor để khởi tạo kết nối và Statement
    public DBAccess() {
        try {
            MyConnection mycon = new MyConnection();
            con = mycon.getConnection(); // Lấy kết nối từ lớp MyConnection
            stmt = con.createStatement(); // Tạo Statement để thực thi SQL
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hàm Update: Thực hiện các câu lệnh INSERT, UPDATE, DELETE
    public int Update(String str) {
        try {
            int i = stmt.executeUpdate(str);
            return i; // Trả về số dòng bị ảnh hưởng
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Trả về -1 nếu có lỗi
        }
    }

    // Hàm Query: Thực hiện các câu lệnh SELECT
    public ResultSet Query(String str) {
        try {
            ResultSet rs = stmt.executeQuery(str); // Trả về kết quả dạng ResultSet
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Trả về null nếu có lỗi
        }
    }

    // Hàm để đóng kết nối
    public void close() {
        try {
            if (stmt != null) stmt.close();
            if (con != null) con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


