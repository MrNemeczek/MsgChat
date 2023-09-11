package org.mj.Threads;

import org.mj.Database.DataBaseOperation;
import org.mj.Functions.MyUI;
import org.mj.Interfaces.IThread;
import org.mj.Models.User;
import org.mj.Views.SearchUserForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

public class FindUsersThread extends Thread implements IThread {
    private SearchUserForm _form;
    private String _searchText;
    public FindUsersThread(SearchUserForm form, String searchText){
        _form = form;
        _searchText = searchText;
    }
    public void run(){
        _form.UsersPanel.removeAll();

        Connection conn = DataBaseOperation.ConnectToDB();
        try {
            LinkedList<User> foundUsers = DataBaseOperation.FindUser(_searchText, conn);
            if(foundUsers == null){
                JOptionPane.showMessageDialog(null, "No user found");
                return;
            }
            for(var foundUser : foundUsers){
                JButton FoundButton = MyUI.FlexButton(foundUser.Name + " " + foundUser.Lastname);

                _form.UsersPanel.add(FoundButton, BorderLayout.CENTER);
                FoundButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String connectionUrl = "jdbc:mysql://mqttdb.mysql.database.azure.com:3306/chatdb";
                        try {
                            if(DataBaseOperation.CheckFriend(foundUser, _form.currentUser, conn)){
                                JOptionPane.showMessageDialog(null, foundUser.Name + " " + foundUser.Lastname + " is already in your friend list");
                                return;
                            }
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }

                        int response = JOptionPane.showConfirmDialog(null, "Do you want to send friend request to: " + foundUser.Name + " " + foundUser.Lastname + "?", "Friend request", JOptionPane.YES_NO_OPTION);

                        if(response == JOptionPane.YES_OPTION){
                            try {
                                DataBaseOperation.FriendRequest( _form.currentUser,foundUser, conn );
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                });

            }

            _form.setVisible(true);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
