package org.mj.Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.mj.Database.*;
import org.mj.Models.Friend;
import org.mj.Models.User;

public class LoginForm extends JFrame{
    private JPanel MainPanel;
    private JTextField LoginField;
    private JPasswordField PasswordField;
    private JButton LoginButton;
    private JButton RegisterButton;

    public  LoginForm(JFrame parent){
        setTitle("Login");
        setContentPane(MainPanel);
        setMinimumSize(new Dimension(450, 300));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

        LoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = LoginField.getText();
                String password = String.valueOf(PasswordField.getPassword());

                Connection conn = DataBaseOperation.ConnectToDB();
                //TODO: sprobowac przeniesc try catcha do DataBaseOperation
                try {
                    User user = DataBaseOperation.Login(login, password, conn);

                    if(user != null){
                        MessagesForm msgForm = new MessagesForm (null /*friends*/, user /*friendsRequested*/, conn);

                        dispose();
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Incorrect login or password!");
                    }


                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

//                ExecutorService executorService = Executors.newSingleThreadExecutor();
//                Future<User> future = executorService.submit(new LoginDB(login, password, conn));
//                try {
//                    User user = future.get();
//                    if (user != null) {
//                        MessagesForm msgForm = new MessagesForm (null , user , conn);
//
//                        dispose();
//                    } else {
//                        JOptionPane.showMessageDialog(null, "Incorrect login or password!");
//                    }
//                } catch (InterruptedException | ExecutionException ex) {
//                    ex.printStackTrace();
//                } catch (SQLException ex) {
//                    throw new RuntimeException(ex);
//                }

            }
        });
        RegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterForm registerForm = new RegisterForm(null);
                dispose();
            }
        });
    }

    public static void main (String[] args){
        LoginForm loginForm = new LoginForm(null);
    }
}
