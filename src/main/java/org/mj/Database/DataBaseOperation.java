package org.mj.Database;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

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
        String query = "SELECT f.*, u.name, u.lastname FROM chatdb.friend as f \n" +
                "INNER JOIN chatdb.user as u ON f.id_user_friend=u.id_user\n" +
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

            friends.add(friend);
        }

        return friends;
    }
}
