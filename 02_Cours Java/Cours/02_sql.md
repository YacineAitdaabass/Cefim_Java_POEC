# SQL au sein de Java
Exécuter des requêtes SQL au sein de Java est facile.
Lier le résultat des requêtes à des objets Java est autrement plus compliqué.

## Initier et clôturer une connexion
Pour initier la connexion à une base SQL, il faut d'abord un driver propre au SGBD.
Nous avons déjà importé la dépendance du driver de MySQL pour Java via Maven.
Pour charger ce driver :

`Class.forName("com.mysql.cj.jdbc.Driver");`

On peut ensuite stocker notre connexion comme suivant : 
```
String user = "user";
String password = "password";
String timezone = "UTC"
String url = "jdbc:mysql://localhost:3306/eventcode?serverTimezone="+timezone;
Connection connection = DriverManager.getConnection(url, user, password);
```

Et il faudra toujours clôturer notre connexion en fin de programme comme ceci :
```
connection.close();
```

## Exécuter une requête
Chaque requête SQL va être soumise à une instance de la classe Statement.
```
String query = "INSERT INTO ...";
Statement stmt = connection.createStatement();
stmt.executeQuery(query);
```

Si la requête renvoie une information (SELECT)
on peut récupérer ces informations au sein d'un ResultSet :
```
...
ResultSet rs = stmt.executeQuery(requete);
```

Observez la classe ResultSet : il s'agit en fait d'une interface.
Celle-ci propose la méthode `next()` qui permet de passer au résultat suivant.
Dans les commentaires il est précisé que le curseur est initialement placé
avant le premier résultat. Il faut donc commencer par un `next()` pour accéder 
au premier résultat.
Un while est donc idéal :

```
while(rs.next()){
    // code ici
}
```

Il est ensuite possible de récupérer n'importe quel champ de la ligne actuelle
via sa position ou son nom, et en castant la valeur vers un type Java :
```
String prenom = rs.getString("prenom");
String nom = rs.getString(1);
int age = rs.getInt("age");
```

> Exercice : afficher pour chaque client une ligne en console avec :
>- email
>- nombre de billets achetés

## DAO (Data Access Object)
Bien que facilement accessibles, nos données obtenues sont dépourvue de toute logique
métier. Au lieu d'un ResultSet, il serait préférable d'avoir un résultat du type 
`List<Client>` où chaque ligne renferme un client.  
Cela nécessite 2 éléments supplémentaires :
- une classe Client
- un "mappeur" qui s'occupera de lier les données brutes issues de SQL à nos instances Client

Lorsque l'on crée une classe "simple" ayant une liste de champs privés avec un ensemble 
de getters/setters, on parle de Plain Old Java Object.
Il existe également les JavaBeans qui sont des POJO sérialisables, avec un constructeur sans argument,
et dont les getters et setters ont des noms déterminés par convention.

> Créez une classe Client avec :
>- les champs nécessaires pour refléter les données en base
>- les getters et setters pour chacun des champs
>- un constructeur sans argument  
> Puis rédigez un programme en Java qui aura pour effet de retourner
l'ensemble des clients sous la former d'un `List<Client>`

> Ajoutez les fonctionnalités suivantes à votre code :
>- la requête doit pouvoir s'effectuer sur tous les clients (méthode `find()`)
>ou sur un seul en spécifiant son id (méthode `findOne(int id)`)
>- ajoutez le champ nbBilletsAchetes à la classe Client et faites
>le nécessaire pour que vos 2 méthodes alimentent bien ce champ

Selon les choix réalisés, vous avez soit :
- ajouté ces 2 méthodes au sein de la classe Client : dans ce cas vous perdez 
la nature POJO de votre classe mais centralisez toute la logique en lien avec
les clients en un point
- créé une classe dédiée à ces méthodes : vous conservez le côté POJO
et découplez votre code, mais éclatez sa logique  
Les 2 approches ont leur avantages et inconvénients.

## ORM

Une problématique commence à apparaitre : au sein de l'application eventCode
le nombre de billets achetés peut être utile mais n'est pas systématiquement nécessaire
lorsque je requête des clients.
Si je poursuis sur le modèle actuel et que j'ajoute des dizaines d'autres champs 
obtenus via des jointures complexes, mes requêtes vont s'alourdir inutilement.
De plus, si mes jointures doivent hydrater 2 classes différentes, cela commence 
à devenir très compliqué.  
Pour éviter cela il y a 2 solutions principales :
- coder autant de méthodes (et donc de requêtes) que de cas d'usages 
pour charger le strict minimum à chaque fois
- utiliser un ORM qui va lui-même interpréter quand charger un champ, une jointure, etc
selon l'usage qui en sera fait dans le code

Cependant les ORM ont également un coût important de part leur très grande complexité
et le caractère parfois trop générique de certaines requêtes générées, qui nécessitera
de le surcharger en rédigeant soi-même la requête.

Si l'on souhaite se passer d'ORM il existe des compromis intéressants comme les
Jdbc templates au sein de Spring, ou plus simple encore : Apache DbUtils

## DbUtils
>Chargez la dépendance d'Apache DbUtils via Maven (prenez la version la plus récente)

