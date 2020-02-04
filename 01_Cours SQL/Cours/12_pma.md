# PhpMyAdmin

## Transformations
Permet de modifier l'affichage d'une colonne selon la nature de son contenu.
Exemple : afficher un lien si la valeur est une URL, afficher une image s'il s'agit d'une image encodée
en chaîne de caractères.
https://docs.phpmyadmin.net/en/latest/transformations.html

Les transformations permettent également de faciliter l'insertion de valeurs (exemple : menu Parcourir 
pour uploader une image en base)

Pour mettre en place une transformation, une colonne doit avoir :
- un type MIME (nécessaire pour que la tranformation fonctionne) 
- une transformation d'affichage
- éventuellement une option de transformation
- éventuellement une transformation de saisie (pour l'insertion)
- éventuellement une option de transformation de saisie

Pour consulter la liste de transformations possibles et leurs éventuelles options :
http://localhost:8080/phpmyadmin/transformation_overview.php

Créez une table avec les colonnes suivantes :
  - id (INT avec une clé primaire auto incrémentée)
  - image (BLOB) avec :
      - type MIME = image/jpeg
      - transformation d'affichage = Inline (image/jpeg: Inline)
      - transformation de saisie = Image upload (image/jpeg: Upload) 
  - json (BLOB) avec :
      - type MIME = text/plain
      - transformation d'affichage = JSON (text/plain: Json)
      - transformation de saisie = JSON (text/plain: JsonEditor)

Puis insérez une ligne avec un jpeg et un json quelconque.
Vous remarquez qu'il est possible d'uploader un fichier via le disque et qu'un éditeur
de JSON est proposé pour sa saisie. A l'affichage de la table on retrouve bien
l'image et une coloration adaptée du JSON. 

## Colonnes centrales
Lorsque vous vos tables et champs, vous êtes fréquemment amenés à redefinir un grand nombre de fois 
le même champs dans différentes tables (exemple : colonne email, nom, prenom, numéro de série...).
Créer manuellement ces champs est à risque d'erreur (votre email pourrait être un VARCHAR(50) sur une table et un
VARCHAR(30) sur une autre, ou pire : un TEXT). Pour cela, vous pouvez définir les champs redondant une unique fois
dans les [Colonnes Centrales] et y faire appel à chaque nouvelle définition de table.

1) Allez dans l'onglet [Colonnes Centrales]
2) Créez une colonne nommée email (VARCHAR(50) NULLABLE)
3) Créez une nouvelle table en réutilisant le champ email précédent (cliquez sur "Choisir à partir d'une colonne
    centrale", en dessous du nom du futur champ)

Note : il y a actuellement un bug sur la version 5.0-rc1 pour cette fonctionnalité (mais testé avec succès sur PMA 4.9)

**Attention** : les colonnes centrales n'ont d'utilité que lors de la création de colonnes.
Si vous modifiez une colonne centrale, vos modifications ne seront pas répercutées sur chaque champ qui l'utilise.
PMA et MySQL ne concervent aucun lien entre les colonnes centrales et leurs différentes instantiations.   

## Bookmarks
https://docs.phpmyadmin.net/en/latest/bookmarks.html

Toute requête exécutée via PMA peut être sauvegardée dans un carnet (bookmarks ou signés en français, 
stocké en base).
Il est même possible de sauvegarder des requêtes à trou via l'insertion de variables.

1) Sélectionnez la base cefim dans PMA
2) Exécutez le script suivant via l'onglet SQL
  ```
   CREATE TABLE examen
   (
       nom VARCHAR(20),
       prenom VARCHAR(20),
       examen VARCHAR(30),
       score INT,
       score_max INT,
       date DATETIME,
       commentaire TEXT
   );
   INSERT INTO examen
   (
       nom,
       prenom,
       examen,
       score,
       score_max,
       date,
       commentaire
   ) VALUES (
       'Bidule',
       'Robert',
       'Java',
       92,
       100,
       '2019-01-10',
       'Avec félicitations du jury'
   ), (
       'Truc',
       'Gérard',
       'SQL',
       NULL,
       50,
       '2019-01-15',
       'Copie perdue :O'
   )
   ;
   ```
3) Rédigez et exécutez une requête permettant de sélectionner les lignes où le score
  est entre 80 et 100
4) En bas de la page des résultats, renseignez un intitulé de signet
  et cliquez sur "Conserver cette requête dans les signets"\
  **Renseignez absolument un intitulé, sinon une erreur 500 se produira**
