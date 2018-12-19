package com.geekbrains.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    private String nickname;
    private Server server; //для реализации рассылки всем клиент, запоминаем ссылку на сервак
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public String getNickname() {
        return nickname;
    }

    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> { // Слушает в отдельном потоке что присылает клиент
                try {
                    while (true) { // ждем в цикле данные для аутентификации
                        String msg = in.readUTF();
                        if (msg.startsWith("/auth ")) {
                            String[] tokens = msg.split("\\s");
                            String nick = server.getAuthService().getNicknameByLoginAndPassword(tokens[1], tokens[2]);
                            if (nick != null && !server.isNickBusy(nick)){
                                sendMsg("/authok " + nick);
                                server.subscribe(this); // добавили в список рассылки(subscribe)
                                nickname = nick;
                                break;
                            }
                        }
                    }
                    while (true) {
                        String msg = in.readUTF();
                        if (msg.equals("/end")) {
                            break;
                        }
                        server.broadcastMsg(nickname + ": " + msg);
                        System.out.println(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    disconnect();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendMsg(String msg){
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void disconnect(){
        server.unsubscribe(this); // отключаем клиента от рассылки когда он отваливается
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
