package com.geekbrains.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class MainServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            Socket socket = serverSocket.accept();
            DataInputStream sc = new DataInputStream(socket.getInputStream());
            while (true){
                String msg = sc.readUTF();
                if(msg.equals("/end")){
                    break;
                }
                System.out.println(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server closed");
    }
}