### Handlers par défaut

DbUtils permet de mapper un résultat SQL sur un JavaBean de façon plus automatisée.
Pour cela il propose par défaut une classe BeanListHandler<E> qui va mapper un résultat
sur notre classe E.

```
BeanListHandler<Client> beanListHandler = new BeanListHandler<>(Client.class);
```  

DbUtils va ensuite réaliser un Statement avec notre requête et appliquer
le BeanListHandler. C'est la classe QueryRunner qui effectue cela :

```
QueryRunner runner = new QueryRunner();
String query = "SELECT * FROM client";
List<Client> clients = runner.query(connection, query, beanListHandler);
```

Il existe d'autres handlers par défaut : https://commons.apache.org/proper/commons-dbutils/apidocs/org/apache/commons/dbutils/ResultSetHandler.html  
En voici un exemple : on peut aussi récupérer directement une valeur scalaire (ex : un nombre de lignes)
via ScalarHandler<Long> :
```
ScalarHandler<Long> scalarHandler = new ScalarHandler<>();
QueryRunner runner = new QueryRunner();
String query = "SELECT COUNT(*) FROM client";
long count = runner.query(connection, query, scalarHandler);
```

### handlers customisés
Je souhaite maintenant récupérer une liste de clients ainsi que leurs billets achetés respectifs,
et que je puisse accéder aux billets achetés par un client via une méthode telle que :
```
public class Client{
    ...
    public List<Billet> getBilletsAchetes() {
        ...
    }
    ...
}
```

> Creéz la classe Billet en suivant les mêmes règles que pour Client.  
> Ajoutez le champ billetsAchetes dans la classe Client avec getter et setter appropriés

Notre BeanListHandler ne pourra pas mapper cette relation entre nos 2 classes.
Nous allons donc tester cette approche (via notre propre handler) :
- création de notre List<Client> comme précédemment
- puis pour chaque Client : SELECT sur la table billet pour obtenir les
billets achetés par le client
- ajout du billet au Client via un `addBilletAchete(billet);`

Nous créons notre handler ClientHandler en héritant de BeanListHandler 

```
public class ClientHandler extends BeanListHandler<Client> {
}
```

Il faudra lui fournir la connexion SQL en paramètre du constructeur.

``` 
    private Connection connection;
 
    public EmployeeHandler(Connection con) {
        super(Client.class);
        this.connection = con;
    }
```

Lorsque nous exécutons un QueryRunner, celui-ci exécute la méthode
handle du handler soumis en paramètre. C'est donc cette méthode qu'il faut surcharger : 

```
    @Override
    public List<Client> handle(ResultSet rs) throws SQLException {
        List<Client> clients = super.handle(rs);
 
        QueryRunner runner = new QueryRunner();
        BeanListHandler<Billet> handler = new BeanListHandler<>(Billet.class);
        String query = "SELECT * FROM billet WHERE achat_client_id = ?";
 
        for (Client client : clients) {
            List<Billet> billetsAchetes = runner.query(connection, query, handler, client.getId());
            client.setBilletsAchetes(billetsAchetes);
        }
        return clients;
    }
```

> Testez le code ci-dessus en accédant en affichant le code des billets du client dont l'id vaut 2.  

### Row processor
Parfois, le nom des champs côté BDD ne peut pas être reflété par le nom des champs
côté Java (camelCase vs snake_case, ou tout simplement pour des facilités de lecture, clarté, etc).

Dans ce cas il est possible de mapper un nom de colonne SQL vers un nom de champ Java.
Cela se fait au sein d'un handler. Il faut :
1) Lui ajouter une méthode permettant de mapper les champs :
    ```
   public static Map<String, String> mapColumnsToFields(){
   
           Map<String, String> columnsToFieldsMap = new HashMap<>();
           columnsToFieldsMap.put("champ_machin_truc", "champMachinTruc"); //SQL -> Java
   
           return columnsToFieldsMap;
   
       }
   ```
2) Préciser qu'il faudra utiliser cette méthode de mapping au sein de son constructeur
    ```
   public EvenementHandler(Connection con) {
       super(Evenement.class, new BasicRowProcessor(new BeanProcessor(mapColumnsToFields())));
       connection = con;
   }
   ```


> En vous basant sur l'exemple précédent,
>- créez les POJO pour les tables organisateur et evenement
>- rédigez le code pour afficher le mail de tous les organisateurs
>- rédigez le code pour afficher le nom et la date de chaque événement
>- faites le nécessaire pour que ceci soit possible après avoir récupéré la 
>liste des événements : 
>`Organisateur organisateur = evenement.getOrganisateur();`

### Insertions
Pour insérer des lignes, on utilise la méthode `insert()` du QueryRunner.
Celle-ci utilise un ScalarHandler afin de retourner la valeur de l'éventuelle
clé auto-incrémentée.

```
ScalarHandler<Integer> scalarHandler = new ScalarHandler<>();
QueryRunner runner = new QueryRunner();
String query = "INSERT INTO client (mail, password) VALUES (?, ?)";
int newId = runner.insert(connection, query, scalarHandler, "test@truc.fr", "6sd564sdf");
```

> Essayez le code ci-dessus

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
 