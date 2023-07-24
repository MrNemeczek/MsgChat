package org.mj.Views;

import org.mj.Database.DataBaseOperation;
import org.mj.Functions.MyUI;
import org.mj.Models.User;

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
    private JPanel UsersPanel;
    private JButton SearchButton;

    public SearchUserForm(JFrame parent, User currentuser){
        setTitle("Search");
        setContentPane(MainPanel);
        setMinimumSize(new Dimension(450, 300));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);


       SearchButton.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               UsersPanel.removeAll();

               String connectionUrl = "jdbc:mysql://mqttdb.mysql.database.azure.com:3306/chatdb";

               Connection conn = DataBaseOperation.ConnectToDB(connectionUrl);
               try {
                   LinkedList<User> foundUsers = DataBaseOperation.FindUser(SearchField.getText(), conn);
                   if(foundUsers == null){
                       JOptionPane.showMessageDialog(null, "No user found");
                       return;
                   }
                   for(var foundUser : foundUsers){
                       //JButton FoundButton = new JButton(foundUser.Name + " " + foundUser.Lastname);
                       JButton FoundButton = MyUI.FlexButton(foundUser.Name + " " + foundUser.Lastname);
//                       FoundButton.setSize(new Dimension(150,150));
                       //System.out.println("wysokosc: " + UsersPanel.getHeight() + " Szerokosc:" + UsersPanel.getWidth());

                       //UsersPanel.add(FoundButton);
                       UsersPanel.add(FoundButton, BorderLayout.CENTER);
                       //UsersPanel.add(FoundButton, BorderLayout.CENTER);
                       FoundButton.addActionListener(new ActionListener() {
                           @Override
                           public void actionPerformed(ActionEvent e) {
                               String connectionUrl = "jdbc:mysql://mqttdb.mysql.database.azure.com:3306/chatdb";
                               try {
                                   if(DataBaseOperation.CheckFriend(foundUser, currentuser, conn)){
                                       JOptionPane.showMessageDialog(null, foundUser.Name + " " + foundUser.Lastname + " is already in your friend list");
                                       return;
                                   }
                               } catch (SQLException ex) {
                                   throw new RuntimeException(ex);
                               }

                               int response = JOptionPane.showConfirmDialog(null, "Do you want to send friend request to: " + foundUser.Name + " " + foundUser.Lastname + "?", "Friend request", JOptionPane.YES_NO_OPTION);

                               if(response == JOptionPane.YES_OPTION){
                                   try {
                                       DataBaseOperation.FriendRequest( currentuser,foundUser, conn );
                                   } catch (SQLException ex) {
                                       throw new RuntimeException(ex);
                                   }
                               }
                           }
                       });

                   }

                   setVisible(true);
               } catch (Exception ex) {
                   throw new RuntimeException(ex);
               }
           }
       });

}
//    public static void main(String[] args) { SearchUserForm searchUserForm = new SearchUserForm(null,);
//    }

    private void createUIComponents() {
        UsersPanel = new JPanel();
        UsersPanel.setLayout(new GridLayout(0,1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
