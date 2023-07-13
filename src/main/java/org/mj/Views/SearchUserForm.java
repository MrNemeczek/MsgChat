package org.mj.Views;

import org.mj.Database.DataBaseOperation;
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

               String namelastname = SearchField.getText();
               String[] name_lastname = namelastname.split(" ");
               String connectionUrl = "jdbc:mysql://mqttdb.mysql.database.azure.com:3306/chatdb";

               User usertofind = new User();
               usertofind.Name = name_lastname[0];
               usertofind.Lastname = name_lastname[1];

               Connection conn = DataBaseOperation.ConnectToDB(connectionUrl);
               try {
                   LinkedList<User> foundUsers = DataBaseOperation.FindUser(usertofind, conn);
                   for(var foundUser : foundUsers){
                       JButton FoundButton = new JButton(foundUser.Name + " " + foundUser.Lastname);
                       FoundButton.setSize(new Dimension(50,50));

                       UsersPanel.add(FoundButton);

                       FoundButton.addActionListener(new ActionListener() {
                           @Override
                           public void actionPerformed(ActionEvent e) {
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
         UsersPanel = new JPanel(new GridBagLayout());
         UsersPanel.setLayout(new BoxLayout(UsersPanel, BoxLayout.Y_AXIS));


     }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
