package eu.cefim2.java.vue;

import javax.swing.*;
import java.awt.*;

public class AccueilVue extends JFrame {

    // Seuls ces 2 composants sont accessibles au controlleur
    public JButton loadClientsButton;
    public JLabel nbClientsLabel;

    public AccueilVue() {
        loadClientsButton = new JButton("Compter le nombre de clients");
        nbClientsLabel = new JLabel("<-- Clique sur ce bouton !");

        setTitle("Démo MVC");
        setMinimumSize(new Dimension(600, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        defineLayout();
    }

    // Regroupement du layout (pour plus de lisibilité)
    private void defineLayout() {
        JPanel pane = (JPanel) getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);

        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);

        gl.setHorizontalGroup(gl.createParallelGroup()
                .addGroup(gl.createSequentialGroup()
                        .addComponent(loadClientsButton)
                        .addComponent(nbClientsLabel)
                )
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup()
                        .addComponent(loadClientsButton)
                        .addComponent(nbClientsLabel)
                )
        );
    }
}
