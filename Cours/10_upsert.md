# UPSERT
Dans certaines situations, il peut être intéressant de n'insérer une ligne
que si celle-ci n'existe pas déjà.
Mais cela nécessiterait de faire un select pour vérifier l'existance de la ligne,
puis le cas échéant insérer ou non notre ligne, peu efficace...
Pire : il s'agirait de 2 opérations et donc on prend le risque qu'entre le select et l'insert,
la ligne ait déjà été insérée par un autre processus (perte de l'atomicité).

Pour résoudre ce problème MySQL propose 3 solutions.

**Pour les 3, MySQL peut déterminer si une ligne existe déjà ou non uniquement sur la base
des contraintes de type PRIMARY ou UNIQUE.**

> Pré requis : exécutez le code suivant dans la base de données de votre choix

```
DROP TABLE IF EXISTS `examen`;

CREATE TABLE `examen` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `nom` varchar(20) DEFAULT NULL,
 `prenom` varchar(20) DEFAULT NULL,
 `examen` varchar(30) DEFAULT NULL,
 `score` int(11) DEFAULT NULL,
 `score_max` int(11) DEFAULT NULL,
 `date` datetime DEFAULT NULL,
 PRIMARY KEY (`id`),
 UNIQUE KEY `nom` (`nom`,`prenom`,`examen`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `examen`
    (
        `nom`,
        `prenom`,
        `examen`,
        `score`,
        `score_max`,
        `date`
    )
VALUES
    ('Holden', 'James', 'Python', 7, 10, '2019-07-21'),
    ('Man', 'Bat', 'C#', 6, 6, '2019-07-21'),
    ('Connor', 'Sarah', 'Java', 10, 10, '2019-07-21'),
    ('Alderson', 'Eliot', 'SQL', 15, 15, '2019-07-21');
```

## INSERT IGNORE
https://dev.mysql.com/doc/refman/8.0/en/insert.html

Ajouter le mot clé `IGNORE` dans un insert permet d'ignorer l'insertion si la ligne existe déjà.
Son existence est déterminée par les contrainte liées aux champs PRIMARY et UNIQUE.
Si ces contraintes ne peuvent être respectées : la ligne n'est pas insérée.

> Exercice : insérez via un INSERT IGNORE les 2 lignes suivante (nom, prenom, examen, score) :
>   - score de 10/10 pour James Holden sur Python (avec date actuelle)
>   - score de 9/10 pour Albert Einstein sur Python (avec date actuelle)
>
> Observez et commentez le résultat 

## REPLACE
https://dev.mysql.com/doc/refman/8.0/en/replace.html

En cas de ligne non présente, REPLACE fonctionne comme un INSERT.
Si la ligne existe déjà, celle-ci est supprimée puis recréée avec les nouvelles valeurs 
(DELETE suivi d'un UPDATE, mais de façon atomique)

> Exercice : insérez via un REPLACE les 2 lignes suivante (nom, prenom, examen, score) :
>   - score de 7/10 pour James Holden sur Python (avec date actuelle)
>   - score de 8/10 pour Captain America sur Python (avec date actuelle)
>
> Observez et commentez le résultat

## INSERT avec ON DUPLICATE KEY UPDATE
https://dev.mysql.com/doc/refman/5.5/en/insert-on-duplicate.html

Contrairement à REPLACE, en cas d'existance de la ligne, cette commande effectue un UPDATE.
Cete différence est cruciale pour les champs auto-incrémentés :
- REPLACE supprime la ligne et la recrée : un nouvel ID sera donc généré par auto-incrément
- INSERT avec ON DUPLICATE KEY UPDATE met à jour la ligne : l'ID sera conservé

> Exercice : insérez via un INSERT avec ON DUPLICATE KEY UPDATE les 2 lignes suivante (nom, prenom, examen, score) :
>   - score de 3/10 pour James Holden sur Python (avec date actuelle)
>   - score de 4/10 pour Ano Nymous sur Python (avec date actuelle)
>
> Observez et commentez le résultat