# Base de données

## Vocabulaire

- **Table** (table) : ensemble de lignes et colonnes
  - Les **colonnes** (columns, fields, variables) représentent les variables, elles sont typées 
  (numérique, caractère, date, etc) et sont figées en production 
  (l'application n'ajoute pas de nouvelle colonne)
  - Les **lignes** (rows) sont dynamiques : l'application ajoute, modifie, supprime des lignes 
  au besoin en renseignant des valeurs propres aux colonnes 

- **Base de données** (database) : ensemble des tables propres à un projet
- (Entrepôt de données : parfois défini comme regroupement de bases de données)

Remarque : il existe une distinction entre les termes `schema` et `database`.
Sur la quasi totalité des SGBD, un schéma est un ensemble de tables, une base de données
est un ensemble de schémas. Cette subdibision n'existe pas dans MySQL/MariaDB, ainsi
les termes `schema` et `database` sont synonymes et peuvent être intervertis à tout moment
dans les commandes. 

Nous allons donc concevoir une base de données dans laquelle nous ajouterons des tables.  
Ces tables auront une liste de champs qui seront alimentés (insertion de lignes).  
Nous créerons également des utilisateurs dédiés à la base de données.

## Base de données
### Création d'une base de données [CREATE DATABASE]
Syntaxe : https://dev.mysql.com/doc/refman/8.0/en/create-database.html
https://dev.mysql.com/doc/refman/8.0/en/charset.html

`CHARACTER_SET` : l'encodage de la base de données  
`COLLATE` : la méthode utilisée pour comparer les caractères d'un encodage (A < a ? A > a ? A = a ?),
utile pour classer par ordre alphabétique par exemple

En l'absence de précision, le charset latin1 et le collate latin1_swedish_ci étaient utilisés par défaut
(ça n'est plus le cas depuis MySQL 8).
Ca n'est pas forcément le meilleur paramétrage :
- latin1 (ou Latin-1 ou ISO-8859-1) correspond à un encodage sur 8 bits, ce qui autorise 256 caractères.
https://fr.wikipedia.org/wiki/ISO/CEI_8859-1  
Des caractères français en sont exclus : Œ œ Ÿ, il en va de même pour d'autres langues latines.
Le problème est d'autant plus important sur des applications internationales (cyrillique, chinois, etc)
- latin1_swedish_ci : un collate est toujours préfixé du nom du charset suivi de la logique issue d'une langue.
Par exemple : les allemands considèrent "ß" équivalent à "ss", mais pas les suédois  
Le suffixe _ci signifie "case insensitive" (pas de différence entre majuscule et minuscule),
les alternatives sont _cs ("case sensitive") et _bin ("binary" = compare la valeur binaire des caractères,
rarement utile)

Ces valeurs par défaut sont liées à l'histoire de MySQL (MySQL AB était une entreprise suédoise),
il n'y a pas lieu de les utiliser en France / à l'international.

Pour afficher la liste des charsets disponibles : `SHOW CHARACTER SET;`  
Pour afficher la liste des collations disponibles : `SHOW COLLATION;`

Pour les cas généraux, il est recommandé d'utiliser :
- charset `utf8mb4`. On utilisait initialement le charset utf8 (https://fr.wikipedia.org/wiki/UTF-8) mais celui-ci 
était tronqué dans sa prise en charge. MySQL a ensuite publié utf8mb4 qui corrigeait cette anomalie
(https://dev.mysql.com/doc/refman/5.5/en/charset-unicode-utf8mb4.html) 
- collation : `utf8mb4_0900_ai_ci`. On utilisait autrefois utf8mb4_general qui était très respectueuse
des cas particuliers mais au prix d'un calcul beaucoup plus couteux. 
utf8mb4_unicode_ci est alors apparu pour simplifier le calcul au prix de quelques erreurs.
MySQL a ensuite publié utf8mb4_0900_ai_ci qui est plus respectueux que utf8mb4_unicode_ci
sans surcoût majeur en calcul, mais MariaDB ne l'a pas encore implémenté.  
On préférera une collation _ci car le respect de la casse mène parfois à des résultats qui
semblent peu naturels.

Contenu des collations : http://mysql.rjweb.org/utf8mb4_collations.html

Une table, voir même une colonne peut posséder son propre encodage/collation si besoin.

> Consigne : créez une base de données nommée `cefim` avec l'encodage et la collation cités
> ci-dessus
 
**Astuce** : n'hésitez pas à rédiger votre code SQL dans un blocnote/Notepad++
pour le réutiliser. Vous pouvez ensuite le copier-coller dans Cmder.
Un fichier .sql est un fichier texte renfermant N commandes SQL.

**Rappel** : n'oubliez pas que toute commande SQL se termine par un `;`

## Modification une base de données [ALTER DATABASE]
Syntaxe : https://dev.mysql.com/doc/refman/8.0/en/alter-database.html

> Consigne : modifier la base de données précédente et attribuez lui la collation latin1_german1_ci

Vous pouvez à tout moment consulter la définition (DDL) de votre base de données à l'aide de la commande
`SHOW CREATE DATABASE cefim;`  
Celle-ci devrait vous renvoyer la définition suivante :
```
CREATE DATABASE `cefim` 
/*!40100 DEFAULT CHARACTER SET latin1 COLLATE latin1_german1_ci */ 
/*!80016 DEFAULT ENCRYPTION='N' */
```

## Supression d'une base de données [DROP DATABASE]
Syntaxe : https://dev.mysql.com/doc/refman/8.0/en/drop-database.html

> Consigne : supprimez la base de données nommée `cefim`

Note : vous pouvez à tout moment recréer la table à l'identique en exécutant le DDL précédemment obtenu
via `SHOW CREATE DATABASE cefim;`   (n'oubliez pas d'ajouter le ";" à la fin du DDL) 