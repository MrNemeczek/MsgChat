package org.mj.Threads;

import org.mj.Database.DataBaseOperation;
import org.mj.Interfaces.IThread;
import org.mj.Models.User;
import org.mj.Views.LoginForm;
import org.mj.Views.MessagesForm;
import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginThread extends Thread implements IThread {
    private LoginForm _form;
    private String _login;
    private String _password;
    public LoginThread(LoginForm form, String login, String password){
        _form = form;
        _login = login;
        _password = password;
    }

    public void run(){
        Connection conn = DataBaseOperation.ConnectToDB();
        try {
            User user = DataBaseOperation.Login(_login, _password, conn);

            if(user != null){
                MessagesForm msgForm = new MessagesForm (null /*friends*/, user /*friendsRequested*/, conn);

                _form.dispose();
            }
            else{
                JOptionPane.showMessageDialog(null, "Incorrect login or password!");
                _form.LoginButton.setEnabled(true);
            }


        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
