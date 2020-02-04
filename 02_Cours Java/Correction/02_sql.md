# SQL au sein de Java

## Initier et clôturer une connexion
> Exercice : afficher pour chaque client une ligne en console avec :
>- email
>- nombre de billets achetés

## DAO (Data Access Object)
> Créez une classe Client avec :
>- les champs nécessaires pour refléter les données en base
>- les getters et setters pour chacun des champs
>- un constructeur sans argument  
> Puis rédigez un programme en Java qui aura pour effet de retourner
> l'ensemble des clients sous la former d'un `List<Client>`

> Ajoutez les fonctionnalités suivantes à votre code :
>- la requête doit pouvoir s'effectuer sur tous les clients (méthode `find()`)
>ou sur un seul en spécifiant son id (méthode `findOne(int id)`)
>- ajoutez le champ nbBilletsAchetes à la classe Client et faites
>le nécessaire pour que vos 2 méthodes alimentent bien ce champ

## DbUtils
>Chargez la dépendance d'Apache DbUtils via Maven (prenez la version la plus récente)

### handlers customisés
> Creéz la classe Billet en suivant les mêmes règles que pour Client.  
> Ajoutez le champ billetsAchetes dans la classe Client avec getter et setter appropriés  

> En vous basant sur l'exemple précédent,
>- créez les POJO pour les tables organisateur et evenement
>- rédigez le code pour afficher le mail de tous les organisateurs
>- rédigez le code pour afficher le nom et la date de chaque événement
>- faites le nécessaire pour que ceci soit possible après avoir récupéré la 
>liste des événements : 
>`Organisateur organisateur = evenement.getOrganisateur();`


### Updates et Deletes
Les updates et deletes sont effectués via les méthodes `update()` et `delete` du runner.
Le runner renvoie alors le nombre de lignes modifiées.
```
String pattern = "%.fr"; 
QueryRunner runner = new QueryRunner();
String query = "UPDATE client SET password = 'newPassword' WHERE mail LIKE ?";
int numRowsUpdated = runner.update(connection, query, pattern);
```


Imaginons le scénario suivant : vous avez créé une instance de Client.
Celle-ci peut être un nouveau client, ou bien un client déjà en base pour lequel
vous avez effectué une modification.
Dans tous les cas vous aimeriez persister cette modification ou création en base.
Pour cela vous aimeriez pouvoir rédiger : `client.persist();`
S'il s'agit d'un insert, votre instance de Client doit ensuite obtenir un id identique
à celui en base.

> Rédigez le code nécessaire afin qu'une telle méthode puisse gérer la création
>comme la modification 
 