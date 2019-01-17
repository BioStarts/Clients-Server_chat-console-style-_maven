package com.geekbrains.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import static java.lang.System.currentTimeMillis;

public class Server {
    private Vector<ClientHandler> clients;
    private Vector<ClientHandler> notAuthClients; //Отдельно заводим вектор для неавторизованных (всех) клиентов

    public AuthService getAuthService() {
        return authService;
    }

    private AuthService authService;
    private final int DISCONNECTION_TIMEOUT = 30000;//временной интервал для чистки неавторизованных юзеров (если больше 30 секунд - определенный клиентхендлер не авторизован - дисконнектим)


    public Server() {
        clients = new Vector<>();
        notAuthClients = new Vector<>();//Инициализируем вектор для хранения всех юзеров неавторизованных
        Thread checkThread = new Thread(() -> {//Запускаем поток предназначенный для проверки и чистки соединения с неавторизованными юзерами
            try {
                while (true) {
                    Thread.sleep(10000); //тормозим поток на 10 секунд - т.е. раз в 10 секунд проходимся по циклу и зачищаем
                    for (ClientHandler o : notAuthClients//бежим по вектору неавторизованных юзеров
                    ) {
                        if (currentTimeMillis() - o.getCurrentTime() > DISCONNECTION_TIMEOUT) {//если юзер неавторизованн более 30 секунд прерываем соединение(время юзера засекаем в
                            // клиент хэндлере и геттером тянем сюда для сравнения) Суть:currentTimeMillis()- поле сохраняется время старта экземпляра ClientHandler(то есть, время,
                            // когда клиент подключился к серверу) вычисляя разницу между текущим временем и сохраненным временем старта, можно понять, сколько клиент висит в неавторизованном состоянии
                            o.disconnect();
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
        checkThread.setDaemon(true);
        checkThread.start();

        //authService = new SimpleAuthService();
        authService = new DbaseAuthServise();
        //***//
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            System.out.println("Сервер запущен на порту 8189");
            while (true) {
                Socket socket = serverSocket.accept();
                notAuthClients.add(new ClientHandler(this, socket)); //Изначально добавляем всех новых клиентов в список неавторизованных юзеров(магия/актуализация списка ниже)
                //new ClientHandler(this, socket); //отдали в клиентхендлер при создании ссылку на себя (для рассылки broadcastMsg) и сокет (для соединения)
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

    //*** - для замены ника реализация метода
    public void changeNick(String nick, String newnick) { // метод для рассылки приватных сообщений

    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        notAuthClients.remove(clientHandler);//Магия! При авторпизации юзера убираем его из списка неавторизованных клиентов
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
}
