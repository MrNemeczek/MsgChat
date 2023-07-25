package org.mj.Database;

import org.mj.Models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;

public class LoginDB extends Thread {
    private Connection _connection;
    private String _login;
    private String _password;
    public LoginDB(String login, String password, Connection connection){
        _connection = connection;
        _login = login;
        _password = password;
    }
    public void run(){
        try {
            Thread.sleep(3000);
            System.out.println("skonczylem");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        String query = "SELECT * FROM user WHERE login=\"" + _login + "\" AND password=\"" + _password + "\"";
//
//        try {
//            PreparedStatement ps = _connection.prepareStatement(query);
//            ResultSet rs = null;
//
//            rs = ps.executeQuery();
//
//
//            if(rs.next()){
//                User user = new User();
//
//                user.Login = rs.getString("login");
//                user.Password = rs.getString("password");
//                user.Name = rs.getString("name");
//                user.Lastname = rs.getString("lastname");
//                user.ID_User = rs.getInt("id_user");
//
//                return user;
//            }
//        } catch (SQLException e) {
//        throw new RuntimeException(e);
//        }
//        return null;
    }

//    @Override
//    public User call() throws Exception {
//        String query = "SELECT * FROM user WHERE login=\"" + _login + "\" AND password=\"" + _password + "\"";
//        Thread.sleep(3000);
//        try {
//            PreparedStatement ps = _connection.prepareStatement(query);
//            ResultSet rs = null;
//
//            rs = ps.executeQuery();
//
//
//            if(rs.next()){
//                User user = new User();
//
//                user.Login = rs.getString("login");
//                user.Password = rs.getString("password");
//                user.Name = rs.getString("name");
//                user.Lastname = rs.getString("lastname");
//                user.ID_User = rs.getInt("id_user");
//
//                return user;
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return null;
//    }
}
