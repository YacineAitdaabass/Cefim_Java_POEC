# Groupes et agrégation

https://dev.mysql.com/doc/refman/8.0/en/group-by-functions.html  
https://dev.mysql.com/doc/refman/8.0/en/group-by-handling.html

Les fonctions de groupage et d'agrégation vont de paire.  
`GROUP BY` permet d'énoncer une ou plusieurs variables définissant des groupes
(un même groupe aura en commun ces variables).  
Les fonctions d'agrégation permettent de résumer ces groupes en une seule valeur.

Le mot clé `HAVING` permet de filtrer sur le résultat d'une table avec `GROUP BY` sans 
effecter de requête imbriquée

> A partir de la table categorie_billet :
> - Afficher la somme de tous les prix
> - Afficher le nombre de catégories par événement
> - Afficher la somme de tous les prix par événement
> - Afficher le prix de la catégorie la plus chère pour chaque événement
> - Afficher les événements dont le nombre de places est inférieur à 100