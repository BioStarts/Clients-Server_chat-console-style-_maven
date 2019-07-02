package com.geekbrains.server;

import java.sql.*;

public class SQLHandler {

        private static Connection connection;
        public static Statement stmt;
        private static PreparedStatement psgetNicknameByLoginAndPassword;
        private static PreparedStatement pschangeNick;

        public static boolean connect() { // открываем соединение для SQL
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:main.db");
                stmt = connection.createStatement();
                pschangeNick = connection.prepareStatement("UPDATE users SET nickname = ? WHERE nickname = ? ;");
                psgetNicknameByLoginAndPassword = connection.prepareStatement("SELECT * FROM users WHERE login = ? AND password = ? ;");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        public static void disconnect() {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                pschangeNick.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                psgetNicknameByLoginAndPassword.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

         static String getNicknameByLoginAndPassword(String login, String password){
            String nick = null;
            try {
                psgetNicknameByLoginAndPassword.setString(1, login);
                psgetNicknameByLoginAndPassword.setString(2, password);
                ResultSet rs = psgetNicknameByLoginAndPassword.executeQuery();
                System.out.println("Поиск по БД произведен");
                System.out.println("Авторизовался пользователь: " + rs.getString("nickname"));
                if (rs.next()){
                    return rs.getString("nickname");//ник вытащить запросом
                }
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return nick;
        }

        public static boolean changeNick (String nowNick, String newNick){ //метод для замены ника
            try {
                pschangeNick.setString(1, newNick);
                pschangeNick.setString(2, nowNick);
                pschangeNick.executeUpdate();
                System.out.println("Ник изменен");
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
}
