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

    public void privateMsg(ClientHandler sender, String reciverNick, String msg) { // метод для рассылки приватных сообщений
        if (sender.getNickname().equals(reciverNick)) { //проверяем совпадают ли ники отправителя и получателя
            sender.sendMsg("заметка для себя: " + msg);
            return;
        }
        for (ClientHandler o : clients) {
            if (o.getNickname().equals(reciverNick)) {
                o.sendMsg("от " + sender.getNickname() + ": " + msg);//отправляем сообщение получателю с указанием от кого
                sender.sendMsg("для " + reciverNick + ": " + msg);//фиксируем сообщение для отправителя - что мол он отправил тому-то
                return;
            }
        }
        sender.sendMsg("Клиент " + reciverNick + " не найден");
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientsList();// обновляем рассылку клиентов при добавлении нового
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientsList();// обновляем рассылку клиентов при выходе клиента из чата
    }

    public boolean isNickBusy(String nickname){
        for (ClientHandler o : clients){
            if (o.getNickname().equals(nickname)){
                return true;
            }
        }
        return false;
    }

    public void broadcastClientsList() {
        StringBuilder sb = new StringBuilder(15 * clients.size());
        sb.append("/clients ");
        for (ClientHandler o : clients) {
            sb.append(o.getNickname()).append(" ");
        }
        sb.setLength(sb.length() - 1); //убираем лишний пробел в конце списка клиентов (который берется из строки выше)
        String out = sb.toString();
        for (ClientHandler o : clients) { // рассылаем всем клиентам список клиентов
            o.sendMsg(out);
        }
    }

    public boolean isAuth(ClientHandler clientHandler){
        for (ClientHandler o : clients){
            if (o.equals(clientHandler)){
                return true;
            }
        }
        return false;
    }

}
