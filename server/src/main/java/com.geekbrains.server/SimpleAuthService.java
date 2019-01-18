package com.geekbrains.server;

import java.util.ArrayList;
import java.util.List;

public class SimpleAuthService implements AuthService{

    private class Userdata{
        private String login;
        private String password;
        private String nickname;

        public Userdata(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }
    }

    private List<Userdata> users;

    public SimpleAuthService() {
        this.users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            users.add(new Userdata("login" + i, "pass" + i, "nick" + i));
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        for (Userdata o : users){
            if (o.login.equals(login) && o.password.equals(password)){
                return o.nickname;
            }
        }
        return null;
    }

}
