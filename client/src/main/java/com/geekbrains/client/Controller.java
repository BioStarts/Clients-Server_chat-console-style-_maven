package com.geekbrains.client;

import javafx.application.Platform;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;

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

    @FXML
    ListView<String> clientsList;

    private boolean authentificated;
    private String nickname;
    private String login;

    public void setAuthentificated(boolean authentificated) {
        this.authentificated = authentificated;
        authPanel.setVisible(!authentificated);
        authPanel.setManaged(!authentificated);
        msgPanel.setVisible(authentificated);
        msgPanel.setManaged(authentificated);
        clientsList.setVisible(authentificated);
        clientsList.setManaged(authentificated);
        if (!authentificated) {
            nickname = ""; // обнуляем ник клиента при разрыве связи
            History.stop();//останавливаем работу с файлом при разрыве соединения
            textArea.clear();//чистим историю с окна при разлогине юзера
        }
    }

    public void sendAuth() {
        Network.sendAuth(loginField.getText(), passField.getText());
        loginField.clear();
        passField.clear();
    }


    public void sendMsg() {
        if (Network.sendMsg(msgField.getText())) {//если отправка прошла то очищаем поле и кидаем фокус на поле
            msgField.clear();
            msgField.requestFocus();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthentificated(false);
        //повешали на список прослушку события что кто-то на него кликает
        clientsList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String nickname = clientsList.getSelectionModel().getSelectedItem();
                msgField.setText("/w " + nickname + " ");
                msgField.requestFocus();
                msgField.selectEnd();
            }
            if ((event.getButton() == MouseButton.SECONDARY) & (clientsList.getSelectionModel().getSelectedItem().equals(nickname))) {//на клик правой кнопки меняем ник
                System.out.println(nickname);
                System.out.println(clientsList.getSelectionModel().getSelectedItem());
                msgField.setText("/ch ");
                msgField.requestFocus();
                msgField.selectEnd();
            }
        });
        linkCallbacks();
    }

    public void showAlert(String msg) {//метод для бросания алертов
        Platform.runLater(() -> {// в тред javaFX закатываем чтоб работало
            Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
            alert.showAndWait();
        });
    }

    public void linkCallbacks() {
        Network.setCallOnException(args -> showAlert(args[0].toString()));
        Network.setCallOnCloseConnection(args -> setAuthentificated(false));
        Network.setCallOnAuthentificated(args -> {
            setAuthentificated(true);
            nickname = args[0].toString();
            login = args[1].toString();
            textArea.clear();
            textArea.appendText(History.getLast100LinesOfHistory(login));//Печатаем крайние 100 сообщений из истори клиента при авторизации
            History.start(login);//открываем соединение с файлом
        });
        Network.setCallOnMsgReceived(args -> {
            String msg = args[0].toString();
            if (msg.startsWith("/")) { //говорим что если летит что-то начинающиеся на / это команда
                if (msg.startsWith("/clients ")) {
                    Platform.runLater(() -> {// прокидывает задачу по обновлению списка из этого трэда в трэд javaFX
                        clientsList.getItems().clear();//отчистили старый список клиентов
                        String[] tokens = msg.split("\\s");//парсим список заполняем массив листа(списка)
                        for (int i = 1; i < tokens.length; i++) {
                            clientsList.getItems().add(tokens[i]);
                        }
                    });
                }
                if (msg.startsWith("/yournickis ")) { //обновили ник после его изменения
                    nickname = msg.split("\\s")[1];
                }
                /*if (msg.startsWith("/loginok ")){ //получили логин после авторизации
                    login = msg.split("\\s")[1];
                }*/
            } else {
                textArea.appendText(msg + "\n");
                History.writeLine(msg);// передаю сообщение в класс хранящий историю клиента
                //History.newMessageLog(login, msg);// передаю логин и сообщение в класс хранящий историю клиента
            }
        });
    }
}