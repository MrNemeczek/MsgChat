package org.mj.Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.mj.Models.*;
import org.mj.Threads.RegisterThread;

public class RegisterForm extends JFrame{
    private JTextField NameField;
    private JTextField LastnameField;
    private JTextField LoginField;
    private JPasswordField PasswordField;
    private JButton RegistryButton;
    private JPanel MainPanel;
    private JButton LoginButton;

    public RegisterForm(JFrame parent){
        setTitle("Registry");
        setContentPane(MainPanel);
        setMinimumSize(new Dimension(450, 350));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        JFrame form = this;
        RegistryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User user = new User();

                user.Login = LoginField.getText();
                user.Password = String.valueOf(PasswordField.getPassword());
                user.Name = NameField.getText();
                user.Lastname = LastnameField.getText();

                RegisterThread registerThread = new RegisterThread(form, user);
                registerThread.start();
            }
        });
        LoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginForm loginForm = new LoginForm(null);
                dispose();
            }
        });
    }
   /* public static void main(String[] args){
        RegisterForm registerForm = new RegisterForm(null);
    }*/
}
