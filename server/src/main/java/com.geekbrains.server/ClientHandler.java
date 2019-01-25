package com.geekbrains.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import static java.lang.System.currentTimeMillis;

public class ClientHandler {

    private String nickname;
    private Server server; //для реализации рассылки всем клиент, запоминаем ссылку на сервак
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private long currentTime;//Заводим поле для фиксации времени юзера, с момента попытки авторизации
    private ExecutorService executorService;

    public String getNickname() {
        return nickname;
    }

    public long getCurrentTime() {//делаем геттер чтобы на сервере можно было узнавать о времени активной работы юзера
        return currentTime;
    }

    public ClientHandler(Server server, Socket socket, ExecutorService executorService) {
        this.server = server;
        this.socket = socket;
        this.currentTime = currentTimeMillis();//засекаем время работы юзера
        this.executorService = executorService;
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    //new Thread(() -> { // Слушает в отдельном потоке что присылает клиент
                try {
                    while (true) { // ждем в цикле данные для аутентификации
                        String msg = in.readUTF();
                        if (msg.startsWith("/auth ")) {
                            String[] tokens = msg.split("\\s");
                            if (tokens.length != 3) {//для того чтобы при отправке пользователем пустых полей логина или пароля сервак не крашился
                                continue;
                            }
                            String nick = server.getAuthService().getNicknameByLoginAndPassword(tokens[1], tokens[2]);
                            //***//
                            if (nick != null && !server.isNickBusy(nick)) {
                                sendMsg("/authok " + nick);
                                sendMsg("/loginok " + tokens[1]);//передаем логин на клиента после авторизации
                                //*** ниже реализация отправки последних десяти сообщений в чат толькочто авторизованного клиента
                                if (FileStoryChat.lastMsg().size() > 10) {
                                    for (int i = 10; i > 0; i--) {
                                        sendMsg(FileStoryChat.lastMsg().get(FileStoryChat.lastMsg().size()- i));
                                    }
                                } else {
                                    for (int i = 0; i < FileStoryChat.lastMsg().size(); i++) {
                                        sendMsg(FileStoryChat.lastMsg().get(i));
                                    }
                                }
                                //***
                                nickname = nick;
                                server.subscribe(ClientHandler.this); // добавили в список рассылки(subscribe)
                                break;
                            }
                        }
                    }
                    while (true) {
                        String msg = in.readUTF();
                        if (msg.startsWith("/")) {// говорим что если сообщение начинается с/ - значит оно сллужебное и там уже обрабатываем, в зависимости от команды
                            if (msg.equals("/end")) {
                                sendMsg("/end");//кидаем клиенту енд в обратку чтобы на его стороне отработать(не выводить) алерт
                                break;
                            }
                            if (msg.startsWith("/w ")) { //начинаем проверку на приватное сообщение
                                String[] tokens = msg.split("\\s", 3); // разбиваем сообщение на 3 части по пробелу
                                server.privateMsg(ClientHandler.this, tokens[1], tokens[2]);
                            }
                            //ниже реалиован вызов метода меняющего ник
                            if (msg.startsWith("/ch ")) { //начинаем проверку запроса на изменение ника
                                String[] tokens = msg.split("\\s", 2); // разбиваем сообщение на 2 части по пробелу
                                if (tokens[1].contains(" ")) {
                                    sendMsg("Ник не может содержать пробелов.");
                                    continue;
                                }
                                if (server.getAuthService().changeNick(ClientHandler.this.nickname, tokens[1])) {
                                    sendMsg("/yournickis " + tokens[1]);//служебная команда которая позволяет серваку поменять ник у клиента
                                    sendMsg("Ваш ник изменен на: " + tokens[1]);
                                    ClientHandler.this.nickname = tokens[1];
                                    server.broadcastClientsList();
                                } else {
                                    sendMsg("Не удалось изменить ник. Такой ник " + tokens[1] + " уже существует.");
                                }
                            }
                        } else {
                            server.broadcastMsg(nickname + ": " + msg);
                            FileStoryChat.newMessageAllClientsLog(nickname + ": " + msg);// отправляем общее сообщение в историю - лог файл
                            System.out.println(msg);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    disconnect();
                }
            //}).start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

//            new Thread(() -> { // Слушает в отдельном потоке что присылает клиент
//                try {
//                    while (true) { // ждем в цикле данные для аутентификации
//                        String msg = in.readUTF();
//                        if (msg.startsWith("/auth ")) {
//                            String[] tokens = msg.split("\\s");
//                            if (tokens.length != 3) {//для того чтобы при отправке пользователем пустых полей логина или пароля сервак не крашился
//                                continue;
//                            }
//                            String nick = server.getAuthService().getNicknameByLoginAndPassword(tokens[1], tokens[2]);
//                            //***//
//                            if (nick != null && !server.isNickBusy(nick)) {
//                                sendMsg("/authok " + nick);
//                                sendMsg("/loginok " + tokens[1]);//передаем логин на клиента после авторизации
//                                //*** ниже реализация отправки последних десяти сообщений в чат толькочто авторизованного клиента
//                                if (FileStoryChat.lastMsg().size() > 10) {
//                                    for (int i = 10; i > 0; i--) {
//                                        sendMsg(FileStoryChat.lastMsg().get(FileStoryChat.lastMsg().size()- i));
//                                    }
//                                } else {
//                                    for (int i = 0; i < FileStoryChat.lastMsg().size(); i++) {
//                                        sendMsg(FileStoryChat.lastMsg().get(i));
//                                    }
//                                }
//                                //***
//                                nickname = nick;
//                                server.subscribe(this); // добавили в список рассылки(subscribe)
//                                break;
//                            }
//                        }
//                    }
//                    while (true) {
//                        String msg = in.readUTF();
//                        if (msg.startsWith("/")) {// говорим что если сообщение начинается с/ - значит оно сллужебное и там уже обрабатываем, в зависимости от команды
//                            if (msg.equals("/end")) {
//                                sendMsg("/end");//кидаем клиенту енд в обратку чтобы на его стороне отработать(не выводить) алерт
//                                break;
//                            }
//                            if (msg.startsWith("/w ")) { //начинаем проверку на приватное сообщение
//                                String[] tokens = msg.split("\\s", 3); // разбиваем сообщение на 3 части по пробелу
//                                server.privateMsg(this, tokens[1], tokens[2]);
//                            }
//                            //ниже реалиован вызов метода меняющего ник
//                            if (msg.startsWith("/ch ")) { //начинаем проверку запроса на изменение ника
//                                String[] tokens = msg.split("\\s", 2); // разбиваем сообщение на 2 части по пробелу
//                                if (tokens[1].contains(" ")) {
//                                    sendMsg("Ник не может содержать пробелов.");
//                                    continue;
//                                }
//                                if (server.getAuthService().changeNick(this.nickname, tokens[1])) {
//                                    sendMsg("/yournickis " + tokens[1]);//служебная команда которая позволяет серваку поменять ник у клиента
//                                    sendMsg("Ваш ник изменен на: " + tokens[1]);
//                                    this.nickname = tokens[1];
//                                    server.broadcastClientsList();
//                                } else {
//                                    sendMsg("Не удалось изменить ник. Такой ник " + tokens[1] + " уже существует.");
//                                }
//                            }
//                        } else {
//                            server.broadcastMsg(nickname + ": " + msg);
//                            FileStoryChat.newMessageAllClientsLog(nickname + ": " + msg);// отправляем общее сообщение в историю - лог файл
//                            System.out.println(msg);
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    disconnect();
//                }
//            }).start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void disconnect() {
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
