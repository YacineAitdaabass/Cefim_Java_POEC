# CRUD : Create, Read, Update, Delete
Il existe 4 opérations principales de manipulation des données dans une base, 
chacune possède un "Verbe" correspondant en SQL :
- Create : création de lignes ➡ INSERT
- Read : lecture des données ➡ SELECT
- Update : modification des données ➡ UPDATE
- Delete : supression de lignes ➡ DELETE

Ces verbes et leurs syntaxes sont les principaux composant du DML (Data Manipulation Language)

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

### INSERT
Syntaxe : https://dev.mysql.com/doc/refman/8.0/en/insert.html

Notez qu'il existe 2 syntaxes équivalentes pour l'insertion :
- `INSERT INTO tbl_name (a,b,c) VALUES(1,2,3);`
- `INSERT INTO tbl_name SET a=1, b=2, c=3;`

Bien que la 2e solution soit plus lisible, elle n'est pas dans le standard SQL
et ne permet pas l'insertion de plusieurs lignes à la fois.

> Consignes : en une seule requête, alimentez la table examen avec les valeurs suivantes :

| nom    | prenom | examen | score | score_max | date       | commentaire                |
|--------|--------|--------|-------|-----------|------------|----------------------------|
| Bidule | Robert | Java   | 92    | 100       | 2019-01-10 | Avec félicitations du jury |
| Truc   | Gérard | SQL    | NULL  | 50        | 2019-01-15 | Copie perdue :O            |
>

### SELECT
Syntaxe : https://dev.mysql.com/doc/refman/8.0/en/select.html
> Consignes :
> - rédigez une requête pour afficher toute la table
> - rédigez une requête pour afficher les colonnes nom et score (sur toutes les lignes)
> - rédigez une requête pour afficher les colonnes nom et score (sur toutes les lignes)
>avec la colonne score renommée "points" (voir ALIAS)

### UPDATE
Syntaxe : https://dev.mysql.com/doc/refman/8.0/en/update.html
> Consignes : remplacez les valeurs de la colonne examen par 'C++'


### DELETE
Syntaxe : https://dev.mysql.com/doc/refman/8.0/en/delete.html
> Consignes : supprimez toutes les lignes de la table

PS : il existe également la commande TRUNCATE que nous verrons plus bas.
Pour l'instant utilisez DELETE.

## Requêtes avec clauses

Les commandes SELECT, UPDATE et DELETE n'ont pas beaucoup d'intérêt si elles affectent toutes les lignes.
Pour filtrer les lignes concernées on ajoute un ensemble de clauses après le mot clé WHERE

### Prérequis
> Exécutez l'insertion suivante (générée à partir de vos données en cours) :

