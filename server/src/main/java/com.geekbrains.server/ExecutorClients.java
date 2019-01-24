package com.geekbrains.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ExecutorClients {

    private static Server server;
    private static Socket socket;
    private static String nickname;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static ClientHandler clientHandler;

    public ExecutorClients (ClientHandler clientHandler, DataInputStream in, DataOutputStream out, Server server) {

        ExecutorService service = Executors.newFixedThreadPool(4);
        service.execute(new Runnable() {
            @Override
            public void run() {
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
                                clientHandler.sendMsg("/authok " + nick);
                                clientHandler.sendMsg("/loginok " + tokens[1]);//передаем логин на клиента после авторизации
                                //*** ниже реализация отправки последних десяти сообщений в чат толькочто авторизованного клиента
                                if (FileStoryChat.lastMsg().size() > 10) {
                                    for (int i = 10; i > 0; i--) {
                                        clientHandler.sendMsg(FileStoryChat.lastMsg().get(FileStoryChat.lastMsg().size()- i));
                                    }
                                } else {
                                    for (int i = 0; i < FileStoryChat.lastMsg().size(); i++) {
                                        clientHandler.sendMsg(FileStoryChat.lastMsg().get(i));
                                    }
                                }
                                //***
                                nickname = nick;
                                server.subscribe(clientHandler); // добавили в список рассылки(subscribe)
                                break;
                            }
                        }
                    }
                    while (true) {
                        String msg = in.readUTF();
                        if (msg.startsWith("/")) {// говорим что если сообщение начинается с/ - значит оно сллужебное и там уже обрабатываем, в зависимости от команды
                            if (msg.equals("/end")) {
                                clientHandler.sendMsg("/end");//кидаем клиенту енд в обратку чтобы на его стороне отработать(не выводить) алерт
                                break;
                            }
                            if (msg.startsWith("/w ")) { //начинаем проверку на приватное сообщение
                                String[] tokens = msg.split("\\s", 3); // разбиваем сообщение на 3 части по пробелу
                                server.privateMsg(clientHandler, tokens[1], tokens[2]);
                            }
                            //ниже реалиован вызов метода меняющего ник
                            if (msg.startsWith("/ch ")) { //начинаем проверку запроса на изменение ника
                                String[] tokens = msg.split("\\s", 2); // разбиваем сообщение на 2 части по пробелу
                                if (tokens[1].contains(" ")) {
                                    clientHandler.sendMsg("Ник не может содержать пробелов.");
                                    continue;
                                }
                                nickname = clientHandler.getNickname();
                                if (server.getAuthService().changeNick(nickname, tokens[1])) {
                                    clientHandler.sendMsg("/yournickis " + tokens[1]);//служебная команда которая позволяет серваку поменять ник у клиента
                                    clientHandler.sendMsg("Ваш ник изменен на: " + tokens[1]);
                                    nickname = tokens[1];
                                    server.broadcastClientsList();
                                } else {
                                    clientHandler.sendMsg("Не удалось изменить ник. Такой ник " + tokens[1] + " уже существует.");
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
                    clientHandler.disconnect();
                }
            }
        });
    }
//    public static void sendMsg(String msg) {
//        try {
//            out.writeUTF(msg);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public static void disconnect() {
//        server.unsubscribe(this); // отключаем клиента от рассылки когда он отваливается
//        try {
//            in.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            out.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}