package org.mj.Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import org.mj.Models.*;

public class MessagesForm extends JFrame{
    private JButton AddFriendButton;
    private JTextField textField1;
    private JPanel MainPanel;
    private JButton SendButton;
    private JScrollPane FriendsScrollPanel;
    private JScrollPane MessagesPanel;

    public  MessagesForm(JFrame parent, LinkedList<Friend> friends, User currentUser) {
        setTitle("Login");
        setContentPane(MainPanel);
        setMinimumSize(new Dimension(450, 300));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

        for(var friend : friends){
            JButton friendButton = new JButton(String.valueOf(friend.ID_Friend));
            //friendButton.setSize(new Dimension(50,50));
            JPanel view = (JPanel) FriendsScrollPanel.getViewport().getView();
            view.add(new JLabel("test"), null);
            //FriendsScrollPanel.add(friendButton);
            repaint();
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
}
