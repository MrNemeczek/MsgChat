package org.mj.Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.mj.Threads.LoginThread;

public class LoginForm extends JFrame{
    private JPanel MainPanel;
    private JTextField LoginField;
    private JPasswordField PasswordField;
    public JButton LoginButton;
    private JButton RegisterButton;

    public  LoginForm(JFrame parent){
        setTitle("Login");
        setContentPane(MainPanel);
        setMinimumSize(new Dimension(450, 300));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        getRootPane().setDefaultButton(LoginButton);
        LoginForm form = this;

        LoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginButton.setEnabled(false);
                String login = LoginField.getText();
                String password = String.valueOf(PasswordField.getPassword());

                LoginThread loginThread = new LoginThread(form, login, password);
                loginThread.start();
            }
        });
        RegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterButton.setEnabled(false);
                RegisterForm registerForm = new RegisterForm(null);
                dispose();
            }
        });
    }

    /*public static void main (String[] args){
        LoginForm loginForm = new LoginForm(null);
    }*/
}
