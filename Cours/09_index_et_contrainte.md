#Index et contraintes

## Index
https://dev.mysql.com/doc/refman/8.0/en/create-index.html

Note : les termes KEY et INDEX sont synonymes dans la documentation de MySQL.
Cependant il n'est pas possible d'intervertir leur usage dans les différentes commandes.

Un index permet d'accélérer la recherche de lignes dans une table.
Il est constitué d'un sous ensemble de champs de la table et est ordonné.  

Exemple :

| nom    | prenom | examen | score | score_max | date       | commentaire                |
|--------|--------|--------|-------|-----------|------------|----------------------------|
| Bidule | Robert | Java   | 92    | 100       | 2019-01-10 | Avec félicitations du jury |
| ...    | ...    | ...    | ...   | ...       | ...        | ...                        |
| Truc   | Gérard | SQL    | NULL  | 50        | 2019-01-15 | Copie perdue :O            |

Si l'on souhaite requêter sur cette table en filtrant sur le nom et le prénom, nous pouvons 
créer un index sur ces 2 champs. L'index conservera constamment une copie classée par ordre 
alphabétique de ces 2 champs. En cas de requête avec un WHERE sur nom et prénom,
l'index sera utilisé et accélèrera grandement la vitesse de lecture.

### Création simple d'un index
> Pré requis : exécutez le code suivant dans la base de données de votre choix
```
DROP TABLE IF EXISTS `examen`;

CREATE TABLE `examen` (
 `id` int(11) NOT NULL,
 `nom` varchar(20) DEFAULT NULL,
 `prenom` varchar(20) DEFAULT NULL,
 `examen` varchar(30) DEFAULT NULL,
 `score` int(11) DEFAULT NULL,
 `score_max` int(11) DEFAULT NULL,
 `date` datetime DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `examen`
    (
        `id`,
        `nom`,
        `prenom`,
        `examen`,
        `score`,
        `score_max`,
        `date`
    )
VALUES
    ('1', 'Holden', 'James', 'Python', 7, 10, '2019-07-21'),
    ('2', 'Man', 'Bat', 'C#', 6, 6, '2019-07-21'),
    ('3', 'Connor', 'Sarah', 'Java', 10, 10, '2019-07-21'),
    ('4', 'Alderson', 'Eliot', 'SQL', 15, 15, '2019-07-21');
```

> - Faites un select avec la condition id=1
> - Recommencez en ajoutant le mot clé "EXPLAIN en début de requête". 
>Observez les champs possible_keys (index disponibles) et key (index utilisés).
>On constate ici qu'aucun index n'est utilisé 
> - Ajoutez un index sur le champ id, relancez votre requête avec EXPLAIN et observez 
>les différences

### Créer un index multiple (sur plusieurs champs)
Créer un index X sur un champ A et un index Y sur un champ B ne permet pas d'accélérer les 
requêtes avec une clause à la fois sur les champs A et B.

Démonstration :
>- Créez un index sur le champ nom
>- Créez un index sur le champ prenom
>- Exécutez un select (nom='Holden' et prenom='James') avec EXPLAIN
>- Que constatez-vous ?

Pour résoudre ce problème il est possible de générer des index composites, qui
reposent sur plusieurs champs à la fois. 

>- Créez un index composite sur les champs nom et prenom
>- Exécutez à nouveau un select (nom='Holden' et prenom='James') avec EXPLAIN
>- Que constatez-vous ?

MySQL effectue lui-même son choix d'index à utiliser lors d'une requête.
Etant donné le peu de lignes dans notre table, son choix de n'utiliser que l'index sur le champ nom
demeure pertinent (une seule ligne avec Holden).

> Forcer MySQL à utiliser notre index composite en supprimant les autres index

Remarque : faut-il dans ce cas construire des index pour toutes les associations possibles
de champs afin d'accélérer les requêtes quelques soient les clauses utilisées ?\
➡ **Surtout pas !** Le maintient d'un index a un coût : à chaque nouvelle ligne insérée MySQL
doit reconstruire les index de la table afin de les maintenir à jour. 
Cette opération n'est pas négligeable en temps machine, aussi il ne faut créer que les index
qui sont nécessaires à l'application (à ses requêtes).
Il faut aussi réfléchir à la balance lecture/écriture : une table souvent mise à jour devra
faire l'objet d'une sélection minutieuse des index à créer. Une table en lecture seule
pourra faire l'objet d'un plus grand nombre d'index sans impacter les performances.

