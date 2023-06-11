package org.mj.Views;

import org.mj.Database.DataBaseOperation;
import org.mj.Models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.LinkedList;

public class SearchUserForm extends JFrame implements ActionListener{
    private JTextField SearchField;
    private JPanel MainPanel;
    private JPanel UsersPanel;
    private JButton SearchButton;

    public SearchUserForm(JFrame parent){
        setTitle("Search");
        setContentPane(MainPanel);
        setMinimumSize(new Dimension(450, 300));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);


       SearchButton.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               String namelastname = SearchField.getText();
               String[] name_lastname = namelastname.split(" ");
               User usertofind = new User();
               usertofind.Name = name_lastname[0];
               usertofind.Lastname = name_lastname[1];
               String connectionUrl = "jdbc:mysql://mqttdb.mysql.database.azure.com:3306/chatdb";

               Connection conn = DataBaseOperation.ConnectToDB(connectionUrl);
               try {
                   LinkedList<User> users = DataBaseOperation.FindUser(usertofind, conn);
                   for(var user : users){
                       JButton FoundButton = new JButton(user.Name + " " + user.Lastname);
                       FoundButton.setSize(new Dimension(50,50));

                       UsersPanel.add(FoundButton);
                   }
                   setVisible(true);
               } catch (Exception ex) {
                   throw new RuntimeException(ex);
               }
           }


       });

}
    public static void main(String[] args) { SearchUserForm searchUserForm = new SearchUserForm(null);
    }

    private void createUIComponents() {
         UsersPanel = new JPanel(new GridBagLayout());
         UsersPanel.setLayout(new BoxLayout(UsersPanel, BoxLayout.Y_AXIS));


     }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
