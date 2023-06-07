package org.mj.Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

import org.mj.Database.*;

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
                String connectionUrl = "jdbc:mysql://mqttdb.mysql.database.azure.com:3306/chatdb";

                Connection conn = DataBaseOperation.ConnectToDB(connectionUrl);
                //TODO: sprobowac przeniesc try catcha do DataBaseOperation
                try {
                    int idTemp = DataBaseOperation.Login(login, password, conn);
                    System.out.println(idTemp);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
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

    public static void main(String[] args){
        LoginForm loginForm = new LoginForm(null);
    }
}