```
INSERT INTO `examen` VALUES ('Bidule','Robert','Java',92,100,'2019-01-10 00:00:00','Avec félicitations du jury'),('Truc','Gérard','SQL',NULL,50,'2019-01-15 00:00:00','Copie perdue :O'),('Pierre','Deupont','Java',70,100,'2019-01-15 00:00:00','OK'),('Jena','René','SQL',30,100,'2018-01-10 00:00:00','Oula...'),('Machin','Truc','Python',10,100,'2010-01-10 00:00:00','Catastrophe !'),('Bat','Man','Java',99,100,'2019-02-10 00:00:00','Top !'),('Mr','Propre','Java',90,100,'2019-01-12 00:00:00','Super'),('Dr','PC','Python',50,100,'2011-01-10 00:00:00','Tout juste'),('Inconnu','Inconnu','Java',NULL,100,'2019-01-10 00:00:00','Copie perdue'),('Bidule','Robert','Java',92,100,'2019-01-10 00:00:00','Avec félicitations du jury'),('Truc','Gérard','SQL',NULL,50,'2019-01-15 00:00:00','Copie perdue :O'),('Pierre','Deupont','Java',70,100,'2019-01-15 00:00:00','OK'),('Jena','René','SQL',30,100,'2018-01-10 00:00:00','Oula...'),('Machin','Truc','Python',10,100,'2010-01-10 00:00:00','Catastrophe !'),('Bat','Man','Java',99,100,'2019-02-10 00:00:00','Top !'),('Mr','Propre','Java',90,100,'2019-01-12 00:00:00','Super'),('Dr','PC','Python',50,100,'2011-01-10 00:00:00','Tout juste'),('Inconnu','Inconnu','Java',NULL,100,'2019-01-10 00:00:00','Copie perdue'),('Bidule','Robert','Java',92,100,'2019-01-10 00:00:00','Avec félicitations du jury'),('Truc','Gérard','SQL',NULL,50,'2019-01-15 00:00:00','Copie perdue :O'),('Pierre','Deupont','Java',70,100,'2019-01-15 00:00:00','OK'),('Jena','René','SQL',30,100,'2018-01-10 00:00:00','Oula...'),('Machin','Truc','Python',10,100,'2010-01-10 00:00:00','Catastrophe !'),('Bat','Man','Java',99,100,'2019-02-10 00:00:00','Top !'),('Mr','Propre','Java',90,100,'2019-01-12 00:00:00','Super'),('Dr','PC','Python',50,100,'2011-01-10 00:00:00','Tout juste'),('Inconnu','Inconnu','Java',NULL,100,'2019-01-10 00:00:00','Copie perdue'),('Broly','Loup','Js',36,100,'2019-01-10 00:00:00','nul'),('Titi','Tutu','PHP',72,100,'2019-01-10 00:00:00','Avec félicitations du jury'),('Toto','Coco','Java',92,100,'2019-01-10 00:00:00','c est super'),('Momo','Robert','C++',62,100,'2019-01-10 00:00:00','mouais'),('Truc','Gérard','SQL',NULL,50,'2019-01-15 00:00:00','Copie perdue :O'),('Bidule','Robert','JAVA',92,100,'2019-01-10 00:00:00','Avec félicitations du jury'),('Truc','Gérard','C++',NULL,50,'2019-01-15 00:00:00','Copie perdue :O'),('Ritoux','lepgm','JAVA',80,100,'2019-01-06 00:00:00','pas mal mais peut perfectionner'),('Paul','Bonan','C++',70,100,'2018-12-24 00:00:00','tres bien'),('Anais','delapelouze','JAVA',4,100,'2019-01-23 00:00:00','A revoir totalement'),('Bidule','Robert','Java',92,100,'2019-01-10 00:00:00','Avec les félicitations du jury'),('Truc','Gérard','SQL',NULL,50,'2019-01-15 00:00:00','Copie perdue :O'),('Wanny','Tchoin','km',-30,200,'2019-01-01 00:00:00','NUL !!'),('Gértrude','Alcoolique','L',190,100,'2019-12-12 00:00:00','Ivrogne mais gentille'),('Macon','Manu','Euro',9999,100,'2056-03-05 00:00:00','Belle quenelle !'),('Bidule','Robert','Java',92,100,'2019-01-10 00:00:00','Avec les félicitations du jury'),('Truc','Gérard','SQL',NULL,50,'2019-01-15 00:00:00','Copie perdue :0'),('Chose','Marie','PHP',80,100,'2019-01-31 00:00:00','Excellent'),('Greene','Bruce','JS',62,85,'2019-02-04 00:00:00','Bien'),('Saison','Automne','PHP',74,100,'2019-01-21 00:00:00','Très Bien'),('BIDULE','Robert','Java',92,100,'2019-01-10 00:00:00','Avec félicitation du jury'),('TOTO','Jean','Java',80,85,'2019-01-10 00:00:00','mention bien'),('TRUC','Gerard','SQL',NULL,50,'2019-01-15 00:00:00','Copie perdue:0'),('TATA','Paul','SQL',90,98,'2019-01-05 00:00:00','mention tres bien'),('TETE','Paul','SQL',50,50,'2019-01-05 00:00:00','assez bien'),('Bidule','Robert','Java',92,100,'2019-01-10 00:00:00','Avec félicitations du jury'),('Truc','Gérard','SQL',NULL,50,'2019-01-15 00:00:00','Copie perdu :0'),('Toto','Greg','Javascript',67,100,'2019-01-12 00:00:00','Passable'),('Fifie','Josée','PHP',23,150,'2019-02-01 00:00:00','PHP c\'est de la merde'),('Cedric','Rousseau','Python',97,100,'2019-06-06 00:00:00','Super intello'),('Bidule','Robert','Java',92,100,'2019-01-10 00:00:00','Avec félicitations du jury'),('Truc','Gérard','SQL',NULL,50,'2019-01-15 00:00:00','Copie perdue :O'),('Machin','Paul','PHP',75,100,'2019-01-12 00:00:00','hehe'),('Moshi','Moshi','Ruby',30,50,'2019-01-20 00:00:00','Pas mal !'),('Nom','Prenom','JS',1,100,'2019-02-27 00:00:00','hmm'),('Bidule','Robert','Java',92,100,'2019-01-10 00:00:00','avec felicitation du jury'),('Truc','Gérard','SQL',NULL,50,'2019-01-15 00:00:00','copie perdue :0'),('Bibi','Mochio','SQL',34,50,'2019-01-17 00:00:00','copie moyenne'),('Dus','Michel','HTML',45,50,'2019-01-18 00:00:00','double félicitation'),('Dominque','Marcel','Python',80,80,'2014-12-15 00:00:00','prix du jury'),('Bidule','Robert','Java',92,100,'2019-01-10 00:00:00','Avec félicitations du jury'),('Truc','Gérard','SQL',NULL,50,'2019-01-15 00:00:00','Copie perdue :O'),('Bidule2','Robert2','Java2',93,1000,'2019-01-11 00:00:00','Avec ovations du jury'),('Truc2','Gérard2','SQL2',NULL,500,'2019-01-18 00:00:00','Copie perdue :4'),('Bidule3','Robert3','Java3',921,1001,'2019-01-12 00:00:00','Avec félicitations du public'),('Bidule','Robert','Java',92,100,'2019-01-10 00:00:00','Avec félicitations du jury'),('Truc','Gérard','SQL',NULL,50,'2019-01-15 00:00:00','Copie perdue :O'),('emploi','pole','java',20,90,'2020-01-04 00:00:00','nice man!!!!!!'),('Bide','Rob','Javascript',2,10,'2010-01-10 00:00:00','Avec félicitations '),('idule','bebert','Php',99,300,'2019-01-10 00:00:00','Génial');
```


