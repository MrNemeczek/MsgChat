package org.mj.Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

import org.mj.Database.DataBaseOperation;
import org.mj.Models.*;

public class MessagesForm extends JFrame{
    private JButton AddFriendButton;
    private JTextField MessageField;
    private JPanel MainPanel;
    private JButton SendButton;
    private JScrollPane MessagesPanel;
    private JPanel FriendsPanel;
    private JScrollPane FriendsScrollPanel;
    private JScrollPane RequestsScrollPanel;
    private JPanel RequestsPanel;

    public  MessagesForm(JFrame parent, LinkedList<Friend> friends, User currentUser, LinkedList<Friend> friendsRequested, Connection conn) {
        setTitle("Messages");
        setContentPane(MainPanel);
        setMinimumSize(new Dimension(450, 300));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

        setFriendsPanel(friends);
        setRequestsPanel(friendsRequested, conn);

        AddFriendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SearchUserForm searchUserForm = new SearchUserForm(null, currentUser);
            }
        });
    }

    public static void main(String[] args){
        //MessagesForm loginForm = new MessagesForm(null);
    }

    private void createUIComponents() {
        FriendsPanel = new JPanel();
        FriendsPanel.setLayout(new BoxLayout(FriendsPanel, BoxLayout.Y_AXIS));
        RequestsPanel = new JPanel();
        RequestsPanel.setLayout(new BoxLayout(RequestsPanel, BoxLayout.Y_AXIS));
    }
}