Remarque :
L'intérêt des index n'est pas limité aux clauses WHERE. Toute opération
nécessitant de rechercher rapidement une/des lignes sur la base des chamsp indexés 
peuvent en bénéficier. Ainsi il est souvent conseillé de mettre en index les champs 
utilisés pour les jointutres.

## Contraintes

Lorsqu'un index est créé, il est possible de définir certaines contraintes.

### UNIQUE
https://dev.mysql.com/doc/refman/8.0/en/create-table.html#create-table-indexes-keys\
https://dev.mysql.com/doc/refman/8.0/en/alter-table.html

On peut forcer un champ à en comporter que des valeurs uniques (interdire les doublons).
Pour cela MySQL nécessite que le champ concerné soit indexé (accélère la recherche de doublon).

Il est possible d'ajouter la contrainte UNIQUE à tout moment sur un champ via un ALTER 
(un index sera alors automatiquement créé), ou directement lors d'un CREATE TABLE.

Comme pour la création d'index, la contrainte UNIQUE peut s'appliquer sur une liste de champ.

> Sur la table précédente, ajoutez une contrainte UNIQUE sur les champs nom et prenom
>afin de prévenir la possibilité d'homonymes. Il doit être possible 
>d'avoit deux Albert ou deux Schmidt, mais pas deux Albert Schmidt.\
>Vérifiez que cela fonctionne en tentant d'insérer ces 4 lignes via 4 INSERT distincts :
>- Eric Schmidt
>- Robert Schmidt
>- Eric Dupont
>- Eric Dupont
>
>Seul le dernier INSERT devrait être refusé
>Videz la table et réinsérer les 4 lignes en un unique INSERT : quel est le résultat ?
>Et pourquoi ?

### Clés primaires (PRIMARY KEYS)
https://dev.mysql.com/doc/refman/8.0/en/create-table.html#create-table-indexes-keys\
https://dev.mysql.com/doc/refman/8.0/en/alter-table.html

