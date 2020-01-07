# CRUD : Create, Read, Update, Delete
## Requêtes simples
### Prérequis
> Consignes : créer une table nommée examen avec les champs suivants :
> - nom : VARCHAR, taille 20
> - prenom : VARCHAR, taille 20
> - examen : VARCHAR, taille 30
> - score : type INT
> - score_max : type INT
> - date : type DATETIME
> - commentaire : type TEXT
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
```

### INSERT
> Consignes : en une seule requête, alimentez la table examen avec les valeurs suivantes :

| nom    | prenom | examen | score | score_max | date       | commentaire                |
|--------|--------|--------|-------|-----------|------------|----------------------------|
| Bidule | Robert | Java   | 92    | 100       | 2019-01-10 | Avec félicitations du jury |
| Truc   | Gérard | SQL    | NULL  | 50        | 2019-01-15 | Copie perdue :O            |

```
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

### SELECT
Syntaxe : https://dev.mysql.com/doc/refman/8.0/en/select.html
> Consignes :
> - rédigez une requête pour afficher toute la table
```
SELECT * FROM examen;
```
> - rédigez une requête pour afficher les colonnes nom et score (sur toutes les lignes)
```
SELECT nom, score FROM examen;
```
> - rédigez une requête pour afficher les colonnes nom et score (sur toutes les lignes)
>avec la colonne score renommée "points
```
SELECT nom, score AS points FROM examen;
```
### UPDATE
Syntaxe : https://dev.mysql.com/doc/refman/8.0/en/update.html
> Consignes : remplacez les valeurs de la colonne examen par 'C++'
```
 UPDATE examen SET examen = 'C++';
```

### DELETE
Syntaxe : https://dev.mysql.com/doc/refman/8.0/en/delete.html
> Consignes : supprimez toutes les lignes de la table

PS : il existe également la commande TRUNCATE que nous verrons plus bas.
Pour l'instant utilisez DELETE.

## Requêtes avec clauses
```
 DELETE FROM examen;
```

Les commandes SELECT, UPDATE et DELETE n'ont pas beaucoup d'intérêt si elles affectent toutes les lignes.
Pour filtrer les lignes concernées on ajoute un ensemble de clauses après le mot clé WHERE

### Prérequis
> Chargez le fichier 

### SELECT 
> Consignes : créez les requêtes SELECT permettant d'afficher :
> - le nom des candidats ayant eu un score de 50
```
SELECT nom FROM examen
WHERE score = 50;
```
> - le nom des candidats ayant eu un score différent de 50
```
 SELECT nom FROM examen
 WHERE score != 50;
 ```
> - le nom des candidats ayant eu un score entre 50 et 60
```
SELECT nom FROM examen
WHERE score >= 50 AND score <= 60;
```
ou encore
```
SELECT nom FROM examen
WHERE score BETWEEN 50 AND 60;
```
> - le nom des candidats ayant eu un score de moins de 20
```
SELECT nom FROM examen
WHERE score < 20;
```
> - le nom des candidats ayant eu au moins 80% de bonnes réponses
```
SELECT nom FROM examen
WHERE score / score_max >= 0.8;
```
> - les notes et épreuves du candidat Bidule Robert
```
SELECT score, examen FROM examen
WHERE nom = 'Bidule' AND prenom = 'Robert';
```
> - le nom et prénom des candidats ayant la valeur null dans la colonne score ou dans la colonne examen
```
 SELECT nom, prenom FROM examen
 WHERE score IS NULL OR examen IS NULL;
 ```
> - les lignes où la date d'examen est antérieure au 1er janvier 2019
```
SELECT * FROM examen
WHERE date < '2019-01-01';
```
> - les lignes où le prénom commence par la lettre A
```
SELECT * FROM examen
WHERE prenom LIKE 'A%';
```
> - les lignes où le prénom ne commence pas par la lettre A
```
SELECT * FROM examen
WHERE prenom NOT LIKE 'A%';
```
> - les lignes où l'examen figure pas dans la liste suivante : Java, SQL, PHP, Python
```
 SELECT * FROM examen
 WHERE examen NOT IN ('Java', 'SQL', 'PHP', 'Python');
```
> - la ligne de l'examen Java de Bidule Robert
```
 SELECT * FROM examen
 WHERE examen NOT IN ('Java', 'SQL', 'PHP', 'Python')
 LIMIT 1;
```

**IMPORTANT : lorsque vous savez que seule une ligne sera retournée,
ajoutez systématiquement un `LIMIT 1` permettant à MySQL d'arrêter
sa recherche dès que la ligne est trouvée.**

### UPDATE
> Consignes : créez et exécutez les requêtes UPDATE permettant de :
> - remplacer les score_max null par la valeur 100
```
UPDATE examen
SET score_max = 100
WHERE score_max IS NULL;
```
> - remplacer les score null par la valeur 0
```
 UPDATE examen
 SET score = 0
 WHERE score IS NULL;
 ```
> - ajoutez une colonne pourcentage de type FLOAT, puis exécutez une requête pour que celle-ci
>correspondent au pourcentage de bonnes réponses (exemple : 3/10 doit donner 0.3)
```
ALTER TABLE examen
ADD pourcentage FLOAT;

UPDATE examen
SET pourcentage = score/score_max;
```

### DELETE
> Consignes : créez les requêtes DELETE permettant de supprimer les lignes qui :
> - ont un pourcentage de réussite inférieur à 10%
```
 DELETE examen
 WHERE pourcentage < 0.1;
```
> - ont un prénom qui contient la lettre A
```
 DELETE examen
 WHERE prenom LIKE '%A%';
```

### TRUNCATE
https://dev.mysql.com/doc/refman/8.0/en/truncate-table.html

Contrairement à DELETE qui peut opérer sur un ensemble de lignes (via WHERE),
TRUNCATE opère sur toute la table en la vidant de toutes ses lignes.
Il existe quelques différences (voir liste dans la documentation) qui prendrons sens par la suite. 

## Les tris
> Consignes : créez les requêtes SELECT permettant d'afficher :
> - les lignes classées selon la colonne score croissante
```
 SELECT * FROM examen
 ORDER BY score;
```
ASC (ascending) est sous-entendu, équivalent de :
```
 SELECT * FROM examen
 ORDER BY score ASC;
```

> - les lignes classées selon la colonne score décroissante
```
 SELECT * FROM examen
 ORDER BY score DESC;
```
> - les lignes classées selon le prénom (ordre alphabétique inverse).
>En cas d'égalité du prénom, on classe par le nom (selon l'ordre alphabétique).
```
 SELECT * FROM examen
 ORDER BY prenom DESC, nom;
```