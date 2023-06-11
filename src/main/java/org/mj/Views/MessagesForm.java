package org.mj.Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import org.mj.Models.*;

public class MessagesForm extends JFrame{
    private JButton AddFriendButton;
    private JTextField MessageField;
    private JPanel MainPanel;
    private JButton SendButton;
    private JScrollPane MessagesPanel;
    private JPanel FriendsPanel;
    private JScrollPane FriendsScrollPanel;

    public  MessagesForm(JFrame parent, LinkedList<Friend> friends, User currentUser) {
        setTitle("Login");
        setContentPane(MainPanel);
        setMinimumSize(new Dimension(450, 300));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

        for(var friend : friends){
            JButton friendButton = new JButton(friend.User_Friend.Name + " " + friend.User_Friend.Lastname);
            friendButton.setSize(new Dimension(50,50));

            FriendsPanel.add(friendButton);
        }

        AddFriendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public static void main(String[] args){
        //MessagesForm loginForm = new MessagesForm(null);
    }

    private void createUIComponents() {
        FriendsPanel = new JPanel();
        FriendsPanel.setLayout(new BoxLayout(FriendsPanel, BoxLayout.Y_AXIS));
    }
}
