/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import backend.SignalingClient;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author justcallmemin
 */
public class ClientService {
    // Lấy tất cả tài khoản
    public static List<Object[]> getAllAccounts() {
        List<Object[]> accounts = new ArrayList<>();
        DBAccess dbAccess = new DBAccess();

        try {
            String query = "SELECT id, username FROM taikhoan";
            ResultSet rs = dbAccess.Query(query);
            while (rs.next()) {
                String accountId = rs.getString("id");
                String username = rs.getString("username");
                String status = SignalingClient.getUserStatus(accountId); // Trạng thái online/offline từ Signaling Server

                Object[] row = new Object[3];
                row[0] = accountId;
                row[1] = username;
                row[2] = status;
                accounts.add(row);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbAccess.close();
        }

        return accounts;
    }

    // Lấy tất cả tài khoản ngoại trừ người dùng hiện tại
    public static List<Object[]> getAllAccountsExcept(String currentUserId) {
        List<Object[]> accounts = new ArrayList<>();
        DBAccess dbAccess = new DBAccess();

        try {
            String query = "SELECT id, username FROM taikhoan WHERE id != '" + currentUserId + "'";
            ResultSet rs = dbAccess.Query(query);
            while (rs.next()) {
                String accountId = rs.getString("id");
                String username = rs.getString("username");
                String status = SignalingClient.getUserStatus(accountId); // Trạng thái online/offline từ Signaling Server

                Object[] row = new Object[3];
                row[0] = accountId;
                row[1] = username;
                row[2] = status;
                accounts.add(row);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbAccess.close();
        }

        return accounts;
    }
}