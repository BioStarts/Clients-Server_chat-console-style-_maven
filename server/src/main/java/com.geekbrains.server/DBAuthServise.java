package com.geekbrains.server;



public class DBAuthServise implements AuthService {


    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        return SQLHandler.getNicknameByLoginAndPassword(login, password);
    }

    @Override
    public boolean changeNick(String nickname, String newNickname) {
        return SQLHandler.changeNick(nickname, newNickname);
    }
}

    /*
    public static void main(String[] args) {
        //-------------sql:
        try {
            connect();
            //createTable();//создаем таблицу если не существует
            clearTable();
            prepareAllStatements();//вызываем оболочку от иньекций
            fillTable();
            System.out.println("Подключились к SQL");
            checkUser();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
            System.out.println("Отключились от БД");
        }
        //-------------сервер:
        new Server();
    }

    private static void checkUser() throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE login = 'login1' AND password = 'pass1'");
        if (rs.next()){
            System.out.println("Найден в базе" + " " + rs.getString("nickname"));
        }
        else {System.out.println("Не найден в базе");}
    }

    private static void createTable() throws SQLException {//создание таблици если не существует
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS USERS (/n" +
                "id       INTEGER PRIMARY KEY AUTOINCREMENT,/n" +
                "login    TEXT,/n" +
                "password TEXT,/n" +
                "nickname TEXT/n" +
                ");");
    }

    private static void dropTable() throws SQLException {//удаление таблицы если она существует
        stmt.executeUpdate("DROP TABLE IF EXISTS USERS");
    }

    public static void fillTable() throws SQLException {//заполняем псевдоюзерами
        connection.setAutoCommit(false);
        for (int i = 1; i <= 10000; i++) {
            psInsert.setString(1, "login" + i);
            psInsert.setString(2, "pass" + i);
            psInsert.setString(3, "nick" + i);
            psInsert.executeUpdate();
        }
        connection.setAutoCommit(true);
    }

    public static void batchFillTable() throws SQLException {//заполняем псевдоюзерами
        connection.setAutoCommit(false);
        for (int i = 1; i <= 10000; i++) {
            psInsert.setString(1, "login" + i);
            psInsert.setString(2, "pass" + i);
            psInsert.setString(3, "nick" + i);
            psInsert.addBatch();
        }
        psInsert.executeBatch();
        connection.setAutoCommit(true);
    }

    public static void prepareAllStatements() throws SQLException {//создаем оболчку защиту от инъекций
        psInsert = connection.prepareStatement("INSERT INTO users (login, password, nickname) VALUES (?, ?, ?);");
    }

    private static void selextUsers() throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE id < 3");
        while(rs.next()){
            System.out.println(rs.getInt(1) + " " + rs.getString("login") + " " + rs.getString("password")
                    + " " + rs.getString("nickname"));
        }
        rs.close();
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
    }*/