5) Retournez dans l'onglet SQL : votre requête est maintenant disponible
dans la section "Requêtes SQL en signets".

Lors de la création de signet vous pouvez préciser si celui-ci est propre au user actuel ou
s'il est disponible pour tout user. Ces signets sont stockés dans la table phpmyadmin.pma__bookmark

- Créez un 2e signet avec la même requête mais rendez cette fois-ci les bornes
  80 et 100 dynamiques à l'aide de variables (voir doc https://docs.phpmyadmin.net/en/latest/bookmarks.html)\
  Indice : n'oubliez pas que la requête doit être valable après suppression de la partie commentée via `/* */`
- Exécutez le signet et remplaçant les bornes par 40 et 60 : une fois le signet sélectionné,
  vous devriez pouvoir renseigner vos 2 variables

Solution :
```
SELECT * FROM examen/* WHERE score BETWEEN [VARIABLE1] AND [VARIABLE2]*/;
``` 
 
## User management
https://docs.phpmyadmin.net/en/latest/privileges.html

### Création d'un User

Dans le cadre d'une application, on crée généralement une base de données dédiées, ainsi que 
un ou plusieurs comptes utilisateurs dédiés (exemple : un compte utilisé par l'appli,
un en lecture seule pour effectuer des stats à la main, etc)

PS : il existe une pratique consistant à utiliser une seule base de données pour N applications.
Dans ce cas on préfixe les tables par le nom de l'application (ex : "wp_" pour les tables 
concernant wordpress, "wm_" pour celles du webmail, etc).
Cette pratique est fortement déconseillée car elle complexifie le découpage des tables et les 
règles de sécurité à mettre en place (pour que l'application A n'accède pas aux données de celle de B).
Cette pratique est née d'offres en ligne restrictives ne permettant que la création d'une unique base.
Vous n'aurez pas de restrictions sur des offres pro / hébergement en interne, donc utilisez
une base par application/projet.

1) Comptes Utilisateurs > Ajouter un compte utilisateur
2) Saisir un username
3) Renseigner l'hôte : pour des raisons de sécurité, le seul hôte autorisé devrait être
    celui de l'application car c'est elle qui se connectera à la base. L'application
    peut être hébergée sur la même machine que la base (dans ce cas on mettra `localhost`,
    c'est ce que nous ferons ici), ou sur une autre machine (dans ce cas on mettre son IP).\
    Cependant on peut être amené à accéder "en tant qu'humain" à la base de données. Si on passe
    par PMA, c'est ce dernier qui va accéder à la base pour nous (depuis localhost), donc ça 
    fonctionne.
    
    Note : On peut aussi être amené à accéder à la base depuis une application installée
    localement sur notre poste (IDE, HeidiSQL, etc). Dans ce cas la connexion sera faite 
    depuis notre poste de travail. Renseigner l'IP de notre poste de travail pour le user sql 
    pose plusieurs problèmes :
   
    - on crée un risque sécuritaire : un utilisateur malveillant sur le même poste ou même
    routeur (= même IP publique) pourrait essayer de se connecter
    - en cas de déplacement notre IP change, on ne pourrait plus se connecter
        
    On conseille généralement de créer des tunnels SSH : l'application (IDE, autre) se 
    connecte en SSH puis exécute les commandes nécessaires pour accéder au shell de mysql.
    Ainsi, c'est comme si les commandes étaient exécutées en localhost.
    On peut reprendre ce principe pour tout type d'interaction avec le serveur (n'autoriser que
    localhost) et ainsi on peut concentrer toutes les mesures de sécurité sun l'accès SSH 
    (VPN, certificat de sécurité, passphrase, etc).
    
    PS : lorsque nous accédons au shell mysql via cmder en SSH, c'est un "tunnel SSH". Un IDE
    peut être configuré pour exécuter les mêmes commandes en arrière plan.
4) Renseigner un mot de passe (2 fois) : mettre un mdp simple pour l'exercice. Sinon vous pouvez
    utiliser le bouton [Générer] en base du formulaire
5) Extension : renseignez Authentification MySQL native pour permettre l'accès à PMA au user
6) Base de données :

    - [Créer une base portant son nom ...] : permet de créer une base et un user dans la foulée
    - [Accorder tous les privilèges à un nom passe partout...] : accorde au user tous les droits
    aux bases préfixées par "nomduuser_" : permet au user de créer de nouvelles bases sans lui
    accorder des droits globaux (il ne voit pas les bases qui ne lui appartiennent pas)
    
   Pour l'exercice ne cochez aucune des 2 options