### SELECT 
> Consignes : créez les requêtes SELECT permettant d'afficher :
> - le nom des candidats ayant eu un score de 50
> - le nom des candidats ayant eu un score différent de 50
> - le nom des candidats ayant eu un score entre 50 et 60
> - le nom des candidats ayant eu un score de moins de 20
> - le nom des candidats ayant eu au moins 80% de bonnes réponses
> - les notes et épreuves du candidat Bidule Robert
> - le nom et prénom des candidats ayant la valeur null dans la colonne score ou dans la colonne examen
> - les lignes où la date d'examen est antérieure au 1er janvier 2019
> - les lignes où le prénom commence par la lettre A
> - les lignes où le prénom ne commence pas par la lettre A
> - les lignes où l'examen ne figure pas dans la liste suivante : Java, SQL, PHP, Python
> - la ligne de l'examen Java de Bidule Robert

### UPDATE
> Consignes : créez et exécutez les requêtes UPDATE permettant de :
> - remplacer les score_max null par la valeur 100
> - remplacer les score null par la valeur 0
> - ajoutez une colonne pourcentage de type FLOAT, puis exécutez une requête pour que celle-ci
>correspondent au pourcentage de bonnes réponses (exemple : 3/10 doit donner 0.3)

PS : en cas d'erreur, vous pouvez dropper la table et la recharger à l'aide de la procédure
du paragraphe prérequis

### DELETE
> Consignes : créez les requêtes DELETE permettant de supprimer les lignes qui :
> - ont un pourcentage de réussite inférieur à 10%
> - ont un prénom qui contient la lettre A

## Les tris
Syntaxe : voir SELECT : https://dev.mysql.com/doc/refman/8.0/en/select.html
> Consignes : créez les requêtes SELECT permettant d'afficher :
> - les lignes classées selon la colonne score croissante
> - les lignes classées selon la colonne score décroissante
> - les lignes classées selon le prénom et le nom (en cas d'égalité du prénom, on classe par le nom)

## Pagination avec LIMIT
https://dev.mysql.com/doc/refman/8.0/en/select.html  
https://dev.mysql.com/doc/refman/8.0/en/limit-optimization.html
 
Pour un affichage paginé, il peut être utile d'afficher les résultats par paquets de N lignes.
Exemple : un blog peut afficher 10 articles par page, à chaque clic sur "page suivante"
nous voulons afficher les 10 suivantes. Pour cela on utilisera `LIMIT X, Y`
- et X correspond aux N premiers résultats à ignorer
- où Y est le nombre de résultats à afficher

Ansi `SELECT * FROM blog LIMIT 10, 50;` affichera les lignes 11 à 60. 