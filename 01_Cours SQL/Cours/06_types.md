#Types de données
https://dev.mysql.com/doc/refman/8.0/en/data-types.html

## Numériques

### Entiers : TINYINT, SMALLINT, MEDIUMINT, INT, BIGINT
https://dev.mysql.com/doc/refman/8.0/en/integer-types.html

Tous les types d'entiers peuvent être associés au mot clé UNSIGNED, ayant pour effet
de garder la même ampleur de valeurs possibles, mais uniquement sur des valeurs positives.

Exemple :
- un TINYINT permet des valeurs de -128 à +127
- un TINYINT UNSIGNED permet des valeurs de 0 à +255 

Il est possible de donner une précision comme paramètre d'un entier (ex : `INT(3)`).
L'intérêt est très limité car le nombre de bits utilisé restera le même.
Ce n'est qu'à l'affichage que MySQL tronquera la valeur afin de ne garder que N chiffres significatifs.
Par défaut, MySQL renseignera toujours une précision suffisante pour un entier, ce qui correspond au nombre 
de chiffres significatifs nécessaires pour afficher les valeurs maximales stockées dans un entier.
Il en va de même pour les types d'entier. 

➡ Ne pas préciser la taille pour les entiers

### Décimaux à virgule fixe (DECIMAL ou son synonyme NUMERIC)
https://dev.mysql.com/doc/refman/8.0/en/fixed-point-types.html

Exemple : `DECIMAL(5,2)` implique que toutes les valeurs stockées auront 
au maximum 5 chiffres significatifs (précision), dont systématiquement 2 après la virgule (échelle).
Il est donc possible d'y stocker tous les nombres compris entre -999.99 et +999.99.

**Notes** : Il est possible d'ajouter le mot clé `ZEROFILL` à un DECIMAL, il en résulte que le 
nombre sera précédé et/ou suivi d'autant de 0 que nécessaire pour atteindre l'échelle et la
précision paramétrée. Cependant cet usage est déprécié et sera prochainement supprimé de MySQL : ne pas utiliser !

### Décimaux à virgule flottante (FLOAT, DOUBLE)
Les types FLOAT et DOUBLE ne prennent que la précision comme paramètre et laissent libre place
à la virgule. Ainsi `FLOAT(3)` permet de stocker les valeurs suivantes :
- 3.14
- 314
- 31400000
- 0.00000314

La différence entre FLOAT et DOUBLE réside dans le nombre de bits alloués (respectivement 4 et 6),
autorisant une précision de 0 à 23 pour un FLOAT, et de 24 à 53 pour un DOUBLE.

**Note** : MySQL autorise l'usage d'un paramètre échelle pour ses types FLOAT et DOUBLE.
Cependant cet usage est déprécié et sera prochainement supprimé de MySQL : ne pas utiliser !

### Binaires (BIT)
https://dev.mysql.com/doc/refman/8.0/en/bit-type.html
Un type BIT permet de stocker de façon binaire un entier. Un type BIT peut stocker jusqu'à 64 bits
: `BIT(64)`.

Son intérêt réside dans la possibilité d'adapter finement la taille d'une colonne pour un besoin précis.
Il peut également être utilie de visualiser un nombre de façon binaire. Exemple : pour stocker
une information binaire pour chaque jour de la semaine (quels sont les jours travaillés), 
il faut 7 bits : `BIT(7)`. On peut alors stocker `B'1101110'` pour la liste Lundi-Mardi-Jeudi-Vendredi-Samedi
(notez le B précédant la chaîne de caractères pour préciser qu'il s'agit d'un binaire en SQL).

### auto-incrémentation (AUTO_INCREMENT)
Le mot clé AUTO_INCREMENT permet de générer automatiquement une valeur pour un champ
lors d'un INSERT. La valeur insérée correspond alors à la plus grande valeur de la colonne+1.
AUTO_INCREMENT est disponible pour les entiers et les décimaux, mais est déprécié pour les FLOAT et DOUBLE.

On utilise le plus souvent AUTO_INCREMENT pour définir automatiquement les clés primaires
dans une table. AUTO_INCREMENT ne supporte pas les valeurs nulles ou négatives ni les zéros.
On crée donc couramment des champs id (clé primaire) via un    
`id INT UNSIGNED NOT NULL AUTO_INCREMENT, PRIMARY KEY(id)`    
Pour les tables ayant de très nombreuses lignes, on peut également remplacé INT par un BIGINT

## Dates et heures
Voir cours dédié à la manipulation des dates et heures

## Chaînes de caractères

Les types CHAR, VARCHAR et TEXT sont utilisés pour stocker des chaînes de caractères.
En plus du type, ces champs peuvent avoir un encodage (CHARSET, permet de faire correspondre les bits à des caractères) 
et une collation (COLLATE, permet de comparer les caractères entre eux) ou utiliser ceux définis pour  la table 
ou pour la base de données.

### CHAR
https://dev.mysql.com/doc/refman/8.0/en/char.html

Un type CHAR stocke un nombre **fixe** de caractères, allant de 0 à 255.
Ainsi un CHAR(5) prendra le même espace pour stocker "ABCDE" et "A".
**Les espaces en début et fin de chaîne sont systématiquement supprimés à l'insertion !**

### VARCHAR
https://dev.mysql.com/doc/refman/8.0/en/char.html

Un type VARCHAR adapte sa taille de stockage à son contenu.
Sa taille à la création correspond à la taille maximale autorisée. Cette taille peut aller jusqu'à 65 535. 
Ainsi un VARCHAR(5) peut stocker "A", "ABCDE" mais pas "ABCDEFG".
Les espaces en début et fin de chaîne ne sont pas supprimés à l'insertion.

### TEXT
https://dev.mysql.com/doc/refman/8.0/en/blob.html    
https://dev.mysql.com/doc/refman/8.0/en/storage-requirements.html

Lorsque la capacité d'un VARCHAR ne suffit pas pour stocker un texte, on utilise l'un des types de TEXT
selon le nombre maximal d'octets à stocker (PS : à nombre de caractères égal, le nombre d'octets peut varier
significativement selon l'encodage choisi, ex : utf8mb4 peut nécessiter jusqu'à 4 octets par caractère) :
- TINYTEXT : < 2^8 octets
- TEXT : < 2^16 octets
- MEDIUMTEXT : < 2^24 octets
- LONGTEXT : < 2^32 octets

