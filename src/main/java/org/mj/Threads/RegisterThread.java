package org.mj.Threads;

import org.mj.Database.DataBaseOperation;
import org.mj.Interfaces.IThread;
import org.mj.Models.User;
import org.mj.Views.LoginForm;
import org.mj.Views.RegisterForm;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class RegisterThread extends Thread implements IThread {
    private User _user;
    private RegisterForm _form;

    public RegisterThread(RegisterForm form, User user){
        _form = form;
        _user = user;
    }
    public void run(){
        Connection conn = DataBaseOperation.ConnectToDB();

        try {
            if(DataBaseOperation.Registry(_user, conn)){
                MQTTRegistryClientThread registryClientThread = new MQTTRegistryClientThread(_user);
                registryClientThread.start();
                JOptionPane.showMessageDialog(null, "Account created");
                LoginForm loginForm = new LoginForm(null);
                _form.dispose();
            }
            else{
                JOptionPane.showMessageDialog(null, "There is already an account with this login");
                _form.RegistryButton.setEnabled(true);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
