/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend;

/**
 *
 * @author htoan
 */

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SimpleHttpServer {

    public static void main(String[] args) throws IOException {
        // Tạo HTTP Server tại cổng 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        String baseDirectory = "D:\\WorkSpaces\\LTMang\\Nhom01_DoAnMonLapTrinhMang\\src\\main\\java\\frontend";

        // Định nghĩa handler cho các yêu cầu HTTP
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String uriPath = exchange.getRequestURI().getPath();
                String filePath = uriPath.equals("/") ? "webrtc.html" : uriPath.substring(1);
                Path path = Paths.get(baseDirectory, filePath);

                System.out.println("Request received: " + exchange.getRequestURI());
                if (Files.exists(path)) {
                    byte[] content = Files.readAllBytes(path);
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                    exchange.sendResponseHeaders(200, content.length);

                    OutputStream os = exchange.getResponseBody();
                    os.write(content);
                    os.close();
                    System.out.println("Response: 200 OK, Content-Length: " + content.length);
                } else {
                    System.err.println("File not found: " + path.toAbsolutePath());
                    String notFound = "404 Not Found";
                    exchange.sendResponseHeaders(404, notFound.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(notFound.getBytes());
                    os.close();
                    System.err.println("Response: 404 Not Found");
                }
            }
        });

        // Khởi chạy server
        server.setExecutor(null); // Sử dụng executor mặc định
        server.start();
        System.out.println("HTTP Server running on http://localhost:8080");
    }
}


