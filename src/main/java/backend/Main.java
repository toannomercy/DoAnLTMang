/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend;

/**
 *
 * @author htoan
 */

import frontend.formLogin;
import java.awt.EventQueue;

public class Main {

    public static void main(String[] args) {
        // Khởi chạy SignalingServerApp trên một luồng riêng
        Thread signalingServerThread = new Thread(() -> {
            try {
                SignalingServerApp.main(null);
            } catch (Exception e) {
                System.err.println("Lỗi khi chạy SignalingServerApp: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // Khởi chạy SimpleHttpServer trên một luồng riêng
        Thread httpServerThread = new Thread(() -> {
            try {
                SimpleHttpServer.main(null);
            } catch (Exception e) {
                System.err.println("Lỗi khi chạy SimpleHttpServer: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // Khởi chạy giao diện formLogin trên luồng chính
        Thread uiThread = new Thread(() -> {
            EventQueue.invokeLater(() -> {
                try {
                    formLogin loginFrame = new formLogin();
                    loginFrame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        // Bắt đầu các luồng
        signalingServerThread.start();
        httpServerThread.start();
        uiThread.start();
    }
}

