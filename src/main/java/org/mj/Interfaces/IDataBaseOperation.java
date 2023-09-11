package org.mj.Interfaces;

import org.mj.Models.Friend;
import org.mj.Models.Message;
import org.mj.Models.User;

import java.sql.Connection;
import java.util.LinkedList;

public interface IDataBaseOperation {
    static Connection ConnectToDB(){
        return null;
    }
    static User Login(){
        return null;
    }
    static boolean Registry() {
        return true;
    }
    static  LinkedList<Friend> GetFriends() {
        return null;
    }
    static LinkedList<Friend> CheckFriendRequests() {
        return null;
    }
    static LinkedList<User> FindUser() {
        return null;
    }
    static boolean FriendRequest() {
        return true;
    }
    static boolean CheckFriend() {
        return true;
    }
    static boolean AcceptFriendRequest() {
        return true;
    }
    static LinkedList<Message> GetOldMessages() {
        return null;
    }
    static boolean SendMessage() {
        return false;
    }
}
