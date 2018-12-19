package com.geekbrains.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
    private Vector<ClientHandler> clients;

    public AuthService getAuthService() {
        return authService;
    }

    private AuthService authService;

    public Server() {
        clients = new Vector<>();
        authService = new SimpleAuthService();
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            System.out.println("Сервер запущен на порту 8189");
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(this, socket); //отдали в клиентхендлер при создании ссылку на себя (для рассылки broadcastMsg) и сокет (для соединения)
                System.out.println("Подключился новый клиент");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Сервер завершил свою работу");
    }

    public void broadcastMsg(String msg) { // метод для рассылки по всей коллекции
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public boolean isNickBusy(String nickname){
        for (ClientHandler o : clients){
            if (o.getNickname().equals(nickname)){
                return true;
            }
        }
        return false;
    }


}
