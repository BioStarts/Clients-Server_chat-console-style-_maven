package com.geekbrains.client;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class History {
    private static PrintWriter out;

    private static String getHistoryFilenameByLogin(String login) {
        return "history/history_" + login + ".txt";
        //return "C:\\Users\\User\\Geek\\Core_1\\6_Clients_Server_chat_Maven\\client\\src\\main\\java\\com\\geekbrains\\client\\ClientsLog\\history\\history_" + login + ".txt";
    }

    public static void start(String login) {
        try {
            out = new PrintWriter(new FileOutputStream(getHistoryFilenameByLogin(login), true), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        if (out != null) {
            out.close();
        }
    }

    public static void writeLine(String msg) {
        out.println(msg);
    }

    public static String getLast100LinesOfHistory(String login) {
        if (!Files.exists(Paths.get(getHistoryFilenameByLogin(login)))) { // если лог файла не существует отправляем пустую строку
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try {
            List<String> historyLines = Files.readAllLines(Paths.get(getHistoryFilenameByLogin(login)));//считываем все строчки с файла
            int startPosition = 0;
            if (historyLines.size() > 100) {
                startPosition = historyLines.size() - 100;
            }
            for (int i = startPosition; i < historyLines.size(); i++) {
                sb.append(historyLines.get(i)).append(System.lineSeparator());//лайнсепаратор - добавили перенос строки для любой ОС
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}

    /*   ------------  Ниже 1е рабочее решение  ------------

    public History(String login, String msgLog) {
        this.login = login;
        this.msgLog = msgLog;
    }

    String login;
    String msgLog;

    File log = new File("/log.txt");

    public static void newMessageLog(String login, String msgLog) {
        File log = new File("C:\\Users\\User\\Geek\\Core_1\\6_Clients_Server_chat_Maven\\client\\src\\main\\java\\com\\geekbrains\\client\\ClientsLog\\history_" + login +".txt");
        if (log.exists() == false) {
            System.out.println("We had to make a new file.");
            try {
                log.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(log, true), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.append(msgLog + "\n");
        out.close();
    }*/





