# Swing
Swing est une bibliothèque graphique qui permet de générer des fenêtre.
Il succède à AWT. JavaFX est le successeur officiel de Swing qui permet 
d'obtenir plus rapidement des applications au design affiné, mais 
certains éléments de Swing n'ont pas encore été implémentés en JavaFX.
Aujourd'hui, les 2 bibliothèques sont toujours utilisées (Swing et JavaFX).

## Installation
Aucune ! Swing est intégré à Java. Il suffit d'importer les bonnes dépendances dans votre code.

## Premier programme avec Swing
Le conteneur principal dans lequel on peut dessiner les éléments graphique 
est le JFrame. Pour concevoir une nouvelle fenêtre on va donc créer
une classe qui hérite de JFrame :
`public class MaFenetre extends JFrame {}`

Observez le contenu de la classe JFrame, nous allons exploiter les méthodes qu'il implémente.

Au sein du constructeur, définissez un titre pour la fenêtre et une taille :
```
setTitle("Ma fenêtre");
setSize(300, 400);
```
Pour centrer la fenêtre :
```
setLocationRelativeTo(null);
```
Enfin, nous aimerions qu'en cliquant sur la croix cela ferme notre fenêtre :
```
setDefaultCloseOperation(EXIT_ON_CLOSE);
```
Pour activer notre fenêtre : créer une méthode main (dans la même classe ou ailleurs) avec :
```
MaFenetre fenetre = new MaFenetre();
fenetre.setVisible(true);
```

## Aparté sur les lambdas
Les lambdas sont disponibles depuis Java 8.
Les lambdas sont une notation abrégée des interfaces fonctionnelles.  
Une interface fonctionnelle comprend une unique méthode abstraite.  
On va donc implémenter cette unique méthode abstraite via un lambda.

Voici un exemple d'interface fonctionnelle :
```
interface InterfaceFonctionnelle{
    int methodeAbstraite(int x);
}
```

Et voici comment implémenter cette interface au travers d'un lambda :
```
public class Truc {
    public void methodeMachin(){
        InterfaceFonctionnelle foisDeux = x -> 2*x;
        foisDeux.methodeAbstraite(5); // renvoie 10
    }
}
```

