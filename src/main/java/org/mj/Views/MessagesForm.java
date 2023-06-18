package org.mj.Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;


import org.eclipse.paho.client.mqttv3.*;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.mj.Database.*;
import org.mj.Models.*;
import org.mj.Functions.*;

public class MessagesForm extends JFrame implements ActionListener{
    private JButton AddFriendButton;
    private JTextField MessageField;
    private JPanel MainPanel;
    private JButton SendButton;
    private JScrollPane MessagesScrollPanel;
    private JPanel FriendsPanel;
    private JScrollPane FriendsScrollPanel;
    private JScrollPane RequestsScrollPanel;
    private JPanel RequestsPanel;
    private JPanel MessagePanel;

    private int ID_texting_friend;
    private User _currentUser;
    private Connection _conn;

    public  MessagesForm(JFrame parent/*, LinkedList<Friend> friends*/, User currentUser /*LinkedList<Friend> friendsRequested*/, Connection conn) throws SQLException {
        _currentUser = currentUser;
        _conn = conn;

        setTitle("Messages");
        setContentPane(MainPanel);
        setMinimumSize(new Dimension(450, 300));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

        //TODO: zapakowac do jednej metody np. SetPanels()
        setFriendsPanel();
        setRequestsPanel();

        AddFriendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SearchUserForm searchUserForm = new SearchUserForm(null, currentUser);

            }
        });

        SendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                try {
                    SendMessage();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }


            }
        });

        }

    private void setFriendsPanel() throws SQLException{
        FriendsPanel.removeAll();
        LinkedList<Friend> friends = DataBaseOperation.GetFriends(_currentUser, _conn);
        for(var friend : friends){

            JButton friendButton = new JButton(friend.User_Friend.Name + " " + friend.User_Friend.Lastname);
            friendButton.setSize(new Dimension(50,50));
            FriendsPanel.add(friendButton);
            FriendsPanel.revalidate();
            FriendsPanel.repaint();

            friendButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    try {
                        LinkedList<Message> messages = DataBaseOperation.GetMessages(_currentUser, friend, _conn);
                        ID_texting_friend = friend.User_Friend.ID_User;

                        MessagePanel.removeAll();
                        MessagePanel.revalidate();
                        MessagePanel.repaint();

                        for(var msg : messages){

                            JLabel msgLabel = new JLabel(msg.Content);

                            if(msg.ID_User_Sender == _currentUser.ID_User){
                                MessagePanel.add(MyUI.placeRight(msgLabel));
                            }
                            else{
                                MessagePanel.add(MyUI.placeLeft(msgLabel));
                            }

//                            test.add(msgLabel);
                            MessagePanel.revalidate();
                            MessagePanel.repaint();
                        }

                        //uruchomienie subskrybenta tematu
                        ReceiveMessage(ID_texting_friend);

                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                }
            });

        }
        System.out.println("Zapytano o znajomych");

    }
    private void setRequestsPanel() throws SQLException {

        class Multi extends Thread{
            public void run(){
                try {
                    while (true){

                        RequestsPanel.removeAll();
                        setFriendsPanel();
                        LinkedList<Friend> friendsRequested = DataBaseOperation.CheckFriendRequests(_currentUser, _conn);
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
                                            DataBaseOperation.AcceptFriendRequest(friend, _conn);
                                            LinkedList<Friend> friends = DataBaseOperation.GetFriends(_currentUser, _conn);
                                            setFriendsPanel();
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
                        setFriendsPanel();

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
        MessagePanel = new JPanel();
        MessagePanel.setLayout(new BoxLayout(MessagePanel, BoxLayout.Y_AXIS));
    }

    private void SendMessage() throws SQLException {
        String content = MessageField.getText();

        DataBaseOperation.SendMessage(_currentUser, ID_texting_friend, content, _conn);

        JLabel msgLabel = new JLabel(content);
        MessagePanel.add(MyUI.placeRight(msgLabel));
        MessagePanel.revalidate();
        MessagePanel.repaint();

        MQTTClientThread MQTTClient = new MQTTClientThread(content, _currentUser, ID_texting_friend);
        MQTTClient.start();

        MessageField.setText("");
    }

    private void ReceiveMessage(int IDTextingFriend){
        class MyThread extends Thread{
            public void run(){


                String broker = "tcp://40.115.61.160:1883";
                String topic = "mqtt/"+_currentUser.ID_User+"/"+IDTextingFriend;
                String username = _currentUser.Name;
                String password = _currentUser.Password;
                String clientid = "test2";
                int qos = 0;

                //System.out.println("nasluchiwanie wlaczone dla: " + topic);

                try {
                    MqttClient client = new MqttClient(broker, clientid, new MemoryPersistence());

                    MqttConnectOptions options = new MqttConnectOptions();
                    options.setUserName(username);
                    options.setPassword(password.toCharArray());
                    options.setConnectionTimeout(60);
                    options.setKeepAliveInterval(60);

                    //TODO: skrocic to kurestwo
                    client.setCallback(new MqttCallback() {

                        public void connectionLost(Throwable cause) {
                            System.out.println("connectionLost: " + cause.getMessage());
                        }

                        public void messageArrived(String topic, MqttMessage message) {
                            System.out.println("message content: " + new String(message.getPayload()));
                            JLabel msgLabel = new JLabel(new String(message.getPayload()));
                            MessagePanel.add(MyUI.placeLeft(msgLabel));
                            MessagePanel.revalidate();
                            MessagePanel.repaint();
                        }

                        public void deliveryComplete(IMqttDeliveryToken token) {
                            System.out.println("deliveryComplete---------" + token.isComplete());
                        }

                    });

                    client.connect(options);
                    client.subscribe(topic, qos);
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        MyThread t = new MyThread();
        t.start();

    }
    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
