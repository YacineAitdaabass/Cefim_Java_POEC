#Index et contraintes
## Index
### Création simple d'un index

> - Faites un select avec la condition id=1

`SELECT * FROM examen WHERE id=1;`

> - Recommencez en ajoutant le mot clé "EXPLAIN en début de requête".
>Observez les champs possible_keys (index disponibles) et key (index utilisés).
>On constate ici qu'aucun index n'est utilisé

`EXPLAIN SELECT * FROM examen WHERE id=1;` 
 
> - Ajoutez un index sur le champ id, relancez votre requête avec EXPLAIN et observez 
>les différences

`ALTER TABLE examen ADD INDEX(id);`
ou
`CREATE INDEX index_name ON examen (id);`

Il y a 2 différences entre un ALTER TABLE ADD INDEX et un CREATE INDEX :
- ALTER TABLE est la seule solution possible pour créer un index PRIMARY (voir plus bas).
L'index créé prend alors le nom du 1er champ renseigné
- CREATE INDEX nécessite/permet de donner un nom à l'index

Cette fois-ci l'index est bien utilisé

### Créer un index multiple (sur plusieurs champs)

>- Créez un index sur le champ nom

`ALTER TABLE examen ADD INDEX(nom);`

>- Créez un index sur le champ prenom

`ALTER TABLE examen ADD INDEX(prenom);`

>- Exécutez un select (nom='Holden' et prenom='James') avec EXPLAIN

`EXPLAIN SELECT * FROM examen WHERE nom='Holden' and prenom='James';`

>- Que constatez-vous ?

Seul l'index sur le nom est utilisé.

>- Créez un index composite sur les champs nom et prenom

ALTER TABLE examen ADD INDEX(nom, prenom);

>- Exécutez à nouveau un select (nom='Holden' et prenom='James') avec EXPLAIN

`EXPLAIN SELECT * FROM examen WHERE nom='Holden' and prenom='James';`

>- Que constatez-vous ?

L'index sur le nom est toujours le seul utilisé.

> Forcer MySQL à utiliser notre index composite en supprimant les autres index

```
ALTER TABLE examen DROP INDEX id;
ALTER TABLE examen DROP INDEX nom;
ALTER TABLE examen DROP INDEX prenom;
EXPLAIN SELECT * FROM examen WHERE nom='Holden' and prenom='James';
```

### UNIQUE

> Sur la table précédente, ajoutez une contrainte UNIQUE sur les champs nom et prenom
>afin de prévenir la possibilité d'homonymes. Il doit être possible 
>d'avoit deux Albert ou deux Schmidt, mais pas deux Albert Schmidt.\

`ALTER TABLE examen ADD UNIQUE(nom, prenom);`

>Vérifiez que cela fonctionne en tentant d'insérer ces 4 lignes via 4 INSERT distincts :
>- Eric Schmidt
>- Robert Schmidt
>- Eric Dupont
>- Eric Dupont

>Seul le dernier INSERT devrait être refusé

```
INSERT INTO examen (id, nom, prenom) VALUES (1, 'Schmidt', 'Eric');
INSERT INTO examen (id, nom, prenom) VALUES (2, 'Schmidt', 'Robert');
INSERT INTO examen (id, nom, prenom) VALUES (3, 'Dupont', 'Eric');
INSERT INTO examen (id, nom, prenom) VALUES (4, 'Dupont', 'Eric');
```

>Videz la table et réinsérer les 4 lignes en un unique INSERT : quel est le résultat ?
>Et pourquoi ?

```
TRUNCATE TABLE examen;
INSERT INTO examen (id, nom, prenom) VALUES
    (1, 'Schmidt', 'Eric'),
    (2, 'Schmidt', 'Robert'),
    (3, 'Dupont', 'Eric'),
    (4, 'Dupont', 'Eric');
```

Cette fois, aucune des 4 lignes n'est insérées. C'est le principe même des requêtes atomiques 
(tout ou rien n'est exécuté).

>Sur la table précédente, supprimez tous les index puis :
>- créer un PRIMARY KEY sur le champ id avec un AUTOINCREMENT

```
ALTER TABLE examen ADD PRIMARY KEY(id);
ALTER TABLE examen CHANGE id id INT(11) NOT NULL AUTO_INCREMENT;`
```

>- imaginons maintenant que le champ id n'existe pas (supprimez ce champ),
>concevez une clé primaire sur notre table qui nous assure qu'un individu 
>ne puisse avoir qu'une seule note à un même examen

```
ALTER TABLE examen DROP id;
ALTER TABLE examen ADD PRIMARY KEY(nom, prenom, examen);
```



### Contraintes de clé

> Puis rédigez une jointure permettant d'afficher une table avec les variables :
>- nom
>- prénom
>- examen
>- score
>- score_max

```
SELECT etu.nom, etu.prenom, exa.examen, exa.score, exa.score_max
FROM etudiant etu
LEFT JOIN examen exa ON exa.id_etudiant = etu.id;
```

> - Définissez quelle est la table parent et la table enfant dans l'exercice

Table parent = etudiant (clé primaire = id)
Table enfant = examen (clé secondaire = id_etudiant)
Une seule clé primaire à créer donc

> - Effectuez la/les déclarations de clé(s) primaire(s) sur ces tables

`ALTER TABLE etudiant ADD PRIMARY KEY(id);`

> - Déclarez une contrainte de clé de façon à interdire la suppression d'un étudiant s'il possède des examens

```
ALTER TABLE examen
ADD CONSTRAINT etudiant_examen
FOREIGN KEY (id_etudiant) 
REFERENCES etudiant(id) 
ON DELETE RESTRICT 
ON UPDATE RESTRICT;
```
Note : par défaut les comportements ON DELETE et ON UPDATE sont RESTRICT, 
il est donc possible de les omettre sur la ligne précédente.

> - Testez la suppression d'un étudiant : cela ne doit pas marcher
>(si besoin, rechargez les données de départ pour réessayer)

`DELETE FROM etudiant WHERE id = 1;`

> - Supprimez cette contrainte de clé

`ALTER TABLE examen DROP FOREIGN KEY etudiant_examen;`

> - Recréez une contrainte mais cette fois-ci la suppression d'un étudiant doit également supprimer
>ses examens

```
ALTER TABLE examen
ADD CONSTRAINT etudiant_examen 
FOREIGN KEY (id_etudiant) 
REFERENCES etudiant(id) 
ON DELETE CASCADE 
ON UPDATE RESTRICT;
```

> - Testez la suppression d'un étudiant : cela ne doit également supprimer ses examens

`DELETE FROM etudiant WHERE id = 1;`