7) Privilèges globaux : ne cochez rien pour l'exercice
8) Cliquez sur Exécuter

### Attribution des droits au user
Votre user créé, nous allons lui attribuer une base de données avec tous les droits

1) Créer une nouvelle base de données vide
2) Retournez dans [Comptes Utilisateurs] et éditez les privilèges de votre nouveau user
3) Dans l'onglet [Base de Données], attribuez les provilèges à votre nouvelle base
4) Sur la page suivante, attribuez tous les droits (cochez "Tout cocher")

TODO: FLUSH ?? 

### Note sur les groupes d'utilisateurs

Il existe un onglet [Groupes d'Utilisateurs] dans la section [Comptes Utilisateurs].
Celui-ci permet de limiter les onglets possibles dans PMA pour un groupe d'utilisateurs.
**Attention** il ne s'agit que de l'affichage des onglets. Exemple : si vous enlevez 
l'onglet [Utilisateurs] pour le user X, celui-ci pourra tout de même créer des users via 
la rédaction de requêtes SQL. Il s'agit donc d'un complement à la gestion des droits
(permet de ne pas afficher une option pour laquelle un user n'a pas les droits) mais ça ne
les remplace en aucun cas. 

## Relations
https://docs.phpmyadmin.net/en/latest/relations.html

Il est possible de gérer les relations (contraintes de clé) via PMA avec 2 outils.
Cela n'est possible qu'entre des tables utilisant le moteur InnoDB.

### Vue relationnelle
1) Pour les tester nous allons créer les 2 tables suivantes :
   - livre
      - id : INT(11), clé primaire, auto incrémenté
     - auteur : INT(11)
   - titre : VARCHAR(50)
  - auteur
      - id : INT(11), clé primaire, auto incrémenté
     - nom : VARCHAR(50)
      - prénom : VARCHAR(50)
2) Nous allons lier les auteurs (table parent) aux livres (table enfant).\
  Sur la table `livre`, allez dans l'onglet [Structure] puis le sous onglet [Vue Relationnelle]
3) Renseignez une contrainte entre les champs `livre.auteur` et `auteur.id`
4) Choisissez la colonne à afficher à la place de `livre.auteur` : `auteur.`
5) Insérez un nouvel auteur dans la table `auteur`
6) Insérez un nouveau livre : le champ auteur est un menu déroulant vous permettant de chercher
  parmi les auteurs existants

Note : les auteurs sont nommés selon leur colonne nom (en plus de l'id) dans le menu déroulant.\
Vous pouvez changer la colonne identifiant l'auteur depuis l'onglet [Vue Relationnelle] 
de la table `auteur` 

### Designer / Concepteur
1) Depuis la page de la base créée précédemment, accédez à l'onglet [Concepteur]\
Vos 2 tables apparaissent avec une relation One-to-Many matérialisé.
Les relations peuvent être affichées ou masquées via le bouton "Commuter les lignes des relations"
2) Vous pouvez modifier le deign des liens en cliquant sur l'option 
"Liens angulaires/Liens directs" (parfois plus lisible)
3) Pour filtrer les tables à afficher, cliquer sur "Afficher/Masquer la liste des tables".
Un menu listant les tables avec une option pour les masquer apparait
4) Vous pouvez déplacer les tables via drag'n drop. Pour plus de précision vous pouvez activer
l'option "Accrocher à la grille"
5) Les tables peuvent être réduites en cliquant sur leur bouton [^] ou via l'option "Agrandir/Réduire tout"
6) Les champs surlignés en rouge sont ceux utilisés pour représenter les tables au travers des
relations, exemple : un auteur est identifié par son nom (comme vu au paragraphe précédent)\
Vous pouvez changer cette colonne via le menu de gauche : option "Choisir la colonne à afficher"
7) Si vous devez créer des relations avec des tables en dehors de la base actuelle, cliquez
sur "Ajouter des tables depuis d'autres bases de données"  
8) Supprimez la relation actuelle : cliquez dessus puis sur supprimer dans la boîte de dialogue
9) Recréez la relation avec le Concepteur :
    - dans le menu de gauche cliquez sur Nouvelle relation
    - cliquez sur la clé primaire de la table parent (auteur.id)
    - puis sur la clé étrangère de la table enfant (livre.auteur)
    - renseignez les contraintes souhaitées dans la boîte de dialogue
