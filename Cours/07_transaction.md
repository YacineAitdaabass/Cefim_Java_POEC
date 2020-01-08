# Locks et Transactions

Par défaut, MySQL fonctionne en mode AUTOCOMMIT, ce qui signifie que toute requête
envoyée est directement appliquée à nos tables. Chacune de nos requêtes est atomique.

Rappel : l'atomicité est le fait qu'un ensemble d'actions est appliqué en tout ou rien.

L'atomicité par requête ne répond pas à tous les besoins. 
Parfois nous aimerions pouvoir grouper plusieurs requêtes successives de façon atomique.

Exemple : vous devez exécuter une opération massive sur vos bases,
impliquant un SELECT rapportant de nombreuses lignes.
Vous traitez ces lignes via un algorithme qui effectue une
succession de UPDATE/DELETE sur différentes tables.
Entre le SELECT qui vous donne un instantané et l'application de vos UPDATE/DELETE,
l'état des tables concernées ne doit pas être modifié.
Plus cette opération sera longue et plus vous prenez le risque qu'une requête 
vienne modifier vos tables pendant le processus.

Autre exemple : vous devez effectuer un virement entre 2 comptes bancaires,
consitant en 2 updates (l'un diminue le montant du compte A de X €, l'autre augmente celui
du compte B de X €). Si l'une des 2 opérations échoue, l'autre ne doit pas non plus avoir lieu
(sinon ces X € se seront soit volatilisé, soit auront été donnés à B en cadeau).

## Locks
https://dev.mysql.com/doc/refman/8.0/en/lock-tables.html

Une première solution pourrait être de verrouiller la/les tables
concernées par votre opération de façon à ce que seule votre session puisse y écrire.

Pour cela les commandes `LOCK TABLES` et `UNLOCK TABLES` peuvent être utilisées 

> Consignes :
>- effectuez un LOCK de type READ sur une table de votre choix
>- instanciez une 2e connexion ssh (en conservant la 1e ouverte) et accédez à la console mysql
>- à partir de cette 2e session, faites un SELECT sur cette table, que se passe-t-il ?
>- à partir de cette 2e session, faites un UPDATE sur cette table, que se passe-t-il ?
>- effectuez un UNLOCK à partir de la 2e session : cela fonctionne-t-il ?
>- lancez à nouveau un UPDATE depuis la 2e session et (sans l'interrompre), exécutez
>un UNLOCK depuis la 1e session : que se passe-t-il ?
>- effectuez un LOCK de type WRITE sur une table de votre choix
>- depuis la 2e session essayez de faire un SELECT et un UPDATE sur cette table, 
>quelles opérations sont autorisées ?

Imaginons que la session 1 qui a fait le LOCK soit hors contrôle (mal déconnectée,
user malintentionné, etc) et que le LOCK persiste. Nous allons voir comment lever un LOCK
de façon forcée.

>Consignes :
>- conservez le LOCK du précédent exercice actif, ou créez en un nouveau
>- avec une autre session, affichez la liste des locks via : `show open tables where in_use>0;`
>- affichez la liste des processus mysql en cours : `show processlist;`
>- repérez le processus à l'origine du lock et notez son Id. Pour cela étudiez l'ensemble des variables fournies :
>il s'agit d'une session root exécutée depuis localhost, actuellement en travail sur la base cefim (
>sauf si fait usage dans autre USE depuis) et qui ne devrait pas avoir de requête en cours (colonne Info)
>- mettez fin au processus en question : `kill id_du_processus;`
>- essayez à nouveau une opération interdite par votre type de LOCK : cela doit fonctionner
>- revenez sur la 1e session (celle ayant subi le kill), exécutez une commande quelconque
>(ex : `SHOW TABLES;`) : une reconnexion est faite avant de pouvoir faire la requête

## Transactions
https://dev.mysql.com/doc/refman/8.0/en/commit.html

Une autre solution consiste à utiliser les transactions :
- invoquez un début de transaction avec `START TRANSACTION;`
- effectuez vos différentes requêtes
- signalez la fin de votre transaction via `COMMIT;`

Ce faisant, la transaction complète sera atomique. Ainsi aucune autre requête
ne peut venir s'intercaler au milieu. En réalité, jusqu'à présent toute requête
était une transaction en soi. On ne fait ici que mettre plus de requêtes dans une seule transaction.

A la moindre erreur lors de la transaction, toutes les requêtes passées seront annulées,
c'est ce qu'on appelle un ROLLBACK. Vous pouvez vous même déclencher un ROLLBACK
au milieu d'une transaction via `ROLLBACK;`

Attention, certaines opérations au sein d'une transaction donnent systématiquement lieu 
à un COMMIT (notamment toutes celles qui touchent au DDL) :
https://dev.mysql.com/doc/refman/8.0/en/implicit-commit.html

Vous pouvez également désactiver le mode AUTOCOMMIT de MySQL le temps de votre session
via : `SET autocommit = 0;`. Par la suite il vous faudra déclencher des `COMMIT` pour
exécuter vos transactions.

> Consignes :
>- ayez au moins une table avec au moins une ligne dans votre base, sinon créez en une
>- débutez une transaction
>- effectuez un UPDATE sur la table
>- vérifiez l'état de la table avec un SELECT
>- effectuez ce même SELECT depuis une autre session (sans interrompre l'actuelle) :
>que se passe-t-il ?
>- effectuez un UPDATE sur la table depuis la 2e session : que se passe-t-il ?
>- effectuez un ROLLBACK
>- vérifiez à nouveau l'état de la table avec un SELECT : que constatez-vous ?
>- recommencez la procédure mais terminez avec un COMMIT au lieu d'un ROLLBACK,
>puis faites à nouveau un SELECT : que constatez-vous ?

## Conséquences
L'usage de LOCKS ou de transactions implique que vos tables seront temporairement 
indisponibles pour les autres sessions.

Si votre opération ne nécessite aucune modification des tables (lecture seule)
vous pouvez utiliser `LOCK READ` ou une transaction `READ ONLY`. Dans ce cas
les autres sessions conservent également un accès en lecture seule.
Aucune session ne peut écrire sur les tables durant la transaction/LOCK.

Dans les autres cas, toute requête (même de lecture) tentée sur une table protégée 
sera mise en attente.