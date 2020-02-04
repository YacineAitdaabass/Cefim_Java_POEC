# Locks et Transactions
## Locks
> Consignes :
>- effectuez un LOCK de type READ sur une table de votre choix

```
LOCK tables examen READ;
```

>- instanciez une 2e connexion ssh (en conservant la 1e ouverte) et accédez à la console mysql


cmder : `ssh cefim@localhost -p 2222`  
ssh : `mysql -u root -p`


>- à partir de cette 2e session, faites un SELECT sur cette table, que se passe-t-il ?

```
select * from examen;
```
Le SELECT renvoie les résultats sans anomalie

>- à partir de cette 2e session, faites un UPDATE sur cette table, que se passe-t-il ?

```
update examen set examen='test';
```
Il ne se passe rien, la requête ne se termine jamais. Faites CTRL+C pour l'annuler.

>- effectuez un UNLOCK à partir de la 2e session : cela fonctionne-t-il ?

```
UNLOCK TABLES;
```
Non, les tables bloquées sont sous la responsabilité de la session qui a effectué le lock.
Il n'est pas possible de débloquer les tables d'une autre session de cette façon.

>- lancez à nouveau un UPDATE depuis la 2e session et (sans l'interrompre), exécutez
>un UNLOCK depuis la 1e session : que se passe-t-il ?

Session 2 : `update examen set examen='test';`  
Session 1 : `UNLOCK TABLES;`  
Dès l'exécution du UNLOCK, l'UPDATE est bien effectué.

Conclusion : un LOCK READ autorise la lecture des tables mais pas leur écriture.
>- effectuez un LOCK de type WRITE sur une table de votre choix
```
LOCK tables examen WRITE;
```
>- depuis la 2e session essayez de faire un SELECT et un UPDATE sur cette table, 
>quelles opérations sont autorisées ?
```
select * from examen;
update examen set examen='test';
```

Aucune action en lecture ni en écriture n'est autorisée sur un LOCK de type WRITE.


## Transactions
> Consignes :
>- ayez au moins une table avec au moins une ligne dans votre base, sinon créez en une
>- débutez une transaction
```
START TRANSACTION;
```
>- effectuez un UPDATE sur la table
```
UPDATE examen SET examen = 'pouet';
```
>- vérifiez l'état de la table avec un SELECT
```
SELECT examen FROM examen;
```
>- effectuez ce même SELECT depuis une autre session (sans interrompre l'actuelle) :
>que se passe-t-il ?

>- effectuez un UPDATE sur la table depuis la 2e session : que se passe-t-il ?

La requête est mise en attente. Tout comme pour un LOCK de type WRITE,
il n'est pas possible de modifier la table en question.
Cette protection est initiée dès lors qu'une modification a lieu sur une table lors 
d'une transaction. Cette protection est levée dès la fin d'une transaction 
(COMMIT, ou automatiquement lors d'une requête "simple") 

>- effectuez un ROLLBACK
```
ROLLBACK;
```
>- vérifiez à nouveau l'état de la table avec un SELECT : que constatez-vous ?

Le ROLLBACK a permis d'annuler l'UPDATE. Nous sommes revenu à l'état pré transaction de la table;

>- recommencez la procédure mais terminez avec un COMMIT au lieu d'un ROLLBACK,
>puis faites à nouveau un SELECT : que constatez-vous ?
```
START TRANSACTION;
UPDATE examen SET examen = 'pouet';
COMMIT;
SELECT examen FROM examen;
```
Cette fois ci les modifications sont définitives et accessibles depuis toutes les sessions.