## Ajout de boutons
Ajoutons un bouton "Quitter" sur cette fenêtre.
Toujours au sein du constructeur :
```
JButton exitButton = new JButton("Quitter");
```
Le code suivant stoppe une application Java :
```
System.exit(0);
```
Le paramètre 0 est le code renvoyé à la fin du programme (0 = pas d'erreur).  
Il faut déclencher ce code lors du clic sur le bouton :
```
exitButton.addActionListener(e -> System.exit(0));
```
Il ne reste plus qu'à enregistrer notre bouton comme élément de la fenêtre :
```
add(exitButton);
```
Mais le rendu n'est pas terrible : le bouton prend toute la place.
On peut manuellement placer le bouton (avant la méthode add)
en x, y, largeur et hauteur via :
```
exitButton.setBounds(130, 100, 100, 40);
```
et désactiver la mise en page automatique :
```
.setLayout(null);
```
Le rendu est correct mais il sera très laborieux de placer à la main chaque élément.

## Ajouter une icône au JFrame
Vous pouvez placer vos ressources telles que les images dans le répertoire `src/main/resources`.  
Placez-y une image et importez-la via :
```
ImageIcon icon = new ImageIcon("src/main/resources/icon.png");
setIconImage(icon.getImage());
```

## Ajouter des tooltips
Les tooltips sont des contenus affichés au survol d'éléments.
Ajoutez un tooltip au bouton Quitter via :
```
exitButton.setToolTipText("Quitter l'application :(");
```

## Ajouter des mnemonics
Un mnemonic est un raccourci clavier, il peut activer un bouton, un libellé, un menu, etc.
Pour cela on précise la touche à presser en plus de la touche Alt
(on peut remplacer Alt par une autre touche via des éléments de configuration).

Pour quitter via le raccourci Alt+Q :
```
exitButton.setMnemonic(KeyEvent.VK_Q);
```

## Ajouter des libellés
De façon similaire à la création d'n bouton :
```
JLabel label = new JLabel("Hello World");
label.setBounds(130, 200, 100, 40);
add(label);
```

## Utiliser des couleurs
13 couleurs par défaut sont disponibles dans la classe java.awt.Color.

> Nous allons créer 2 labels et utiliser 2 couleurs de fond.
> Par défaut les labels sont transparents. Vous pouvez les rendre opaque via ;
> `monLabel.setOpaque(true);`  
> Puis attribuez leur une couleur via la méthode 
> `monLabel.setBackground(color);` où color est un des attributs de la classe statique Color. 

Si votre label ne possède pas de texte et que vous souhaitez lui imposer une taille minimale
pour le forcer à s'afficher :
```
label.setMinimumSize(new Dimension(100, 40));
```

## Les layouts
Les layouts permettent de faciliter la mise en page en établissant quelques règles.
En voici la liste :
https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html  
Supprimez la ligne `setLayout(null)` et toutes les lignes avec un `setBounds`.

Nous allons commencer par le GroupLayout qui répond à un grand nombre de situations.
Un GroupLayout de déclare avec comme paramètre le JPanel sur lequel il s'applique :
```
JPanel pane = (JPanel) getContentPane();
GroupLayout gl = new GroupLayout(pane);
pane.setLayout(gl);
```

Il faut ensuite décrire l'assemblage des composant sur l'axe vertical et sur l'axe horizontal.  
Détails ici : https://docs.oracle.com/javase/tutorial/uiswing/layout/group.html  
Et on fini par la méthode `pack()` pour appliquer le tout.


```
gl.setHorizontalGroup(gl.createParallelGroup()
        .addGroup(gl.createSequentialGroup()
                .addComponent(exitButton)
                .addComponent(label)
                .addComponent(label2)
        )
);

gl.setVerticalGroup(gl.createSequentialGroup()
        .addGroup(gl.createParallelGroup()
                .addComponent(exitButton)
                .addComponent(label)
                .addComponent(label2)
        )
);

pack();
```

Entre 2 addCmponent on peut ajouter de l'espace manuellement via un addGap(),
ou bien de façon automatique en réglant les gaps automatiquement : 

```
gl.setAutoCreateContainerGaps(true);
gl.setAutoCreateGaps(true);
```

> Exercice : créez une fenêtre avec 13 labels affichant 13 couleurs différentes.
>Ces labels doivent être organisés en 4 colonnes de 4 lignes via un GroupLayout.

## Ecouter les mouvements de la souris
Nous allons afficher les coordonnées de la souris en x, y sur notre fenêtre 

Commencez par ajouter un label vide nommé `coords` et placez le dans le Layout.

La méthode addMouseMotionListener permet de déclencher une action en cas de mouvement de la souris en
implémentant l'interface MouseMotionAdapter.
Seul hic... cette interface possède 2 méthode abstraites. On ne peut donc pas utiliser de lambda.
On pourrait l'implémenter classiquement, mais cela reviendrait à créer une classe pour chaque écoute d'événement (Adaptater).
Il y a un intermédiaire : les classes anonymes. Il s'agit là encore d'implémenter et d'instancier de façon expresse :
```
addMouseMotionListener(new MouseMotionAdapter() {
    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        int x = e.getX();
        int y = e.getY();
        String text = String.format("x: %d, y: %d", x, y);
        coords.setText(text);
    }
});
```

Essayez. Notez que seuls les mouvements effectués au sein de la fenêtre sont pris en compte.

Pour parvenir à ce résultat, IntelliJ nous aide dès lors que nous appliquons le mot clé `new` à une interface.
Il vous propose alors de pré-rédiger l'implémentation pour nous. Ici sélectionnez la méthode mouseMoved
dans la boîte de dialogue pour l'implémenter et complétez pour obtenir le résultat ci-dessus.

N"hésitez pas à repartir sur une nouvelle fenêtre pour la suite du cours.

## Ajouter un menu JMenuBar
Un JMenuBar comprend plusieurs éléments JMenu, lesquels déplient des JMenuItem qui peuvent comporter :
- un texte
- une image
- un raccourci clavier (mnemonic)
- un ActionListener
- un tooltip
- etc

Voici un exemple :
```
JMenuBar menuBar = new JMenuBar();

JMenu fileMenu = new JMenu("Fichier");
fileMenu.setMnemonic(KeyEvent.VK_F);

JMenuItem openItem = new JMenuItem("Ouvrir");
ImageIcon dbIcon = new ImageIcon("src/main/resources/db.png");
openItem.setIcon(dbIcon);
openItem.setMnemonic(KeyEvent.VK_O);
fileMenu.add(openItem);

JMenuItem exitItem = new JMenuItem("Quitter");
exitItem.setMnemonic(KeyEvent.VK_Q);
exitItem.setToolTipText("Quitter définitivement l'application :o");
exitItem.addActionListener(e -> System.exit(0));
fileMenu.add(exitItem);

menuBar.add(fileMenu);

setJMenuBar(menuBar);
```

Les JMenu peuvent être placés au sein d'autres JMenu.
Dans ce cas, ils génèrent des sous menus et affichent les JMenuItem enfants au survol.

> Concevez une fenêtre avec l'aborescence suivante de menus :
> - Fichier
>   - Nouveau
>       - Projet
>       - Fichier
>   - Ouvrir
> - Préférences
>   - Langue
>       - Anglais
>       - Français
>           - Canada
>           - France
>           - Suisse
>           - Belgique
>   - Affichage

## Les Threads
Voir cours dédié puis continuer ici

Au sein d'une application Swing, il peut y avoir 3 types threads :
- Initial thread : le thread principal, par lequel notre code débute
- Event dispatcher thread : le thread dans lequel tout le code propre à l'UI
et toutes les actions qui découlent des événements (clics, mnemonics, etc) tant que 
ceux-ci sont instantanés
- Worker threads : les threads d'arrière plan qui gèrent les tâches coûteuses,
c'est-à-dire toutes celles qui ne sont pas instantanées (ex : mise à jour BDD)

Ignorer la gestion des threads pour une application graphique posera plusieurs problèmes,
dont le freeze de l'interface lorsque certaines actions seront réalisées en arrière plan.

> Concevez une application graphique avec une fenêtre, 1 label et 2 boutons :
> - le label doit afficher un compteur initialisé à 0
> - le 1er bouton doit incrémenter cette valeur de 1 instantanément
> - le 2e bouton doit patienter 3 secondes (pour simuler une opération longue puis
>incrémenter le compteur de 1  
> Testez votre application en alternant l'usage des 2 boutons, que remarquez-vous ?

Pendant les 3 secondes de sommeil, toute l'application est en pause (y compris
la croix pour mettre fin à l'application !)

Il y a 2 problèmes dans notre application :
- notre GUI nest géré par le Thread principal et non celui de l'Event dispatcher
- notre 2e bouton effectue une action non instantanée, il faut donc qu'il
utilise un Worker Thread

### Event dispatcher thread
 
Pour initier l'Event dispatcher thread, Swing propose une méthode clé en main :

```
SwingUtilities.invokeLater(new Runnable() {
    public void run() {
        //code ici
    }
});
```

On peut à nouveau utiliser un lambda à la place :
```
SwingUtilities.invokeLater(() -> {
    //code ici
});
```

Pour vérifier si une portion de code s'exécute bien dans l'Event dispatcher thread,
vous pouvez appeler la méthode statique suivante :
```
SwingUtilities.isEventDispatchThread()
```

> Dans votre application précédente, affichez dans la console
>le résultat de la méthode `SwingUtilities.isEventDispatchThread()`
>depuis le constructeur de votre JFrame.  
>Puis recommencez mais cette fois en créant et en rendant visible votre
>JFrame au sein de la méthode `SwingUtilities.invokeLater()`

### Worker thread
> Dans votre application précédente, faites le nécessaire
>pour que le code déclenché par le clic sur le 2e bouton (avec un sleep)
>soit exécuté dans un thread dédié et testez votre application.

Cela a bien réglé le problème de freezing mais il reste 2 améliorations possibles.

1) Imaginez que le code pour le bouton 2 soit :
    ```
    int tmp = compteur;
    Thread.sleep(3);
    compteur = tmp + 1;
    ```
   Nous aurions alors le phénomène suivant :
   - en cliquant sur le bouton 2 puis tout de suite sur le bouton 1 :
       - le compteur passe à 1
       - les 3 secondes s'écoulent
       - le compteur reste à 1  
       Le bouton 2 a récupéré la valeur 0 puis a ajouté 1, et a écrasé l'action du bouton 1.
       Nous allons voir comment éviter ces problèmes de concurrence de 2 threads sur une même variable
       (suite du cours sur les Threads)
2) Nous avons créé un Thread dédié, mais il est possible de créer un Worker thread, qui offre
plus d'options

