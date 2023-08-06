package org.mj.Database;

import java.sql.*;
import java.util.LinkedList;

import org.mj.Models.*;

public class DataBaseOperation {
    /**
     * laczenie sie z baza danych
     * @return Connection jesli nie udalo sie polaczyc zwraca null
     */
    public static Connection ConnectToDB(){
        String connectionUrl = "jdbc:mysql://mqttdb.mysql.database.azure.com:3306/chatdb";

        try {
            Connection conn = DriverManager.getConnection(connectionUrl, "adminansb", "Mqttdb1!");
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
        String checkLoginExists = "SELECT login FROM user WHERE login='" + user.Login + "';";
        String query = "INSERT INTO user (`login`, `password`, `name`, `lastname`) VALUES ('" + user.Login + "', '" + user.Password + "', '" + user.Name + "', '" + user.Lastname + "');";

        //PreparedStatement ps = connection.prepareStatement(query);
        PreparedStatement ps = connection.prepareStatement(checkLoginExists);
        ResultSet rs = ps.executeQuery();

        if(!rs.next()) {
            ps = connection.prepareStatement(query);
            ps.executeUpdate(query);

            System.out.println("nie ma takiego loginu");

            return true;
        }
        else{
            System.out.println("jest juz taki login");

            return false;
        }
    }

    /**
     * wyszukuje z DB jakich mamy znajomych
     * @param user uzytkownik dla ktorego szukamy friendow
     * @param connection
     * @return LinkedList<Friend> firendy z user
     * @throws SQLException
     */
    public static LinkedList<Friend> GetFriends(User user, Connection connection) throws SQLException {
        String query = "SELECT f.*, " +
                "CASE WHEN f.id_user=" + user.ID_User + " THEN u_friend.name ELSE u.name END as name, " +
                "CASE WHEN f.id_user=" + user.ID_User + " THEN u_friend.lastname ELSE u.lastname END as lastname, " +
                "CASE WHEN f.id_user=" + user.ID_User + " THEN u_friend.id_user ELSE u.id_user END as id_user_fk " +
                "FROM friend f " +
                "INNER JOIN user u ON u.id_user = f.id_user " +
                "INNER JOIN user u_friend ON u_friend.id_user = f.id_user_friend " +
                "WHERE ((f.id_user=" + user.ID_User + ") OR (f.id_user_friend=" + user.ID_User + ")) AND f.accepted = 1;";

        LinkedList<Friend> friends = new LinkedList<>();

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
            friend.User_Friend.ID_User = rs.getInt("id_user_fk");

            friends.add(friend);
        }

        return friends;
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

    /**
     * szuka uzytkownik po imieniu i nazwisku
     * @param searchString - string imie lub nazwisko zeby znalezc osobe
     * @param connection
     * @return LinkedList<User> uzytkownicy ktorzy pasuje do frazy szukajacej
     * @throws Exception
     */
    public static LinkedList<User> FindUser(String searchString, Connection connection) throws Exception{
        String[] splittedText = searchString.split(" ");
        String query;
        if(splittedText.length == 1){
            query = "SELECT id_user, name, lastname FROM user WHERE name LIKE '%" + splittedText[0] + "%' OR lastname LIKE '%" + splittedText[0] + "%';";
        } else if (splittedText.length == 2) {
            query = "SELECT id_user, name, lastname FROM user WHERE name LIKE '%" + splittedText[0] + "%' OR lastname LIKE '%" + splittedText[1] + "%';";
        } else{
          return null;
        }

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

    /**
     * Sprawdza czy juz mamy takiego znajomego
     * @return
     */
    public static boolean CheckFriend(User friend, User user, Connection connection) throws SQLException {
        String query = "SELECT f.id_friend FROM friend f\n" +
                "join user u ON u.id_user=f.id_user \n" +
                "join user uf ON uf.id_user=f.id_user_friend \n" +
                "WHERE (f.id_user=" + user.ID_User + " OR f.id_user_friend=" + user.ID_User + ") AND (u.lastname='" + friend.Lastname + "' AND u.name='" + friend.Name + "' OR uf.lastname='" + friend.Lastname + "' AND uf.name='" + friend.Name + "');";

        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        if(!rs.next()){
            return false;
        }else{
            return true;
        }
    }

    public static boolean AcceptFriendRequest(Friend friend, Connection connection) throws SQLException{
        String query = "UPDATE friend SET `accepted` = '1' WHERE (`id_friend`=" + friend.ID_Friend + ");";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.executeUpdate(query);

        return true;
    }

//    public static LinkedList<Message> GetMessages(User currentUser, Friend friend, Connection connection) throws SQLException{
//        String query = "SELECT * FROM message \n" +
//                "WHERE (id_user_sender=" + currentUser.ID_User + " AND id_user_receiver=" + friend.User_Friend.ID_User + ") OR (id_user_sender=" + friend.User_Friend.ID_User + " AND id_user_receiver=" + currentUser.ID_User + ")\n" +
//                "ORDER BY id_message DESC LIMIT 11;";
//
//        LinkedList<Message> messages = new LinkedList<>();
//
//        PreparedStatement ps = connection.prepareStatement(query);
//        ResultSet rs = ps.executeQuery();
//
//        while(rs.next()){
//
//            Message message = new Message();
//
//            message.ID_Message = rs.getInt("id_message");
//            message.ID_User_Sender = rs.getInt("id_user_sender");
//            message.Content = rs.getString("content");
//            message.Timestamp = rs.getTimestamp("ts");
//
//            messages.add(message);
//
//        }
//
//        return messages;
//    }

    public static LinkedList<Message> GetOldMessages(User currentUser, Friend friend, int lastIDMsg ,Connection connection) throws SQLException {
        String queryGetNext10Msgs = "SELECT * FROM message \n" +
                "WHERE ((id_user_sender=" + currentUser.ID_User + " AND id_user_receiver=" + friend.User_Friend.ID_User + ") OR (id_user_sender=" + friend.User_Friend.ID_User + " AND id_user_receiver=" + currentUser.ID_User + ")) AND id_message < " + lastIDMsg + "\n" +
                "ORDER BY id_message DESC LIMIT 11;";
        System.out.println(lastIDMsg);

        LinkedList<Message> messages = new LinkedList<>();

        PreparedStatement ps = connection.prepareStatement(queryGetNext10Msgs);
        ResultSet rs = ps.executeQuery();

        while(rs.next()){

            Message message = new Message();

            message.ID_Message = rs.getInt("id_message");
            message.ID_User_Sender = rs.getInt("id_user_sender");
            message.Content = rs.getString("content");
            message.Timestamp = rs.getTimestamp("ts");

            messages.add(message);
            System.out.println(message.ID_Message + " " + message.Content);
        }

        return messages;
    }

    public static boolean SendMessage(User currentUser, int ID_friend, String msg, Connection connection) throws SQLException{
        String query = "INSERT INTO message (`id_user_sender`, `id_user_receiver`, `content`) " +
                "VALUES ('" + currentUser.ID_User + "', '" + ID_friend + "', '" + msg + "');";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.executeUpdate(query);

        return true;
    }


}
