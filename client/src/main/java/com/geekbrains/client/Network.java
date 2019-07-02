package com.geekbrains.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network {
    private static Socket socket;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static Callback callOnMsgReceived;

    static { // сказали что нет стандартной реализации
        Callback empty = args -> {};
        callOnMsgReceived = empty;
        callOnAuthentificated = empty;
        callOnCloseConnection = empty;
        callOnException = empty;
    }

    public static void setCallOnMsgReceived(Callback callOnMsgReceived) {
        Network.callOnMsgReceived = callOnMsgReceived;
    }

    public static void setCallOnAuthentificated(Callback callOnAuthentificated) {
        Network.callOnAuthentificated = callOnAuthentificated;
    }

    public static void setCallOnException(Callback callOnException) {
        Network.callOnException = callOnException;
    }

    public static void setCallOnCloseConnection(Callback callOnCloseConnection) {
        Network.callOnCloseConnection = callOnCloseConnection;
    }

    private static Callback callOnAuthentificated;
    private static Callback callOnException;
    private static Callback callOnCloseConnection;

    public static void sendAuth(String login, String password) {
        try {
            if (socket == null || socket.isClosed()) { // при разрыве соединения переподключаемся
                connect();
            }
            out.writeUTF("/auth " + login + " " + password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void connect() {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread t = new Thread(() -> {
                try {
                    while (true) {//ждем потдверждения от сервака что мы ок, т.е. есть в базе (квазитокен)
                        String msg = in.readUTF();
                        if (msg.startsWith("/authok ")) {
                            String[] tokens = msg.split("\\s");
                            callOnAuthentificated.callback(tokens[1], tokens[2]); // из полученного от сервера подтверждения об авторизации вырезаем ник и логин
                            break;
                        }
                    }
                    while (true) {//все ок мы норм, ждем сообщений
                        String msg = in.readUTF();
                        if (msg.equals("/end")) {//если от сервака летит /end - летим в finaly минуя алерт о разрыве соедениния(т.к. сами его прервали командой /енд)
                            break;
                        }
                        callOnMsgReceived.callback(msg);
                    }
                } catch (IOException e) {
                    callOnException.callback("Соединение с сервером разорвано");//кидаем сообщение для авзова алерта что соедение с сервером разорвано
                } finally {
                    closeConnection();
                }
            });
            t.setDaemon(true);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean sendMsg(String msg) {
        try {
            out.writeUTF(msg);
            return true;//возвращаем 1 если отправили
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void closeConnection() {
        callOnCloseConnection.callback();
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