10) Le résultat peut être sauvegardé/rechargé via les boutons "Enregistrer la page", "Ouvrir la page"
11) Le résultat peut aussi être exporté en PDF/SVG/EPS/Dia via le bouton "Exporter le schéma"

Note : pour exporter vers unlogiciel d'UML, seul le DDL est nécessaire. Vous pouvez l'obtenir
via des requêtes SHOW CREATE ou plus simplement via le menu d'export de PMA.



## Charts
https://docs.phpmyadmin.net/en/latest/charts.html

## Import et Export
https://docs.phpmyadmin.net/en/latest/import_export.html

## Authentification à 2 facteurs
https://docs.phpmyadmin.net/en/latest/two_factor.html

Les authentifications 2FA améliorent drastiquement la sécurité. Voici son fonctionnement sous PMA.

1) Connectez vous à PMA avec le user pour lequel vous souhaitez activer le 2FA
2) Allez dans l'onglet Paramètres > Authentification à 2 facteurs
3) Choisissez l'option Google Authentificator
4) Depuis votre smartphone, installez Google Authentificator (ou application similaire)
5) Scannez le QR code depuis l'application et renseignez dans PMA le code à 6 chiffres correspondant
6) Validez puis déconnectez vous
7) Reconnectez vous avec le même compte : cette fois-ci il vous sera demandé un code à 6 chiffres
en plus du mot de passe. Celui-ci est disponible dans Google Authentificator et est modifié toutes les 
30 secondes

Il est également possible de paramétrer l'authentification à 2 facteur avec une clé USB comem 2e facteur
(voir documentation de PMA)  

## Evènements
## Suivi
## Procédures stockées
## Déclencheurs
## Requête
Permet de générer des requêtes SQL à l'aide de formulaire et/ou du Concepteur.
Peu/pas d'intérêt si maitrise du langage SQL

## Import / Export
L'import et l'export de bases et/ou tables (vides ou avec données) est une des fonctions 
les plus utiles de PMA.

### Export

1) Pour exporter plusieurs bases à la fois, placez-vous sur la page d'accueil de PMA.\
Pour exporter une ou plusieurs tables d'une unique base, placez-vous sur la page de la base
correspondante.\
Pour exporter une table précise, palcez vous sur la page de la table en question.\
Pour l'exemple placez-vous sur la base de données de votre choix, celle-ci doit contenir au 
moins une table avec une ligne.
2) Cliquez sur l'onglet [Exporter]
3) La méthode "rapide" exporte tout le périmètre désiré (toutes les base, ou la base sélectionnée).\
La méthode "personnalisée" permet de sélection les bases/tables à exporter et un certain nombre 
d'autres paramètres : sélectionnez ce mode
4) Choisissez le format d'export (SQL pour un backup des données, CSV pour ouvrir dans un tableur, etc).
Ici laissez SQL pour l'exemple
5) Sélectionnez toutes les tables de la base, et veillez à ce que Structure et Données sont bien cochés.\
Si besoin vous pouvez n'exporter que l'un ou l'autre (CREATE TABLE ou INSERT)
6) Cochez l'option "CREATE DATABASE / USE" : cela permet d'ajouter la requête créant la base dans 
l'export SQL. Dans le cas contraire seules les requêtes propres aux tables sont exportées.
7) Cliquez sur [Exécuter] : le fichier est téléchargé, étudiez son contenu à l'aide Notepad++

### Import

Nous allons réimporter les données précédemment exportées

1) Commencez par supprimer la base en question : Base > Opérations > Supprimer la base de données
2) Accédez à l'onglet [Importer]
3) Cliquez sur [Choisir un fichier] et sélectionnez l'export précédent
4) Laissez les paramètres par défaut et cliquez sur [Exécuter]

Notes :
- si vous n'avez pas coché l'option "CREATE DATABASE / USE" lors de l'export, vous devez au préalable
créer la base en question et vous y placer pour exécutez l'export. Dans le cas contraire une erreur
vous prévidnra qu'aucune base n'a été sélectionnée.
- lors de l'import de bases, les fichiers sql envoyés peuvent être très volumineux.
Il est impératif d'avoir effectué les modifications nécessaires dans le fichier php.ini,
sinon une limite par défaut de 2 Mo par fichier importé est appliquée.

## Etat
Plein de stats cruciales pour optimiser le serveur

Notamment les sous onglets "Toutes les variables d'état", "Surveillance" et "Conseiller"