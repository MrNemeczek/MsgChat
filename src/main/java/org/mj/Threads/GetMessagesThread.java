package org.mj.Threads;

import org.mj.Database.DataBaseOperation;
import org.mj.Functions.MyUI;
import org.mj.Interfaces.IThread;
import org.mj.Models.Friend;
import org.mj.Models.Message;
import org.mj.Views.MessagesForm;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;

public class GetMessagesThread extends Thread implements IThread {
    private MessagesForm _form;
    private Friend _friend;
    public GetMessagesThread(MessagesForm form, Friend friend){
        _form = form;
        _friend = friend;
    }
    public void run(){
        _form._firstScrollTop = false;
        _form.msgsLoaded = false;

        try {
            do {
                int lastIDMsg = Message.LastIndex(_form.messages);
                if(_form.messages != null){
                    LinkedList<Message> tempMsgs = DataBaseOperation.GetOldMessages(_form._currentUser, _friend, lastIDMsg, _form._conn);
                    Collections.reverse(tempMsgs);
                    _form.messages.addAll(0, tempMsgs);

                    if(tempMsgs == null || tempMsgs.size() == 0){
                        break;
                    }
                }else{
                    _form.messages = DataBaseOperation.GetOldMessages(_form._currentUser, _friend, lastIDMsg, _form._conn);
                    Collections.reverse(_form.messages);
                }

                _form.ID_texting_friend = _friend.User_Friend.ID_User;

                if(_form.messages == null || _form.messages.size() == 0){
                    break;
                }


                _form.MessagePanel.removeAll();
                _form.MessagePanel.revalidate();
                _form.MessagePanel.repaint();

                for (var msg : _form.messages) {

                    JLabel msgLabel = new JLabel(msg.Content);
                    msgLabel.setFont(new Font("Arial", Font.PLAIN, 60));

                    if (msg.ID_User_Sender == _form._currentUser.ID_User) {
                        _form.MessagePanel.add(MyUI.placeRight(msgLabel));
                    } else {
                        _form.MessagePanel.add(MyUI.placeLeft(msgLabel));
                    }
                    _form.MessagePanel.revalidate();
                    _form.MessagePanel.repaint();
                    Thread.sleep(10);//musi byc zeby dzialalo poprawnie
                }
            }while (!_form.ScrollBarMessage.isVisible());

            _form.ScrollBarMessage.setValue(_form.ScrollBarMessage.getMaximum());
            //uruchomienie subskrybenta tematu
            _form.ReceiveMessage(_form.ID_texting_friend);
            _form.msgsLoaded = true;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
