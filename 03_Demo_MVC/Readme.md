#Démonstration du pattern MVC pour une application Swing avec gestion de BDD
Le code source est présent dans le répertoire java.
N'oubliez pas de modifier les instructions `package` des différentes classes
pour s'accorder à celles de votre projet.

Le pom.xml fourni liste les 2 dépendances nécessaires (DbUtils et le driver SQL)

**N'oubliez pas de :**
- démarrer votre VM pour avoir un serveur MySQL
- créer un tunnel pour le port 3306 via ssh en faisant :
```
ssh -L 3307:localhost:3306 cefim@localhost -p 2222
```

De plus : lors de l'exécution de l'application, les effets du bouton peuvent mettre
quelques secondes à s'effectuer. N'hésitez pas à regarder les affichages console
pour vérifier qu'il n'y a pas d'erreur de connexion à MySQL.

Quelques éléments ajoutés dans ce projet :
- la gestion de la connexion SQL a été centralisée dans une classe SqlService
via une approche "Singleton"
- le controlleur s'occupe de déclencher la fermeture de la connexion SQL lorsque
la fenêtre est fermée via la méthode `addWindowListener` du JFrame.

Bonne lecture :)