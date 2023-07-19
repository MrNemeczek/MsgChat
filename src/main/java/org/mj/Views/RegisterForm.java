package org.mj.Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

import org.mj.Functions.MQTTRegistryClientThread;
import org.mj.Models.*;
import org.mj.Database.*;

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
        RegistryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User user = new User();

                user.Login = LoginField.getText();
                user.Password = String.valueOf(PasswordField.getPassword());
                user.Name = NameField.getText();
                user.Lastname = LastnameField.getText();

                String connectionUrl = "jdbc:mysql://mqttdb.mysql.database.azure.com:3306/chatdb";
                Connection conn = DataBaseOperation.ConnectToDB(connectionUrl);
                //TODO: sprobowac przeniesc try catcha do DataBaseOperation
                try {
                    if(DataBaseOperation.Registry(user, conn)){
                        MQTTRegistryClientThread registryClientThread = new MQTTRegistryClientThread(user);
                        registryClientThread.start();
                        JOptionPane.showMessageDialog(null, "Account created");
                        LoginForm loginForm = new LoginForm(null);
                        dispose();
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "There is already an account with this login");
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
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
    public static void main(String[] args){
        RegisterForm registerForm = new RegisterForm(null);
    }
}