Si un LONGTEXT ne suffit pas, il faut mettre en place un algorithme de découpage en plusieurs LONGTEXT. 

Il n'est pas possible de définir de valeur par défaut pour ces types

### SET
https://dev.mysql.com/doc/refman/8.0/en/set.html

Un SET permet de stocker une liste de valeurs au sein d'un même champ.
Comme pour un ENUM, les valeurs autorisées doivent être déclarées dans la définition de la table.
Un SET autorise jusqu'à 64 valeurs distinctes.

Il n'est pas possible de stocker une valeur dupliquée (exemple : `('a,a,b')` stockera `('a,b')`) ni 
de valeur comprenant une virgule.

## Chaînes binaires
https://dev.mysql.com/doc/refman/8.0/en/binary-varbinary.html    
https://dev.mysql.com/doc/refman/8.0/en/blob.html    
https://dev.mysql.com/doc/refman/8.0/en/storage-requirements.html

Les types BINARY, VARBINARY et BLOB sont les homologues de CHAR, VARCHAR et TEXT.
La seule différence entre eux réside dans la volonté de stocker des valeurs binaires (ex : le contenu d'une image)
sans un équivalent en caractères. Ces types sont donc dépourvus d'encodage et de collation.
En réalité ils utilisent un encodage et une collation nommés "binary" qui se contente de ne faire correspondre
aucun caractère.

Comme pour TEXT, BLOB existe en 4 déclinaisons (TINYBLOB, BLOB, MEDIUMBLOB et LONGBLOB) avec les mêmes capacités
de stockage.

### ENUM
https://dev.mysql.com/doc/refman/8.0/en/enum.html

Un type ENUM permet de lister les valeurs possibles pour un champ textuel.
Les valeurs possibles sont listées lors du CREATE TABLE (ou ALTER TABLE).
Lors d'INSERT, les valeurs sont converties en index entiers qui sont stockées à la place des valeurs textuelles
 (permettant un gain de place).
Lors d'un SELECT, la conversion est faite dans l'autre sens pour récupérer la valeur textuelle.

https://dev.mysql.com/doc/mysql-reslimits-excerpt/5.7/en/limits-frm-file.html    
Le nombre maximum de valeurs distinctes dans un ENUM est de 3000.

## Spatiaux
https://dev.mysql.com/doc/refman/8.0/en/spatial-types.html

Non abordé

## JSON
https://dev.mysql.com/doc/refman/8.0/en/json.html

Non abordé

## Exercice
> Consignes : Créez une table personnel permettant de stocker les champs suivants
>   - id (clé primaire)
>   - nom
>   - prenom
>   - date de naissance (date_naissance)
>   - age à l'inscription (age_inscription)
>   - email
>   - telephone (format 10 chiffres, sans indicateur +XX)
>   - biographie
>   - photo (pour stocker un jpeg de maximum 1Mo)
>   - sexe
>   - date dernière connexion (date_derniere_connexion)
>   - jours de présence dans la semaine (jours_presence)
> Les champs suivants sont obligatoires lors de l'inscription : nom, prenom, email    
> Si les jours de présence ne sont pas renseignés, on souhaite par défaut la valeur 'Lundi-Mardi'    
> Lors de la création d'un compte, on souhaite que la date de dernière connexion soit automatiquement
> renseignée comme la date actuelle
> Une fois votre table créée, exportez sa définition dans un fichier .sql