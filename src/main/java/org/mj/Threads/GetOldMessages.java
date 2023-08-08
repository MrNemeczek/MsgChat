package org.mj.Threads;

import org.mj.Database.DataBaseOperation;
import org.mj.Functions.MyUI;
import org.mj.Models.Friend;
import org.mj.Models.Message;
import org.mj.Views.MessagesForm;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.LinkedList;

public class GetOldMessages extends Thread{
    private MessagesForm _form;

    public GetOldMessages(MessagesForm form){
        _form = form;
    }

    public void run(){
        int lastIDMsg = Message.LastIndex(_form.messages);
        try {
            LinkedList<Message> oldMessages = DataBaseOperation.GetOldMessages(_form._currentUser, _form._conversationFriend, lastIDMsg, _form._conn);
            if(oldMessages.size() == 0){
                _form.allMsgsDownloaded = true;
            }
            for(var msg : oldMessages){

                JLabel msgLabel = new JLabel(msg.Content);
                msgLabel.setFont(new Font("Arial", Font.PLAIN, 60));

                if(msg.ID_User_Sender == _form._currentUser.ID_User){
                    _form.MessagePanel.add(MyUI.placeRight(msgLabel), 0);
                }
                else{
                    _form.MessagePanel.add(MyUI.placeLeft(msgLabel), 0);
                }
                _form.MessagePanel.revalidate();
                _form.MessagePanel.repaint();
            }
            _form.messages.addAll(oldMessages);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