La classe `javax.swing.SwingWorker` permet de créer un Worker thread. Il s'agit d'un thread
avec des méthodes supplémentaires très pratiques :
- `done()` : portion de code exécutée par l'EDT lorsque le thread est terminé
- `publish()` : permet d'exécuter du code depuis l'EDT avant la fin du thread (utile pour les barres
de progression par exemple)
- propriétés bound : déclenchent des événements lorsqu'elles sont modifiées
- implémente l'interface Future : permet de renvoyer une valeur à l'EDT lorsque le thread est terminé

Voici un exemple :
```
import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MaFenetre extends JFrame {

    public MaFenetre() throws HeadlessException {

        setTitle("Ma fenêtre");
        setSize(300, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JLabel compteurLabel = new JLabel();
        add(compteurLabel);

        JButton startWorkerButton = new JButton("Start Worker");

        startWorkerButton.addActionListener(e -> {

            // Instanciation du worker
            SwingWorker worker = new SwingWorker<String, Double>(){

                // Méthode qui sera exécutée au démarrage du worker
                @Override
                protected String doInBackground() throws Exception {

                    int n;
                    int nbIterations = 11;
                    for (n = 0; n < nbIterations; n++) {
                        Thread.sleep(1000);

                        //publish déclenche la méthode process
                        publish((double) n / nbIterations * 100);
                    }
                    return n + " itérations réalisées";
                }

                // process peut accéder et modifier l'EDT
                @Override
                protected void process(List<Double> chunks) {
                    DecimalFormat df = new DecimalFormat("###.##");
                    String pourcentage = df.format(chunks.get(0)) + "%";
                    compteurLabel.setText(pourcentage);
                }

                // done est exécuté lorsque doInBackground est terminé
                // et peut accéder/modifier l'EDT
                @Override
                protected void done() {
                    try {
                        // get() retourne la valeur envoyée par doInBackground()
                        compteurLabel.setText(get());
                    } catch (InterruptedException | ExecutionException ex) {
                        ex.printStackTrace();
                    }
                    System.out.println("Worker terminé");
                }
            };

            // Exécution du worker
            worker.execute();
        });
        add(startWorkerButton);

        JPanel pane = (JPanel) getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);

        gl.setHorizontalGroup(gl.createParallelGroup()
                .addGroup(gl.createSequentialGroup()
                        .addComponent(compteurLabel)
                        .addComponent(startWorkerButton)
                )
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup()
                        .addComponent(compteurLabel)
                        .addComponent(startWorkerButton)
                )
        );

        pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MaFenetre fenetre = new MaFenetre();
            fenetre.setVisible(true);
        });
    }
}
```

