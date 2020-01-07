# Manipulation des dates et heures

## Exercice 1
> Consignes : Créez une table nommée 'horloge' avec les 5 colonnes suivantes :
> - test_date de type DATE
> - test_time de type TIME
> - test_datetime de type DATETIME
> - test_timestamp de type TIMESTAMP
> - test_year de type YEAR

```
CREATE TABLE horloge
(
    test_date DATE,
    test_time TIME,
    test_datetime DATETIME,
    test_timestamp TIMESTAMP,
    test_year YEAR
);
```

> Et Insérez 3 lignes :
> - Une 1e avec la date `2019-12-31 13:45:23` dans chaque colonne
> (s'il s'agit d'un champ DATE n'insérez que la partie date, 
> s'il s'agit d'un champ TIME n'insérez que la partie time, etc)

```
INSERT INTO horloge
(test_date,
 test_time,
 test_datetime,
 test_timestamp,
 test_year)
VALUES ('2019-12-31',
        '13:45:23',
        '2019-12-31 13:45:23',
        '2019-12-31 13:45:23',
        '2019');
```

> - Une 2e avec la date `2018-06-12 11:20:01` dans chaque colonne (mêmes consignes)

```
INSERT INTO horloge
(test_date,
 test_time,
 test_datetime,
 test_timestamp,
 test_year)
VALUES ('2018-06-12',
        '11:20:01',
        '2018-06-12 11:20:01',
        '2018-06-12 11:20:01',
        '2018');
```

> - Une 3e avec les valeurs "zéro" autorisées dans chaque colonne

```
INSERT INTO horloge
(test_date,
 test_time,
 test_datetime,
 test_timestamp,
 test_year)
VALUES ('0000-00-00',
        '00:00:00',
        '0000:00:00 00:00:00',
        '0000:00:00 00:00:00',
        '0000');
```

> - Une 4e avec les valeurs NULL dans chaque colonne

```
INSERT INTO horloge
(test_date,
 test_time,
 test_datetime,
 test_timestamp,
 test_year)
VALUES (NULL,
        NULL,
        NULL,
        NULL,
        NULL);
```

## Les fonctions de date et heure

### Extraire un jour, mois, heure, etc d'une date/heure
> Consignes : rédigez une requête affichant 
> - le mois actuel dans un champ "mois"
> - l'heure actuelle dans un champ "heure"
```
SELECT
    MONTH(CURDATE()) AS mois,
    HOUR(CURTIME()) AS heure;
```
Ou plus simplement (mais un peu moins efficient) :
```
SELECT
    MONTH(NOW()) AS mois,
    HOUR(NOW()) AS heure;
```

### Ajout de dates (ADDDATE)
> Consignes : affichez la table horloge avec un champ calculé nommé 'somme', correspondant 
> - au champ test_date + 20 jours
```
SELECT *,
    ADDDATE(test_date, INTERVAL 20 DAY) AS somme
FROM horloge;
```
> - au champ test_date + 4 mois
```
SELECT *,
    ADDDATE(test_date, INTERVAL 4 MONTH) AS somme
FROM horloge;
 ```
> - au champ test_date + 2 ans
```
SELECT *,
    ADDDATE(test_date, INTERVAL 2 YEAR) AS somme
FROM horloge;
```
> - au champ test_date - 20 jours
```
SELECT *,
    ADDDATE(test_date, INTERVAL -20 DAY) AS somme
FROM horloge;
```
> - au champ test_datetime + 1 an, 4 mois, 13 jours, 2 heures
```
 SELECT *,
     ADDDATE(
        ADDDATE(test_datetime, INTERVAL '1-4' YEAR_MONTH),
        INTERVAL '13 2' DAY_HOUR
     ) AS somme
 FROM horloge;
 ```

> Note : Il est également possible d'utiliser SUBDATE pour effectuer des soustractions

### Ajout de temps (ADDTIME : ajout de durée à un TIME/DATETIME/TIMESTAMP)
> Consignes : affichez la table horloge avec un champ calculé nommé 'somme', correspondant 
> - au champ test_time + 20 minutes
```
 SELECT *,
     ADDTIME(test_time, '00:20:00') AS somme
 FROM horloge;
```
> - au champ test_datetime + 1 jour, 2 heures
```
 SELECT *,
     ADDTIME(test_datetime, '1 02:00:00') AS somme
 FROM horloge;
```
> - au champ test_timestamp - 15 secondes
```
  SELECT *,
      ADDTIME(test_timestamp, '-00:00:15') AS somme
  FROM horloge;
```

