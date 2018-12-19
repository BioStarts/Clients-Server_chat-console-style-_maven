package com.geekbrains.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    TextField msgField, loginField;

    @FXML
    TextArea textArea;

    @FXML
    HBox msgPanel, authPanel;

    @FXML
    PasswordField passField;

    private boolean authentificated;
    private String nickname;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public void setAuthentificated(boolean authentificated) {
        this.authentificated = authentificated;
        authPanel.setVisible(!authentificated);
        authPanel.setManaged(!authentificated);
        msgPanel.setVisible(authentificated);
        msgPanel.setManaged(authentificated);
        if(!authentificated){
            nickname = ""; // обнуляем ник клиента при разрыве связи
        }
    }

    public void connect() {
        try {
            setAuthentificated(false);
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread t = new Thread(() -> {
                try {
                    while (true) {//ждем потдверждения от сервака что мы ок, т.е. есть в базе (квазитокен)
                        String msg = in.readUTF();
                        if(msg.startsWith("/authok ")) {
                            setAuthentificated(true);
                            nickname = msg.split("\\s")[1];
                            break;
                        }
                    }
                    while (true) {//все ок мы норм, ждем сообщений
                        String msg = in.readUTF();
                        textArea.appendText(msg + "\n");
                    }
                } catch (IOException e){
                    e.printStackTrace();
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

    public void sendMsg(ActionEvent actionEvent) {
        try {
            out.writeUTF(msgField.getText());
            msgField.clear();
            msgField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendAuth() {
        try {
            if(socket == null || socket.isClosed()){ // при разрыве соединения переподключаемся
                connect();
            }
            out.writeUTF("/auth " + loginField.getText() + " " + passField.getText());
            loginField.clear();
            passField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        setAuthentificated(false);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthentificated(false);
    }
}