## JTextField et JPasswordField

Reprenons avec d'autres éléments de base de Swing : les champs de texte.


https://www.javamex.com/tutorials/threads/invokelater.shtml
http://zetcode.com/tutorials/javaswingtutorial/menusandtoolbars/

## Modal et Barre de progression

## JTable

### Table simple
La façon la plus simple pour créer une table est la suivante :
```
String[] columnNames = {
        "Nom",
        "Prénom",
        "Age",
        "Sexe",
        "Adhésion"
};

Object[][] data = {
        {"Holden", "James", 20, "M", true},
        {"Bond", "James", 40, "M", false},
        {"Cotillard", "Marion", 30, "F", true},
        {"Marceau", "Sophie", 40, "F", false},
};

JTable table = new JTable(data, columnNames);
```

Cependant il y a une énorme limitation : toutes les cellules sont éditables
et le sont en tant que String. Nous verrons comment changer cela.

Remarquez qu'il est possible de sélectionner des lignes via SHIFT+clic, CTRL+clic
ou clic-glissé.

### Ajouter un conteneur pour scroller
Pour limiter la taille de la fenêtre et ajouter un ascenseur :
```
JScrollPane scrollPane = new JScrollPane(table);
table.setFillsViewportHeight(true);

scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

add(scrollPane);
```
Nécessite de ne pas placer notre JTable dans la fenêtre (via add ni layout) mais
bien le JScrollPane qui l'encapsule.

### Modifier le type des colonnes
Pour cela il faut créer son propre modèle de table.
Par défaut, un JTable est créé à partir de la classe modèle AbstractTableModel.
Nous allons créer notre propre table qui va en hériter.

```
JTable table = new JTable(new AbstractTableModel() {

    private String[] columnNames = {
            "ID",
            "Nom",
            "Prénom",
            "Age",
            "Sexe",
            "Adhésion"
    };

    private Boolean[] editable = {
            false,
            true,
            true,
            true,
            true,
            false
    };

    // pour des données en base, on pourra
    // les charger à partir d'un constructeur faisant appel à méthode BDD au sein d'un Thread dédié
    private Object[][] data = {
            {1, "Holden", "James", 20, "M", true},
            {2, "Bond", "James", 40, "M", false},
            {3, "Cotillard", "Marion", 30, "F", true},
            {4, "Marceau", "Sophie", 40, "F", false}
    };

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return editable[columnIndex];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // déclenchement du code SQL ici
        // + validation des données (ex : pas d'âge négatif, etc)
        // si ok :
        data[rowIndex][columnIndex] = aValue;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }
});
```

### Et plein d'autres fonctionnalités
https://docs.oracle.com/javase/tutorial/uiswing/components/table.html