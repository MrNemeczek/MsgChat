package org.mj.Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;


import org.mj.Database.*;
import org.mj.Models.*;

public class MessagesForm extends JFrame implements ActionListener{
    private JButton AddFriendButton;
    private JTextField MessageField;
    private JPanel MainPanel;
    private JButton SendButton;
    private JScrollPane MessagesPanel;
    private JPanel FriendsPanel;
    private JScrollPane FriendsScrollPanel;
    private JScrollPane RequestsScrollPanel;
    private JPanel RequestsPanel;

    public  MessagesForm(JFrame parent, LinkedList<Friend> friends, User currentUser /*LinkedList<Friend> friendsRequested*/, Connection conn) throws SQLException {

        setTitle("Messages");
        setContentPane(MainPanel);
        setMinimumSize(new Dimension(450, 300));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

        setFriendsPanel(friends);
        setRequestsPanel(/*friendsRequested*/ conn, currentUser);

        AddFriendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SearchUserForm searchUserForm = new SearchUserForm(null, currentUser);

            }
        });

        }

    private void setFriendsPanel(LinkedList<Friend> friends){
        FriendsPanel.removeAll();
        for(var friend : friends){

            JButton friendButton = new JButton(friend.User_Friend.Name + " " + friend.User_Friend.Lastname);
            friendButton.setSize(new Dimension(50,50));
            FriendsPanel.add(friendButton);
            FriendsPanel.revalidate();
            FriendsPanel.repaint();

        }
        System.out.println("Zapytano o znajomych");

    }

    private void setRequestsPanel(/*LinkedList<Friend> friendsRequested, */Connection conn, User currentUser) throws SQLException {

        class Multi extends Thread{
            public void run(){
                try {
                    while (true){

                        RequestsPanel.removeAll();
                        LinkedList<Friend> friendsRequested = DataBaseOperation.CheckFriendRequests(currentUser, conn);
                        for (var friend : friendsRequested) {

                            JButton requestButton = new JButton(friend.User_Friend.Name + " " + friend.User_Friend.Lastname);
                            requestButton.setSize(new Dimension(50, 50));
                            RequestsPanel.add(requestButton);
                            RequestsPanel.revalidate();
                            RequestsPanel.repaint();


                            requestButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    int response = JOptionPane.showConfirmDialog(null, "Are you sure?");

                                    if (response == JOptionPane.YES_OPTION) {
                                        try {
                                            DataBaseOperation.AcceptFriendRequest(friend, conn);
                                            LinkedList<Friend> friends = DataBaseOperation.GetFriends(currentUser, conn);
                                            setFriendsPanel(friends);
                                            RequestsPanel.remove(requestButton);
                                            RequestsPanel.revalidate();
                                            RequestsPanel.repaint();
                                        } catch (SQLException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }
                                }
                            });

                        }
                        System.out.println("Zapytano o zaproszenia");

                        Thread.sleep(5000);
                    }
                }catch (InterruptedException | SQLException e) {
                    throw new RuntimeException(e);

                }}}
        Multi t1 = new Multi();
        t1.start();


    }

    private void createUIComponents() {
        FriendsPanel = new JPanel();
        FriendsPanel.setLayout(new BoxLayout(FriendsPanel, BoxLayout.Y_AXIS));
        RequestsPanel = new JPanel();
        RequestsPanel.setLayout(new BoxLayout(RequestsPanel, BoxLayout.Y_AXIS));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
