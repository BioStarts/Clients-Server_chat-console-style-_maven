package com.geekbrains.client;

import java.io.*;

public class FileClientChatLog {

    public FileClientChatLog(String login, String msgLog) {
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
            out = new PrintWriter(new FileWriter(log, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.append(msgLog + "\n");
        out.close();
    }



}




    /*public static void main(String[] args) {
        File file = new File("log.txt");
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            int x;
            while((x = in.read()) != -1){
                System.out.print((char) x);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println();

        newMessageLog(login);
    }*/
/*File newFile = new File("C:\\Users\\User\\Geek\\Core_1\\6_Clients_Server_chat_Maven\\client\\src\\main\\java\\com\\geekbrains\\client\\ClientsLog\\log22.txt");
        try
        {
            boolean created = newFile.createNewFile();
            if(created)
                System.out.println("File has been created");
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }*/