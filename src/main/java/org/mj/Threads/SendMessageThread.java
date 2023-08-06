package org.mj.Threads;

import org.mj.Database.DataBaseOperation;
import org.mj.Models.User;

import java.sql.Connection;
import java.sql.SQLException;

public class SendMessageThread extends Thread{
    private User _currentUser;
    private int _ID_texting_friend;
    private String _content;
    private Connection _conn;
    public SendMessageThread(User currentUser, int ID_texting_friend, String content, Connection conn){
        _currentUser = currentUser;
        _ID_texting_friend = ID_texting_friend;
        _content = content;
        _conn = conn;
    }
    public void run() {
        try {
            DataBaseOperation.SendMessage(_currentUser, _ID_texting_friend, _content, _conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
