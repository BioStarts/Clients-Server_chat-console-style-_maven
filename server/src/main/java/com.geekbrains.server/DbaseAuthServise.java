package com.geekbrains.server;

import java.sql.ResultSet;
import java.sql.SQLException;



public class DbaseAuthServise implements AuthService {

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) throws SQLException {
        System.out.println("Поиск произведен");
        //ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE login = 'lo' AND password = 'pass1'");
        ResultSet rs = MainServer.stmt.executeQuery("SELECT * FROM users WHERE login = 'login1' AND password = 'pass1'");
        System.out.println(rs.getString("nickname"));
        if (rs.next()){
            return rs.getString("nickname");//ник вытащить запросом
        }
        return null;
    }
}