> Note : Il est également possible d'utiliser SUBTIME pour effectuer des soustractions

### Soustraction de dates (DATEDIFF)
> Consignes : affichez la table horloge avec un champ calculé nommé 'difference', correspondant à
> la différence en jours entre la date actuelle et le champ test_date
```
  SELECT *,
      DATEDIFF(test_date, CURRENT_DATE()) AS difference
  FROM horloge;
```

### Soustraction d'heures (TIMEDIFF)
> Consignes : affichez la table horloge avec un champ calculé nommé 'difference', correspondant à
> la différence en heures, minutes, secondes entre la date actuelle et le champ test_datetime
```
   SELECT *,
       TIMEDIFF(test_datetime, NOW) AS difference
   FROM horloge;
```

### Soustraction de dates et heures (TIMESTAMPDIFF)
> Consignes : calculez la différence entre le champ test_datetime et la date-heure actuelle,
> et affichez cette différence en 4 champs :
> - un champ diff_jours pour le nombre de jours
> - un champ diff_heures pour le nombre d'heures
> - un champ diff_minutes pour le nombre de minutes
> - un champ diff_secondes pour le nombre de secondes
> Puis créez un dernier champ (nommé phrase) affichant cette différence sous le format 'X jours X heures X minutes X secondes'
```
   SELECT *,
       TIMESTAMPDIFF(DAY, test_datetime, NOW()) AS diff_jours,
       TIMESTAMPDIFF(HOUR, test_datetime, NOW()) % 24 AS diff_heures,
       TIMESTAMPDIFF(MINUTE, test_datetime, NOW()) % 60 AS diff_minutes,
       TIMESTAMPDIFF(SECOND, test_datetime, NOW()) % 60 AS diff_secondes
   FROM horloge;
```
Notes :
- Pensez à appliquer des modulo (%), sinon TIMESTAMPDIFF renvoie la différence totale dans l'unité demandée
- CURRENT_TIMESTAMP, CURRENT_TIMESTAMP() et NOW() sont synonymes

Important : les champs calculés (comme diff_jours) ne peuvent pas être réutilisés dans d'autres champs.
Ainsi il n'est pas possible de faire la chose suivante :

```
   SELECT *,
       TIMESTAMPDIFF(DAY, test_datetime, NOW()) AS diff_jours,
       TIMESTAMPDIFF(HOUR, test_datetime, NOW()) % 24 AS diff_heures,
       TIMESTAMPDIFF(MINUTE, test_datetime, NOW()) % 60 AS diff_minutes,
       TIMESTAMPDIFF(SECOND, test_datetime, NOW()) % 60 AS diff_secondes,
       CONCAT(
        diff_jours, ' jours ',
        diff_heures, ' heures ',
        diff_minutes, ' minutes ',
        diff_secondes, ' secondes'
       ) 
   FROM horloge;
```

A la place il faut réécrire la définition de ces variables à chaque fois, ce qui est très peu pratique.
Nous verrons plus tard une alternative à cela.

```
   SELECT *,
       TIMESTAMPDIFF(DAY, test_datetime, NOW()) AS diff_jours,
       TIMESTAMPDIFF(HOUR, test_datetime, NOW()) % 24 AS diff_heures,
       TIMESTAMPDIFF(MINUTE, test_datetime, NOW()) % 60 AS diff_minutes,
       TIMESTAMPDIFF(SECOND, test_datetime, NOW()) % 60 AS diff_secondes,
       CONCAT(
        TIMESTAMPDIFF(DAY, test_datetime, NOW()), ' jours ',
        TIMESTAMPDIFF(HOUR, test_datetime, NOW()) % 24, ' heures ',
        TIMESTAMPDIFF(MINUTE, test_datetime, NOW()) % 60, ' minutes ',
        TIMESTAMPDIFF(SECOND, test_datetime, NOW()) % 60, ' secondes'
       ) 
   FROM horloge;
```