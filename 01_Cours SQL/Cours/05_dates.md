# Manipulation des dates et heures

## Les types de date et heure
Types : https://dev.mysql.com/doc/refman/8.0/en/date-and-time-types.html  
DATE, DATETIME, TIMESTAMP : https://dev.mysql.com/doc/refman/8.0/en/datetime.html

5 types de variable sont proposés :
- DATE (ex : `2019-12-31`), valeurs autorisées allant de `1000-01-01` à `9999-12-31`
- TIME (ex : `23:45:59`), valeurs autorisées allant de `-838:59:59` à `838:59:59`  
- DATETIME (ex : `2019-12-31 23:45:59`), valeurs autorisées allant de `1000-01-01 00:00:00` à `9999-12-31 23:59:59`
- TIMESTAMP (ex : `2019-12-31 23:45:59`), valeurs autorisées allant de `1970-01-01 00:00:01` UTC à `2038-01-19 03:14:07` UTC
- YEAR  (ex : `2019`), valeurs autorisées allant de `1901` à `2155`

Les types TIME, DATETIME et TIMESTAMP peuvent inclure des fractions de seconde sous la forme `2019-12-31 23:59:59.999999`

Attention à renseigner leurs valeurs dans le format standard pour éviter les mauvaises surprises.  
Exemples :
- DATE `69-01-01` équivaut à `1969-01-01`
- DATE `70-01-01` équivaut à `2070-01-01`

Chaque type peut prendre une valeur NULL et une valeur "zéro" (voir lien documentation).
En cas de valeur aberrante inséré en MySQL stockera la valeur "zéro" propre au type.

Il est possible de stocker un mois et/ou un jour de mois valant zéro dans une date.
Exemple :
- DATE `2019-00-01` = ???
- DATE `2019-01-00` = mois de janvier 2019
- DATE `2019-00-00` = année 2019  
Les opérations de calcul sur ces dates seront par contre non fiables

Ces valeurs "zéro" sont propices à la confusion : en cas d'insertion de date erronée (ex : mois=24),
MySQL insérait une valeur "zéro" à la place. Ainsi on pouvait ne pas voir des erreurs d'insertion,
et en cas d'usage de valeur "zéro", on ne pouvait plus savoir s'il s'agissait d'une erreur ou d'une
véritable valeur "zéro".  
Depuis MySQL 5.6 ce comportement a été résolu en interdisant les valeurs "zéro" pour les types
de date et heure (strict_mode=on). Ainsi une valeur erronée (ex : mois=24) donne bien lieu à une erreur
désormais. De la même façon, les mois et jour du mois valant 0 sont également interdits.  
Si besoin il reste possible de désactiver le strict_mode pour revenir au comportemetn avant la 5.6.

## Les fuseaux horaires (timezones)
Il existe 2 cas :
- usage du type TIMESTAMP : MySQL stocke par défaut tous les TIMESTAMP
dans le fuseau UTC. Si le client SQL est configuré dans un autre fuseau et insere TIMESTAMP,
MySQL fait la conversion vers le fuseau UTC. Lors du SELECT, MySQL fait une nouvelle
conversion du fuseau UTC vers celui du client avant d'envoyer la valeur
- usage du type DATETIME : seule la date et l'heure sont stockée, 
MySQL ne prend pas en compte le fuseau horaire.
Utile si l'usage des fuseaux horaires n'est pas nécessaire (ex : un seul pays),
ou si le fuseau est stocké dans un autre champ (et dans ce cas c'est l'application
qui devra gérer le calcul de date selon le fuseau)

Cette gestion du fuseau et le range de dates autorisées sont les 2 principales 
différences entre un TIMESTAMP et un DATETIME.

## Exercice
> Consignes : Créez une table nommée 'horloge' avec les 5 colonnes suivantes :
> - test_date de type DATE
> - test_time de type TIME
> - test_datetime de type DATETIME
> - test_timestamp de type TIMESTAMP
> - test_year de type YEAR  
> Et Insérez 3 lignes :
> - Une 1e avec la date `2019-12-31 13:45:23` dans chaque colonne
>(s'il s'agit d'un champ DATE n'insérez que la partie date, 
>s'il s'agit d'un champ TIME n'insérez que la partie time, etc)
> - Une 2e avec la date `2018-06-12 11:20:01` dans chaque colonne (mêmes consignes)
> - Une 3e avec les valeurs NULL dans chaque colonne 

## Les fonctions de date et heure
https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html  
Unités d'intervalles temporels : https://dev.mysql.com/doc/refman/8.0/en/expressions.html#temporal-intervals

### Récupérer la date actuelle
Selon si l'on veut une date, une heure ou un timestamp (date+heure), il existe plusieurs fonctions :
- date : 3 synonymes
    - CURDATE()
    - CURRENT_DATE(),
    - CURRENT_DATE
- heure : 3 synonymes
    - CURTIME()
    - CURRENT_TIME(), 
    - CURRENT_TIME
- date+heure : 3 synonymes
    - NOW()
    - CURRENT_TIMESTAMP(),
    - CURRENT_TIMESTAMP
    
Il existe également SYSDATE() qui renvoie un timestamp qui peut différer de NOW() :
- NOW() prend la valeur de l'instant du début d'exécution de la requête
- SYSDATE() prend la valeur exacte où il est exécuté

Exemple illustrant cette différence : (la fonction SLEEP(5) met en pause 5 secondes la requête)  
```
SELECT 
    NOW(), SYSDATE(), 
    SLEEP(5), 
    NOW(), SYSDATE();
``` 

### Extraire un jour, mois, heure, etc d'une date/heure
> Consignes : rédigez une requête affichant 
> - le mois actuel dans un champ "mois"
> - l'heure actuelle dans un champ "heure"

### Ajout de dates (ADDDATE : ajout de durée à un champ de date/heure)
> Consignes : affichez la table horloge avec un champ calculé nommé 'somme', correspondant 
> - au champ test_date + 20 jours
> - au champ test_date + 4 mois
> - au champ test_date + 2 ans
> - au champ test_date - 20 jours
> - au champ test_datetime + 1 an, 4 mois, 13 jours, 2 heures

### Ajout de temps (ADDTIME : ajout de durée à un TIME/DATETIME/TIMESTAMP)
> Consignes : affichez la table horloge avec un champ calculé nommé 'somme', correspondant 
> - au champ test_time + 20 minutes
> - au champ test_datetime + 1 jour, 2 heures
> - au champ test_timestamp - 15 secondes

### Soustraction de dates (DATEDIFF)
> Consignes : affichez la table horloge avec un champ calculé nommé 'difference', correspondant à
> la différence en jours entre la date actuelle et le champ test_date 

### Soustraction d'heures (TIMEDIFF)
> Consignes : affichez la table horloge avec un champ calculé nommé 'difference', correspondant à
> la différence en heures, minutes, secondes entre la date actuelle et le champ test_datetime

Les valeurs obtenues sont tronquées car dépassent les valeurs autorisées pour un champ de type TIME.
La solution consiste à utiliser TIMESTAMPDIFF. 

### Soustraction de dates et heures (TIMESTAMPDIFF)
> Consignes : calculez la différence entre le champ test_datetime et la date-heure actuelle,
> et affichez cette différence en 4 champs :
> - un champ diff_jours pour le nombre de jours
> - un champ diff_heures pour le nombre d'heures
> - un champ diff_minutes pour le nombre de minutes
> - un champ diff_secondes pour le nombre de secondes
>
> Puis créez un dernier champ (nommé phrase) affichant cette différence sous le format 'X jours X heures X minutes X secondes'

Indice pour le dernier point : https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_concat