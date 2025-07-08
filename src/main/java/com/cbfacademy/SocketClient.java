package com.cbfacademy;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 4040);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String message = "Hello, World!";
            out.println(message);
            System.out.println("Sent message to server: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
