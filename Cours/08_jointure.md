# Jointures

Les jointures permettent de rattacher les données de plusieurs tables
sur la base d'identifiants communs.

Il existe 4 types de relation entre des tables :
- OneToMany : une région (table region) comprend plusieurs départements (table departement)
- ManyToOne (symétrique de OneTMany) : plusieurs départements sont compris dans une région
- ManyToMany : un article (table article) peut être acheté par plusieurs clients (table client) et 
un client peut acheter plusieurs articles
- OneToOne : une personne (table personne) possède une adresse (table adresse) ➡
peu utilisé car les 2 informations pourraient plus simplement être dans la même table

On appelle table parent celle qui possède un identifiant unique (ou clé primaire)
et table enfant celle qui stocke la référence à ces identifiants

Exemple pour les régions et départements :

- la table région est la table parent (= côté One), elle possède une clé primaire (id)

| id | region              |
|----|---------------------|
| 1  | Centre Val de Loire |
| 2  | Normandie           |
| 3  | Occitanie           |

- la table departement est la table enfant (= côté Many), 
elle stocke la clé primaire de sa région de rattachement (id_region)

| id_region | departement  |
|-----------|--------------|
| 3         | Ariège       |
| 3         | Aude         |
| 1         | Loir-et-Cher |

Pour les relations ManyToMany il n'est pas possible de représenter le lien au travers
des 2 tables (client et article). Une table intermédiaire est nécessaire :

- table client

| id | nom          |
|----|--------------|
| 1  | M.Propre     |
| 2  | John Snow    |
| 3  | Santa Claus  |

- table article

| id | libelle           |
|----|-------------------|
| 1  | Chocolat          |
| 2  | Produit vaisselle |
| 3  | Désinfectant      |

- table panier

| id_client | id_article |
|-----------|------------|
| 1         | 3          |
| 1         | 2          |
| 3         | 1          |
| 2         | 1          |

L'éclatement de l'information entre différentes tables permet la normalisation
(l'information n'est jamais répétée, ex : si je renomme Désinfectant par Mouchoir,
je peux le faire en une seule modification).
Mais il est nécessaire de pouvoir reconstituer des tableaux de ce type :

| client       | article           |
|--------------|-------------------|
| M.Propre     | Désinfectant      |
| M.Propre     | Produit vaisselle |
| Santa Claus  | Chocolat          |
| John Snow    | Chocolat          |

Pour cela il faut effectuer des jointures.

> Exécutez les requêtes suivantes pour générer les tables d'exercice

```
DROP TABLE IF EXISTS salarie;
CREATE TABLE salarie (
    id INT,
    nom VARCHAR(50)
);

DROP TABLE IF EXISTS groupe; 
CREATE TABLE groupe (
    id_salarie INT,
    nom VARCHAR(100)
);

INSERT INTO salarie VALUES
(1, 'John Snow'),
(2, 'M.Propre'),
(3, 'Alfred Hitchcock'),
(4, 'Robert de Niro');

INSERT INTO groupe VALUES
(1, 'Groupe de lecture'),
(2, 'Groupe de lecture'),
(3, 'Soirées Tupperware'),
(5, 'Soirées Tupperware');
```

## INNER JOIN
https://dev.mysql.com/doc/refman/8.0/en/join.html

Une jointure de type INNER ne conserve que les données qui sont communes aux 2 tables.
Voici comment effecture une jointure entre les table salarie et groupe en ne conservant que
les salariés qui sont dans un groupe et les groupes qui ont au moins un salarié.

```
SELECT *
FROM salarie
INNER JOIN groupe
  ON salarie.id = groupe.id_salarie;
```

Répéter les noms des tables peut s'avérer fastidieux sur de grandes requêtes.
Il est possible de déclarer des ALIAS après un FROM ou un JOIN de la façon suivante :
(notez que le mot clé AS est optionnel)

```
SELECT *
FROM salarie AS sl
INNER JOIN groupe AS gp
  ON sl.id = gp.id_salarie;
```

