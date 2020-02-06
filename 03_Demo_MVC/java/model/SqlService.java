package eu.cefim2.java.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
Totalement optionnel mais pratique :
Centraliser la gestion de vos connexions au sein d'une classe statique
 */
public class SqlService {

    private static Connection connection;

    public static Connection getConnection(){

        // Si la connexion n'existe pas encore : elle est créée
        if(connection == null){
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connect();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("com.mysql.cj.jdbc.Driver est indisponible");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Erreur à la connexion");
            }
        }

        return connection;

    }

    public static void closeConnection(){
        if(connection != null) {
            try {
                connection.close();
                System.out.println("Connexion à la base clôturée avec succès");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Erreur à la clôture de la connexion");
            }
        }
    }

    private static void connect() throws SQLException {
        String user = "root";
        String password = "cefim";
        String timezone = "UTC";
        String url = "jdbc:mysql://localhost:3307/eventcode?serverTimezone="+timezone;
        connection = DriverManager.getConnection(url, user, password);
    }

}
