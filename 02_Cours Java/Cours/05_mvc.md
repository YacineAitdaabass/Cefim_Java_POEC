# Pattern MVC

Le pattern MVC (Modèle Vue Controlleur) est une approche qui permet de découpler 3 éléments d'une application :
- Modèles : classes POJO/JavaBeans qui permettent de représenter les objets en base. On peut y ajouter
également les classes qui vont nous aider à réaliser les opérations CRUD.
- Vues : code dédié à l'affichage, dépourvu de toute logique métier
- Controlleur : assemblage des Modèles et Vues avec ajout de la logique métier

Le découplage du code permet une meilleure évolutivité du projet.
Il est aussi plus aisé de travailler en équipe car chacun va pouvoir avancer sur sa brique (M, V ou C)
indépendamment du travail des autres, à condition que des spécifications aient été décidées en amont.

Voici un exemple d'organisation :
- l'équipe est découpée en 3 groupes, chacun se charge d'une des briques
- un première réflexion est menée en commun afin d'arrêter une maquette constituée des classes et signatures
des méthodes de chaque brique. Cela peut faire l'objet d'une représentation UML. Exemple :
    - l'équipe M devra produire une classe Client, celle-ci aura une méthode `public static Client findOne(int id)` qui
    retournera un Client sur la base de son id, et des getters et setters appropriés pour les attributs de Client
    - l'équipe V produira une fenêtre via un `public class VueClient extends JFrame` qui affichera les détails 
    du client au travers de 4 champs, lesquels sont accessibles via setters :
    ```
    public void setId(int id)  
    public void setNom(String nom)
    public void setPrenom(String prenom)
    public void setAdresse(String adresse)
    ``` 
   - l'équipe C produira une classe `public class ControllerClient` dont l'une des méthode
   permettra d'afficher une fenêtre avec les détails de l'utilisateur :
   ```
   public static showClient(int id){
      Client client = Client.findOne(id);
      
      VueClient vue = new VueClient();
      vue.setId(client.getId());
      vue.setNom(client.getNom());
      vue.setPrenom(client.getPrenom());
      vue.setAdresse(client.getAdresse());

      vue.setVisible(true);  
   }
   ```

On peut ainsi imaginer le scénario suivant :
- une fenêtre liste les utilisateur
- si on clic sur le bouton [Voir] en face d'un des utilisateur
- cela déclenche la méthode `showClient` avec l'id adéquat

L'important est de toujours découpler les 3 briques pour rester modulable.

Pour que l'équipe C puisse commencer à travailler, elle a cependant besoin de pouvoir manipuler dès le début
les classes que vont produire les équipes M et V. Voici une astuce pour ce genre de situation :
- après la réunion initiale visant à concevoir l'UML
- rédiger des intefaces pour chacune des classes à générer : celles-ci doivent posséder
le même nom que les classes définitives avec un préfixe Abstract par exemple, et posséder l'ensemble des méthodes publiques (rappel :
aucun champ ne devrait être public !)
- l'équipe C va alors utiliser ces classes abstraites en les implémentant dans un comme classes anonymes
- au fur et à mesure que les équipes M et V auront fait des git push, l'équipe C va pouvoir remplacer
ses instantiations par les véritables classes en changeant simplement les imports

Démonstration :
- à l'issue de l'UML, la classe abstraite suivante est rédigée :
```
package com.company.app.model

abstract class AbstractClient {
    public abstract int getId();
    public abstract String getNom();
    public abstract String getPrenom();
    public abstract String getAdresse();
    public abstract AbstractClient findOne(int id);
}
```
- l'équipe C implémente cette interface dans un package distinct :
```
package com.company.app.model.dev

class Client implements ClientInterface {

    public int getId() {
        return 1;
    }

    @Override
    public String getNom() {
        return "nom test";
    }

    @Override
    public String getPrenom() {
        return "prenom test";
    }

    @Override
    public String getAdresse() {
        return "adresse test";
    }

    @Override
    public ClientInterface findOne(int id) {
        return this;
    }
}
```
- et commence à l'utiliser
```
import com.company.app.model.dev.Client;
...
Client client = new Client();
```
- une fois que l'équipe M a fait une première proposition, l'équipe C
peut changer son import :
```
import com.company.app.model.Client;
...
Client client = new Client();
```

Pour que cela fonctionne : M, V et C doivent toujours implémenter
les interfaces qui ont été décidées en commun.
Si l'une des équipes nécessite de faire une modification d'interface,
elle doit le faire en concertation avec les autres équipes.
Si la modification est validée, l'équipe fait un commit sur la nouvelle interface
et tout le monde récupère cette nouvelle version via un git pull.

Il y a cependant une limite avec cette approche : il est possible 
en java de créer une méthode statique via une interface, mais
dans ce cas les classes qui implémentent cette interface ne pourront pas la 
surcharger. Donc vous ne pourrez pas mettre dans vos spec (interfaces)
la signature de vos méthodes statiques...

Ce découpage entre M, V et C ne doit pas être une barrière infranchissable.
Si M a terminé son travail, il peut venir aider V ou C.
Si une tâche nécessite une réflexion poussée sur les 3 briques à la fois,
il peut être plus judicieux de laisser une personne se charger de l'ensemble du
MVC pour cette tâche et de faire relire le code après par les équipes de chaque brique
pour un éventuel refactoring/découplage.