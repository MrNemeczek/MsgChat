package org.mj.Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

import org.mj.Models.*;
import org.mj.Database.*;

public class RegisterForm extends JFrame{
    private JTextField NameField;
    private JTextField LastnameField;
    private JTextField LoginField;
    private JPasswordField PasswordField;
    private JButton RegistryButton;
    private JPanel MainPanel;

    public RegisterForm(JFrame parent){
        setTitle("Registry");
        setContentPane(MainPanel);
        setMinimumSize(new Dimension(450, 300));
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
                //TODO: zrobic to na watkach
                Connection conn = DataBaseOperation.ConnectToDB(connectionUrl);
                //TODO: sprobowac przeniesc try catcha do DataBaseOperation
                try {
                    DataBaseOperation.Registry(user, conn);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                JOptionPane.showMessageDialog(null, "Account created");
                LoginForm loginForm = new LoginForm(null);
                dispose();
            }
        });
    }
    public static void main(String[] args){
        RegisterForm registerForm = new RegisterForm(null);
    }
}
