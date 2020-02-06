package eu.cefim2.java.controller;

import eu.cefim2.java.model.Client;
import eu.cefim2.java.model.ClientDAO;
import eu.cefim2.java.model.SqlService;
import eu.cefim2.java.vue.AccueilVue;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccueilController {

    private static List<Client> clients = new ArrayList<>();

    public static void start() {

        // Thread = Event Dispatcher (UI)
        SwingUtilities.invokeLater(() -> {
            //Instantiation de la fenêtre
            AccueilVue fenetre = new AccueilVue();


            /*
            En cas de clic sur le bouton : charge tous les clients
            et en affiche le nombre
             */
            fenetre.loadClientsButton.addActionListener(e ->
                    // Les opérations SQL n'étant pas instantanées : création d'un Thread
                    (new Thread(() -> {
                        try {
                            System.out.println("Connexion à la base de données");
                            clients = ClientDAO.findAll();
                            fenetre.nbClientsLabel.setText("Il y a " + clients.size() + "client(s) en base");
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    })).start()
            );


            fenetre.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    SqlService.closeConnection();
                }
            });

            // Affichage de la fenêtre
            fenetre.setVisible(true);

        });
    }

}
