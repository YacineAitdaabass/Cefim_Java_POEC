# Tables

## Prérequis
> Consignes : créez une base de données nommée `cefim` avec l'encodage `utf8mb4`
> et la collation `utf8mb4_0900_ai_ci`

## Sélectionner une base de données [USE]
Pour travailler sur des tables, il faut d'abord préciser sur quelle
base de données on opère.

Pour cela on utilise la commande `USE`
Syntaxe : https://dev.mysql.com/doc/refman/8.0/en/use.html

On peut aussi dès la connexion à mysql préciser la table sur laquelle on opère :  
`mysql -p nom_de_la_base` (en plus des paramètres habituel tel que -u, etc)

Une fois la base de données sélectionnée, on remarque que le shell
mysql nous indique la base en question entre [].

## Afficher les tables [SHOW TABLES]
Syntaxe : https://dev.mysql.com/doc/refman/8.0/en/show-tables.html
Actuellement, il n'y a aucune table

## Créer des tables [CREATE TABLE]
Syntaxe : https://dev.mysql.com/doc/refman/8.0/en/create-table.html

La commande `CREATE TABLE` est plus complexe, elle requiert de renseigner :
- le nom de la table à créer
- la liste des colonnes, avec pour chacune :
    - le nom de la colonne
    - si elle peut prendre une valeur nulle ou non 
    - son type
    - sa taille
    - son éventuelle collation
    - etc
- d'éventuelles options de table
- d'éventuelles options de partition

Le choix d'un type est primordial car il conditionne la façon de requêter sur la table et
la place nécessaire pour en stocker les valeurs. Nous aborderons ces types ultérieurement.

> Consignes : créer une table nommée examen avec les champs suivants :
> - nom : VARCHAR, taille 20
> - prenom : VARCHAR, taille 20
> - examen : VARCHAR, taille 30
> - score : type INT
> - date : type DATETIME
> - commentaire : type TEXT

## Modifier une table [ALTER TABLE]
Syntaxe : https://dev.mysql.com/doc/refman/8.0/en/alter-table.html

> Consignes : en une seule requête effectuez les modifications suivantes :
> - Ajoutez un champ score_max de type INT, il doit être placé après le champ score
> - Supprimez le champ prenom
> - Modifiez la taille du champ nom (20) par 30

## Afficher la structure d'une table (son DDL : Data Definition Language) [SHOW CREATE TABLE]
Syntaxe : https://dev.mysql.com/doc/refman/8.0/en/show-create-table.html

Vous devriez normalement obtenir le DDL suivant :
```
CREATE TABLE `examen` (
  `nom` varchar(30) DEFAULT NULL,
  `examen` varchar(30) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  `score_max` int(11) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `commentaire` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
```
On remarque 2 choses :
- les noms des champs et des tables sont entourés de backticks. Cela n'est utile que 
lorsque l'identifiant est un mot clé réservé par MySQL (https://dev.mysql.com/doc/refman/8.0/en/keywords.html),
s'il contient des espaces ou des caractères spéciaux : https://dev.mysql.com/doc/refman/8.0/en/identifiers.html.
Ces 3 situations sont fortement déconseillées, aussi vous ne devriez pas avoir à utiliser de backticks pour vos
identifiants 
- des paramètres supplémentaires sont apparus :
il s'agit de valeurs par défaut ajoutées automatiquement lors de la création de la table 
(DEFAULT NULL, ENGINE, nombre entre parenthèse après le type de variable...),
nous les aborderons plus tard.

## Supprimer des tables [DROP TABLE]
Syntaxe : https://dev.mysql.com/doc/refman/8.0/en/drop-table.html

> Consignes : supprimez la table examen