package org.mj.Database;

import java.sql.*;
import java.util.LinkedList;

import org.mj.Models.*;

public class DataBaseOperation {
    /**
     * laczenie sie z baza danych
     * @param connectionURL adres URL do bazy danych
     * @return Connection jesli nie udalo sie polaczyc zwraca null
     */
    public static Connection ConnectToDB(String connectionURL){
        try {
            Connection conn = DriverManager.getConnection(connectionURL, "adminansb", "Bolekkrul1!");
            return conn;
        }
        catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * logowanie do aplikacji
     * @param login
     * @param password
     * @param connection
     * @return 0 - jesli logowanie jest niepoprawne (kazda inna liczba oznacza udane logowanie)
     * @throws SQLException
     */
    public static User Login(String login, String password, Connection connection) throws SQLException {
        String query = "SELECT * FROM user WHERE login=\"" + login + "\" AND password=\"" + password + "\"";

        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        if(rs.next()){
            User user = new User();

            user.Login = rs.getString("login");
            user.Password = rs.getString("password");
            user.Name = rs.getString("name");
            user.Lastname = rs.getString("lastname");
            user.ID_User = rs.getInt("id_user");

            return user;
        }
        //TODO: zrobic zeby zwracalo User i jesli fail logowanie to null
        return null;
    }

    /**
     * rejestracja do aplikacji
     * @param user
     * @param connection
     * @return true - udana rejestracja false - nieudana rejestracja
     * @throws SQLException
     */
    public static boolean Registry(User user, Connection connection) throws SQLException {
        String query = "INSERT INTO user (`login`, `password`, `name`, `lastname`) VALUES ('" + user.Login + "', '" + user.Password + "', '" + user.Name + "', '" + user.Lastname + "');";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.executeUpdate(query);

        //TODO: dodac false jesli sie nie powiedzie i jesli dany login juz istnieje w bazie
        return true;
    }

    public static LinkedList<Friend> GetFriends(User user, Connection connection) throws SQLException {
        String query = "SELECT f.*, u.name, u.lastname FROM friend as f \n" +
                "INNER JOIN user as u ON f.id_user_friend=u.id_user\n" +
                "WHERE f.id_user=" + user.ID_User + " AND f.accepted=1;";

        LinkedList<Friend> friends = new LinkedList<Friend>();

        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            Friend friend = new Friend();

            friend.ID_Friend = rs.getInt("id_friend");
            friend.ID_User = rs.getInt("id_user");
            friend.ID_User_Friend = rs.getInt("id_user_friend");
            friend.Accepted = rs.getInt("accepted");
            friend.User_Friend.Name =  rs.getString("name");
            friend.User_Friend.Lastname =  rs.getString("lastname");

            friends.add(friend);
        }

        return friends;
    }
    public static LinkedList<User> FindUser(User user, Connection connection) throws Exception{
        String query = "SELECT id_user, name, lastname FROM user WHERE name LIKE '%" + user.Name + "%' OR lastname LIKE '%" + user.Lastname + "%';";

        LinkedList<User> users = new LinkedList<>();
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        
        while(rs.next()){
            
            User userfound = new User();
            
            userfound.Name = rs.getString("name");
            userfound.Lastname = rs.getString("lastname");
            userfound.ID_User = rs.getInt("id_user");
            users.add(userfound);
            System.out.println(userfound.ID_User);
            
        }
        return users;

    }

    public static boolean FriendRequest (User user, User frienduser, Connection connection) throws SQLException {
        String query = "INSERT INTO friend (`id_user`, `id_user_friend`) VALUES ( " + user.ID_User + ", " + frienduser.ID_User + ")";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.executeUpdate(query);

        return true;
    }

    public static LinkedList<Friend> CheckFriendRequests (User user, Connection connection) throws SQLException {
        LinkedList<Friend> friendsRequested = new LinkedList<>();

        String query = "SELECT f.id_friend, f.id_user, f.id_user_friend, u.name, u.lastname FROM friend f\n" +
                "INNER JOIN user u ON f.id_user=u.id_user \n" +
                "WHERE f.id_user_friend=" + user.ID_User + " AND f.accepted=0;";

        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            Friend friend = new Friend();

            friend.ID_Friend = rs.getInt("id_friend");
            friend.ID_User = rs.getInt("id_user");
            friend.ID_User_Friend = rs.getInt("id_user_friend");
            friend.User_Friend.Name =  rs.getString("name");
            friend.User_Friend.Lastname =  rs.getString("lastname");

            friendsRequested.add(friend);
        }

        return friendsRequested;
    }

    public static boolean AcceptFriendRequest(Friend friend, Connection connection) throws SQLException{
        String query = "UPDATE friend SET `accepted` = '1' WHERE (`id_friend`=" + friend.ID_Friend + ");";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.executeUpdate(query);

        return true;
    }
}
