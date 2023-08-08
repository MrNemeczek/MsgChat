package org.mj.Views;

import org.mj.Database.DataBaseOperation;
import org.mj.Functions.MyUI;
import org.mj.Models.User;
import org.mj.Threads.FindUsersThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

public class SearchUserForm extends JFrame implements ActionListener{
    private JTextField SearchField;
    private JPanel MainPanel;
    public JPanel UsersPanel;
    private JButton SearchButton;
    public User currentUser;

    public SearchUserForm(JFrame parent, User currentuser){
        setTitle("Search");
        setContentPane(MainPanel);
        setMinimumSize(new Dimension(450, 300));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        SearchUserForm form = this;
        currentUser = currentuser;

       SearchButton.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               FindUsersThread findUsersThread = new FindUsersThread(form, SearchField.getText());
               findUsersThread.start();
           }
       });

}
    private void createUIComponents() {
        UsersPanel = new JPanel();
        //UsersPanel.setLayout(new GridLayout(0,1));
        UsersPanel.setLayout(new BoxLayout(UsersPanel, BoxLayout.Y_AXIS));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
