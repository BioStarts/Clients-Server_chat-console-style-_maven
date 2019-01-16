package com.geekbrains.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MainServer {

    private static Connection connection;
    private static Statement stmt;

    public static void connect() throws ClassNotFoundException, SQLException { // открываем соединение для SQL
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:main.db");
        stmt = connection.createStatement();
    }

    public static void disconnect() {
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //-------------sql:
        try {
            connect();
            System.out.println("Подключились к SQL");
            stmt.executeUpdate("");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }

        //-------------сервер:
        new Server();
    }

    private static void clearTable() throws SQLException {
        stmt.executeUpdate("DELETE FROM users;");
    }

    private static void deleteUser() throws SQLException {
        stmt.executeUpdate("DELETE FROM users WHERE id = 3;");
    }

    private static void updateUser() throws SQLException {
        stmt.executeUpdate("UPDATE users SET nickname = 'lola' WHERE id = 2 ;");
    }

    private static void insertUser() throws SQLException {
        stmt.executeUpdate("INSERT INTO users (login, password, nickname) VALUES ('lol2', 'lolo2', 'lol2)');");
    }
}