## LEFT JOIN
Dans le paragraphe suivant nous avons perdu le salarié Robert de Niro dans les résultats
car celui-ci ne figure dans aucun groupe. Pour conserver tous les salariés (et afficher NULL
s'ils ne sont dans aucun groupe) on effectue une jointure gauche.
Gauche se réfère à la première table (par opposition à droite).
Dans le paragraphe précédent nous avons fait une jointure de la table salarie déclarée
dans le FROM (=gauche) vers la table groupe (=droite).
Une jointure gauche assure que toutes les lignes de la table gauche sont conservées
(mais pas forcément celles de la table droite)

> Effectuez une jointure gauche afin de conserver tous les salariés dans le résultat

## RIGHT JOIN
La jointure droite est simplement l'inverse de la jointure gauche.

> Effectuez cette jointure afin de conserver tous les groupes (table droite)

Note : une autre solution consiste à inverser l'ordre des tables dans la jointure gauche  

## CROSS JOIN
Jointure rarement utile, elle permet de chaîner toutes les lignes d'une table A 
à toutes les lignes d'une table B, sans critère précis.

Exemple : 

```
SELECT *
FROM salarie
CROSS JOIN groupe;
```

Une autre façon de l'écrire est de simplement lister les 2 tables dans le FROM :

`
SELECT *
FROM salarie, groupe
` 

## INNER JOIN sans INNER JOIN
Il est fréquent de voir la syntaxe suivante pour un INNER JOIN :

```
SELECT *
FROM salarie, groupe
WHERE salarie.id = groupe.id_salarie;
```

Bien que cela fonctionne, cette syntaxe est déconseillée car elle nécessite un CROSS JOIN
puis un filtre par clause WHERE (moins efficient) et ne permet pas de basculer vers
une jointure droite/gauche sans avoir à réécrire une bonne partie de la requête.
De plus elle est moins intuitive à la lecture (JOIN implicite).

## Exercice

> Consignes : exécutez les requêtes suivantes pour générer les 3 tables d'exercice 
```
CREATE TABLE `client` ( `id` INT NOT NULL ,  `nom` VARCHAR(50) NOT NULL ) ENGINE = InnoDB;
INSERT INTO `client` (`id`, `nom`) VALUES ('1', 'Batman'), ('2', 'Captain Igloo'), ('3', 'Spiderman'), ('4', 'Mr Propre'), ('5', 'Catwoman'), ('6', 'Superman'), ('7', 'Wonderwoman'), ('8', 'Mr Robot'), ('9', 'Eliot Alderson'), ('10', 'James Holden');

CREATE TABLE `bidule`.`client` ( `id` INT NOT NULL ,  `nom` VARCHAR(50) NOT NULL ) ENGINE = InnoDB;
INSERT INTO `article` (`id`, `libelle`) VALUES ('1', 'Liquide vaisselle'), ('2', 'Gauffres'), ('3', 'Kit de réparation voiture'), ('4', 'Tractopelle'), ('5', 'Bouée'), ('6', 'Hélicoptère'), ('7', 'Vaisseau spatial'), ('8', 'Chocolat'), ('9', 'Place de concert'), ('10', 'Ketchup');

CREATE TABLE `panier` ( `id_client` INT NOT NULL , `id_article` INT NOT NULL , `quantite` INT NOT NULL ) ENGINE = InnoDB;
INSERT INTO `panier` (`id_client`, `id_article`, `quantite`) VALUES ('1', '1', '1'),  ('2', '1', '1'), ('3', '2', '1'), ('4', '2', '2'), ('5', '1', '1'), ('6', '2', '2'), ('7', '1', '1'), ('8', '1', '1'), ('9', '2', '1'), ('1', '3', '2'), ('2', '4', '1'), ('3', '4', '10'), ('4', '4', '1'), ('5', '3', '1'), ('6', '4', '1'), ('7', '3', '7'), ('8', '4', '1'), ('9', '4', '1'), ('1', '5', '1'), ('2', '6', '3'), ('3', '6', '1'), ('4', '6', '1'), ('5', '6', '4'), ('6', '5', '2'), ('7', '6', '1'), ('8', '6', '1'), ('9', '6', '40'), ('1', '8', '1'), ('2', '8', '1'), ('3', '7', '1'), ('4', '7', '5'), ('5', '7', '1'), ('6', '7', '1'), ('7', '7', '2'), ('8', '7', '2'), ('9', '7', '2'), ('1', '9', '1'), ('2', '9', '1'), ('3', '9', '3'), ('4', '9', '1'), ('5', '9', '1'), ('6', '9', '25'), ('7', '9', '1'), ('8', '10', '1'), ('9', '10', '12'), ('10', '1', '1'), ('10', '3', '10'), ('10', '7', '2'), ('10', '9', '1');
```
> Puis rédigez le requêtes suivantes en organisant l'ordre de vos jointures
>afin de générer la requête la plus efficiente possible pour MySQL :
>- afficher les id des articles achetés la cliente Wonderwoman
>- afficher les articles (leur libellé) du client dont l'id vaut 10  
>**[Pour les items suivants vous n'avez plus le droit de filtrer par id dans les clauses WHERE]**
>- afficher les articles (leur libellé) achetés par le client Batman
>- afficher les articles (libellés) ET les quantités associées pour le client Superman
>- afficher les clients (noms) qui ont acheté du chocolat
>- afficher les clients qui ont acheté un même article en au moins 10 exemplaires
>- pour chaque achat d'un article à au moins 10 exemplaires, affichez une ligne avec :
>nom du client, libellé de l'article, quantité achetée