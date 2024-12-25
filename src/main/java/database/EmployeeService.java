/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author htoan
 */
public class EmployeeService {
    public static List<Object[]> getOnlineEmployees() {
        List<Object[]> employees = new ArrayList<>();
        DBAccess dbAccess = new DBAccess();

        try {
            // Truy vấn danh sách nhân viên có trạng thái online
            String query = "SELECT id, full_name, role FROM employee WHERE status = 'online'";
            ResultSet rs = dbAccess.Query(query);
            while (rs.next()) {
                Object[] row = new Object[3];
                row[0] = rs.getInt("id");          // ID nhân viên
                row[1] = rs.getString("full_name"); // Tên đầy đủ
                row[2] = rs.getString("role");     // Vai trò
                employees.add(row);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbAccess.close();
        }

        return employees;
    }
}