Il est souvent intéressant d'avoir un moyen fiable d'identifier à coup sûr une ligne
dans une table. Il peut s'agir d'un champ id avec une contrainte UNIQUE par exemple.
Mais parfois notre identifiant unique sera un ensemble de champs (comme pour l'exercice précédent).

Lorsque vous concevez un index UNIQUE dans cette optique, vous pouvez le déclarer comme PRIMARY.
Chaque table ne peut posséder qu'un seul index PRIMARY (communément appelé PRIMARY KEY = clé primaire).
Il se comporte alors comme un INDEX sur un champ NOT NULL avec une contrainte UNIQUE.
L'avantage de déclarer ce type de champ avec le mot clé PRIMARY est que MySQL
va pouvoir s'en servir comme moyen d'identification de vos lignes, et vous offrira d'autres
contraintes réservés aux PRIMARY KEYS.

Notes :
- un index PRIMARY est automatiquement nommé PRIMARY KEY
- lorsque la clé primaire est un INT dont la valeur n'a aucune traduction métier 
(simple nombre unique pour cahque ligne), on appelle classiquement ce champ id
et on lui ajoute l'attribut AUTOINCREMENT qui permet de lui attribuer une nouvelle
valeur à chaque INSERT.


>Sur la table précédente, supprimez tous les index puis :
>- créer un PRIMARY KEY sur le champ id avec un AUTOINCREMENT
>- imaginons maintenant que le champ id n'existe pas (supprimez ce champ),
>concevez une clé primaire sur notre table qui nous assure qu'un individu 
>ne puisse avoir qu'une seule note à un même examen

### Contraintes de clé
https://dev.mysql.com/doc/refman/8.0/en/create-table-foreign-keys.html

Nous avons vu qu'il existait différents types de relations possibles entre les tables (OneToMany, etc).
Nous avons exploité ces relations au travers de jointures.
Mais jusqu'à présent MySQL n'avait aucun moyen de connaitre la nature de ces relations.

#### ON DELETE

Voici un exemple de 2 tables liées par une relation OneToMany (un étudiant peut passer plusieurs examens)

>Exécutez le code suivant : 
```
DROP TABLE IF EXISTS `etudiant`;
CREATE TABLE `etudiant` (
 `id` int(11) NOT NULL,
 `nom` varchar(50) NOT NULL,
 `prenom` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
INSERT INTO `etudiant` (`id`, `nom`, `prenom`) VALUES 
('1', 'Man', 'Bat'), 
('2', 'Einstein', 'Albert');



DROP TABLE IF EXISTS `examen`;
CREATE TABLE `examen` (
 `id_etudiant` int(11) NOT NULL,
 `examen` varchar(30) NOT NULL,
 `score` int(11) DEFAULT NULL,
 `score_max` int(11) DEFAULT NULL,
 `date` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
INSERT INTO `examen` (`id_etudiant`, `examen`, `score`, `score_max`, `date`) VALUES 
('1', 'Python', '9', '10', '2020-01-01'), 
('2', 'Java', '7', '7', '2020-01-02'),
('1', 'Java', '6', '7', '2020-01-01'), 
('2', 'Python', '10', '10', '2020-01-02')
;
```
> Puis rédigez une jointure permettant d'afficher une table avec les variables :
>- nom
>- prénom
>- examen
>- score
>- score_max

Les jointures sont possibles mais que se passerait-il si l'on supprimait l'étudiant Albert ?
La requête précédente n'afficherait plus ses résultats, mais nous les conserverions en base !

Dans de nombreux cas, supprimer une ligne du côté One d'une relation OneToMany ne peut pas 
se faire sans certaines précautions. Il existe 3 réponses possibles :
- supprimer en cascade (CASCADE) : supprimer l'entité (étudiant Albert) doit avoir pour effet de supprimer
toutes les données qui lui sont liées (ses examens)
- créer des données orphelines (SET NULL) : les données (examens) liées à l'entité supprimée (Albert) sont conservées
mais la référence à l'entité est mise à NULL (id_etudiant=NULL)
- interdire la suppression (RESTRICT, synonyme de NO ACTION) : tant que des données (examens) sont rattachées à l'entité (étudiant Albert),
il n'est pas possible de supprimer l'entité. Une application (ou admin) devrait donc supprimer d'abord 
les données rattachées à l'entité avant de supprimer l'entité elle même.


Ces 3 comportements sont paramétrables au sein de MySQL à condition d'avoir déclaré une clé
primaire dans la/les tables concernées.

Rappel :
- dans une relation OneToMany, on considère que la table "parent" est celle qui est identifiée
par une clé primaire = côté One
- la table enfant est celle qui stocke les références à ces clés (on parle alors de clés secondaires)
= côté Many

**Important : la création d'une contrainte de clé se fait toujours depuis la table enfant**

> - Définissez quelle est la table parent et la table enfant dans l'exercice
> - Effectuez la/les déclarations de clé(s) primaire(s) sur ces tables
> - Déclarez une contrainte de clé de façon à interdire la suppression d'un étudiant s'il possède des examens
> - Testez la suppression d'un étudiant : cela ne doit pas marcher
>(si besoin, rechargez les données de départ pour réessayer)
> - Supprimez cette contrainte de clé
> - Recréez une contrainte mais cette fois-ci la suppression d'un étudiant doit également supprimer
>ses examens 
> - Testez la suppression d'un étudiant : cela ne doit également supprimer ses examens

#### ON UPDATE

Pour information, ces 3 comportements (CASCADE, SET NULL, RESTRICT) peuvent aussi être configurés
comme contrainte de clé pour les UPDATES, mais cela est moins courant.

Exemple : dans la vraie vie les étudiants sont déjà identifiés par un numéro INE sur lequel nous n'avons pas la main
(généré au niveau national). On pourrait très bien utiliser ce numéro comme clé primaire dans notre table etudiant,
au lieu de notre id autoincrémenté. Cette approche est tout à fait valable mais elle pose quelques difficultés :
- il faut que l'autorité qui fournit notre identifiant externe (ici l'INE) soit parfaitement fiable.
Si elle attribue le même INE à 2 individus, nous n'auront aucune solution pour enregistrer nos 2 étudiants
- si cet identifiant externe est amené à changer, il faudra répercuter la modification sur toutes les tables concernées
(ce qui peut être un véritable cauchemard à faire à la main)

On peut donc choisir 2 approches :
- stocker l'INE dans la table etudiant au même titre que n'importe quel autre champ, et continuer à
utiliser notre id autoincrémenté comme clé primaire pour nos jointures et contraintes
- utiliser l'INE comme clé primaire et configurer une contrainte de clé ON UPDATE

Voici les 3 comportements possibles :
- mettre à jour en cascade (CASCADE) : une mise à jour de la clé primaire (INE de la table etudiant) se répercutera 
sur la table liée par la contrainte (l'INE dans la table examen)
- interdire la mise à jour (RESTRICT ou NO ACTION) : la mise à jour de la clé primaire (INE de la table etudiant)
est interdite
- créer des données orphelines (SET NULL) : la mise à jour de la clé primaire (INE de la table etudiant)
mettra à NULL les clés secondaires des tables liées (INE de la table examen pour les étudiants concernés)