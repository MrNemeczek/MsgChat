package org.mj.Models;

import java.util.LinkedList;

public class Message {
    public int ID_Message;
    public int ID_User_Sender;
    public int ID_User_Receiver;
    public String Content;
    public java.sql.Timestamp Timestamp;

    public static int LastIndex(LinkedList<Message> messageList){
        if(messageList == null){
            return Integer.MAX_VALUE;
        }
        int minID = Integer.MAX_VALUE;

        for (Message message : messageList) {
            if (message.ID_Message < minID) {
                minID = message.ID_Message;
            }
        }

        return minID;
    }
}
