package org.mj.Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.*;


import org.eclipse.paho.client.mqttv3.*;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.mj.Database.*;
import org.mj.Models.*;
import org.mj.Functions.*;
import org.mj.Threads.GetMessagesThread;
import org.mj.Threads.GetOldMessages;
import org.mj.Threads.SendMessageThread;

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
    public JPanel MessagePanel;
    private JButton LogoutButton;
    private JLabel UserLabel;
    private JLabel ConversationLbl;
    public JScrollBar ScrollBarMessage;

    public int ID_texting_friend;
    private int MsgPage = 1;
    public User _currentUser;
    public Friend _conversationFriend;
    public Connection _conn;
    private boolean _logged = true;
    public boolean _firstScrollTop = false;
    public boolean msgsLoaded = false;
    public LinkedList<Message> messages;
    private MessagesForm _form;
    public boolean allMsgsDownloaded = false;

    public  MessagesForm(JFrame parent, User currentUser, Connection conn) throws SQLException {
        _currentUser = currentUser;
        _conn = conn;

        setTitle("Messages");
        setContentPane(MainPanel);
        setMinimumSize(new Dimension(800, 800));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        _form = this;

        UserLabel.setText(currentUser.Name + " " + currentUser.Lastname);

        setFriendsPanel();
        setRequestsPanel();

        ScrollBarMessage = MessagesScrollPanel.getVerticalScrollBar();
        ScrollBarMessage.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                JScrollBar scrollBar = (JScrollBar) e.getSource();
//tu pobieranie kolejnych wiadomosci przy dojechaniu do gory scrollem
                if (scrollBar.getValue() == scrollBar.getMinimum() && msgsLoaded && !allMsgsDownloaded) {
                    scrollBar.setValue(scrollBar.getMinimum() + 20);

                    GetOldMessages getOldMessages = new GetOldMessages(_form);
                    getOldMessages.start();
                }
            }
        });
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
                } catch (SQLException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        LogoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginForm loginForm = new LoginForm(null);
                _logged = false;
                dispose();
            }
        });
    }

    private void setFriendsPanel() throws SQLException{
        FriendsPanel.removeAll();
        LinkedList<Friend> friends = DataBaseOperation.GetFriends(_currentUser, _conn);
        for(var friend : friends){

            JButton friendButton = MyUI.FlexButton(friend.User_Friend.Name + " " + friend.User_Friend.Lastname);
            FriendsPanel.add(friendButton);
            FriendsPanel.revalidate();
            FriendsPanel.repaint();

            friendButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    _conversationFriend = friend;
                    ConversationLbl.setText(friend.User_Friend.Name + " " + friend.User_Friend.Lastname);
                    GetMessagesThread getMessagesThread = new GetMessagesThread(_form, friend);
                    getMessagesThread.start();
                }
            });

        }
        System.out.println("Zapytano o znajomych");

    }
    private void setRequestsPanel() throws SQLException {
        class Multi extends Thread{
            public void run(){
                try {
                    while (_logged){

                        RequestsPanel.removeAll();
                        setFriendsPanel();
                        LinkedList<Friend> friendsRequested = DataBaseOperation.CheckFriendRequests(_currentUser, _conn);
                        for (var friendRequest : friendsRequested) {

                            JButton requestButton = MyUI.FlexButton(friendRequest.User_Friend.Name + " " + friendRequest.User_Friend.Lastname);
                            RequestsPanel.add(requestButton);
                            RequestsPanel.revalidate();
                            RequestsPanel.repaint();


                            requestButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    int response = JOptionPane.showConfirmDialog(null, "Do you want to accept friend request from: " + friendRequest.User_Friend.Name + " " + friendRequest.User_Friend.Lastname + "?", "Friend request", JOptionPane.YES_NO_OPTION);

                                    if (response == JOptionPane.YES_OPTION) {
                                        try {
                                            DataBaseOperation.AcceptFriendRequest(friendRequest, _conn);
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
                        //setFriendsPanel();
                        //TODO: zmniejszyc czas
                        Thread.sleep(5000);
                    }
                }catch (InterruptedException | SQLException e) {
                    throw new RuntimeException(e);

                }}}
        Multi t1 = new Multi();
        t1.setDaemon(true);
        t1.start();

    }
    private void SendMessage() throws SQLException, InterruptedException {
        String content = MessageField.getText();
        SendMessageThread sendMessageThread = new SendMessageThread(_currentUser, ID_texting_friend, content, _conn);
        sendMessageThread.start();

        JLabel msgLabel = new JLabel(content);
        MessagePanel.add(MyUI.placeRight(msgLabel));
        MessagePanel.revalidate();
        MessagePanel.repaint();

        MQTTClientThread MQTTClient = new MQTTClientThread(content, _currentUser, ID_texting_friend);
        MQTTClient.start();

        MessageField.setText("");
    }

    public void ReceiveMessage(int IDTextingFriend){
        class MyThread extends Thread{
            public void run(){


                String broker = "tcp://40.115.61.160:1883";
                String topic = "mqtt/"+_currentUser.ID_User+"/"+IDTextingFriend;
                String username = _currentUser.Name;
                String password = _currentUser.Password;
                String clientid = String.valueOf(_currentUser.ID_User);
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
                            //msgLabel.setFont(new Font("Arial", Font.PLAIN, 40));

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
    private void createUIComponents() {
        FriendsPanel = new JPanel();
        FriendsPanel.setLayout(new BoxLayout(FriendsPanel, BoxLayout.Y_AXIS));

        RequestsPanel = new JPanel();
        RequestsPanel.setLayout(new BoxLayout(RequestsPanel, BoxLayout.Y_AXIS));

        MessagePanel = new JPanel();
        MessagePanel.setLayout(new BoxLayout(MessagePanel, BoxLayout.Y_AXIS));
    }
    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
