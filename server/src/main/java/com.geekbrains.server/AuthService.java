package com.geekbrains.server;

import java.sql.SQLException;

public interface AuthService {
    String getNicknameByLoginAndPassword (String login, String password) throws SQLException;
}
