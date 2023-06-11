package org.mj.Models;

public class Friend {
    public int ID_Friend;
    public int ID_User;
    public int ID_User_Friend;
    //0 - nieakceptowane, 1 - akceptowane
    public int Accepted;

    public User User_Friend = new User();
}
