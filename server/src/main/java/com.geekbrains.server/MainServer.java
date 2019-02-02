package com.geekbrains.server;


import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MainServer {

    public static void main(String[] args) throws IOException {
        Logger globLogger = Logger.getLogger("");//при старте берем глоальный логгер
        globLogger.removeHandler(globLogger.getHandlers()[0]);//выкидываем у него хендлер
        Handler handler = new FileHandler("server-log%u.log", true);//запись в файл
        handler.setFormatter(new SimpleFormatter());//убираем формат fxml
        globLogger.addHandler(handler);//добавляем настроенный хендлер к логгеру

        new Server();
    }
}