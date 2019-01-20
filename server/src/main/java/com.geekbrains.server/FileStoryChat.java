package com.geekbrains.server;

import java.io.*;

import java.util.ArrayList;

public class FileStoryChat {

    public FileStoryChat(String msgAllLog) {
        this.msgAllLog = msgAllLog;
    }

    String msgAllLog;

    public static void newMessageAllClientsLog(String msgAllLog) {
        File log = new File("C:\\Users\\User\\Geek\\Core_1\\6_Clients_Server_chat_Maven\\client\\src\\main\\java\\com\\geekbrains\\client\\ClientsLog\\Allhistory.txt");
        if (log.exists() == false) {
            System.out.println("We had to make a new All log file.");
            try {
                log.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(log, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.append(msgAllLog + "\n");
        out.close();
    }

    public static ArrayList<String> lastMsg(){// метод возвращающий список(List) сообщений из Лог файла
        File logRead = new File("C:\\Users\\User\\Geek\\Core_1\\6_Clients_Server_chat_Maven\\client\\src\\main\\java\\com\\geekbrains\\client\\ClientsLog\\Allhistory.txt");
        ArrayList<String> lastMsg = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(logRead))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                lastMsg.add(sCurrentLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lastMsg;
    }
}

/*public static void main(String[] args) {
        File file = new File("log.txt");
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            int x;
            while ((x = in.read()) != -1) {
                System.out.print((char) x);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }*/

      /*System.out.println("-----------------Читаем из файла:---------------");
        try (FileInputStream in = new FileInputStream(file)) {
            int x;
            while((x = in.read()) != -1){
                System.out.print((char) x);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println();
        System.out.println("-----------------Читаем через массив байт:---------------");
        byte[] arr = new byte[8];
        try (FileInputStream in = new FileInputStream(file)) {
            while((in.read(arr)) > 0){
                System.out.print(new String(arr));
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("-----------------Зписываем в файл:---------------");
        //byte[] arr = new byte[8];
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write("Helloвапыпаывап".getBytes());
        }catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("-----------------Читаем из файла с указанием кодировки любьые символы:---------------");
        try (InputStreamReader in = new InputStreamReader(new FileInputStream(file),StandardCharsets.UTF_8)) {
            int x;
            while((x = in.read()) != -1){
                System.out.print((char) x);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println();*/

