package com.geekbrains.client;

import javafx.application.Platform;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

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
            } else {
                textArea.appendText(msg + "\n");
            }
        });
    }
}