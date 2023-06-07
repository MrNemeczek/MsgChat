package org.mj.Database;

import java.sql.*;

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
    public static int Login(String login, String password, Connection connection) throws SQLException {
        String query = "SELECT id_user FROM user WHERE login=\"" + login + "\" AND password=\"" + password + "\"";

        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        if(rs.next()){
            return rs.getInt("id_user");
        }
        //TODO: zrobic zeby zwracalo User i jesli fail logowanie to null
        return 0;
    }
}
