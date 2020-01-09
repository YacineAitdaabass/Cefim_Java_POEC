# Jointures
## Exercice

> Consignes : exécutez les requêtes suivantes pour générer les 3 tables d'exercice 
```
CREATE TABLE `client` ( `id` INT NOT NULL ,  `nom` VARCHAR(50) NOT NULL ) ENGINE = InnoDB;
INSERT INTO `client` (`id`, `nom`) VALUES ('1', 'Batman'), ('2', 'Captain Igloo'), ('3', 'Spiderman'), ('4', 'Mr Propre'), ('5', 'Catwoman'), ('6', 'Superman'), ('7', 'Wonderwoman'), ('8', 'Mr Robot'), ('9', 'Eliot Alderson'), ('10', 'James Holden');

CREATE TABLE `bidule`.`client` ( `id` INT NOT NULL ,  `nom` VARCHAR(50) NOT NULL ) ENGINE = InnoDB;
INSERT INTO `article` (`id`, `libelle`) VALUES ('1', 'Liquide vaisselle'), ('2', 'Gauffres'), ('3', 'Kit de réparation voiture'), ('4', 'Tractopelle'), ('5', 'Bouée'), ('6', 'Hélicoptère'), ('7', 'Vaisseau spatial'), ('8', 'Chocolat'), ('9', 'Place de concert'), ('10', 'Ketchup');

CREATE TABLE `panier` ( `id_client` INT NOT NULL , `id_article` INT NOT NULL , `quantite` INT NOT NULL ) ENGINE = InnoDB;
INSERT INTO `panier` (`id_client`, `id_article`, `quantite`) VALUES ('1', '1', '1'),  ('2', '1', '1'), ('3', '2', '1'), ('4', '2', '2'), ('5', '1', '1'), ('6', '2', '2'), ('7', '1', '1'), ('8', '1', '1'), ('9', '2', '1'), ('1', '3', '2'), ('2', '4', '1'), ('3', '4', '10'), ('4', '4', '1'), ('5', '3', '1'), ('6', '4', '1'), ('7', '3', '7'), ('8', '4', '1'), ('9', '4', '1'), ('1', '5', '1'), ('2', '6', '3'), ('3', '6', '1'), ('4', '6', '1'), ('5', '6', '4'), ('6', '5', '2'), ('7', '6', '1'), ('8', '6', '1'), ('9', '6', '40'), ('1', '8', '1'), ('2', '8', '1'), ('3', '7', '1'), ('4', '7', '5'), ('5', '7', '1'), ('6', '7', '1'), ('7', '7', '2'), ('8', '7', '2'), ('9', '7', '2'), ('1', '9', '1'), ('2', '9', '1'), ('3', '9', '3'), ('4', '9', '1'), ('5', '9', '1'), ('6', '9', '25'), ('7', '9', '1'), ('8', '10', '1'), ('9', '10', '12'), ('10', '1', '1'), ('10', '3', '10'), ('10', '7', '2'), ('10', '9', '1');
```
> Puis rédigez le requêtes suivantes en organisant l'ordre de vos jointures
>afin de générer la requête la plus efficiente possible pour MySQL :
>- afficher les id des articles achetés la cliente Wonderwoman
```
SELECT p.id_article
FROM client AS c
LEFT JOIN panier AS p
	ON p.id_client = c.id
WHERE c.nom = 'Wonderwoman';
```

>- afficher les articles (leur libellé) du client dont l'id vaut 10
```
SELECT a.libelle FROM panier AS p
LEFT JOIN article AS a
	ON a.id = p.id_article
WHERE p.id_client = 10;
```
>**[Pour les items suivants vous n'avez plus le droit de filtrer par id dans les clauses WHERE]**
>- afficher les articles (leur libellé) achetés par le client Batman
```
SELECT a.libelle FROM client AS c
LEFT JOIN panier AS p
	ON p.id_client = c.id
LEFT JOIN article AS a
	ON a.id = p.id_article
WHERE c.nom = 'Batman';
```
>- afficher les articles (libellés) ET les quantités associées pour le client Superman
```
SELECT a.libelle, p.quantite FROM client AS c
LEFT JOIN panier AS p
	ON p.id_client = c.id
LEFT JOIN article AS a
	ON a.id = p.id_article
WHERE c.nom = 'Superman';
```
>- afficher les clients (noms) qui ont acheté du chocolat
```
SELECT c.nom
FROM article a
LEFT JOIN panier p
	ON p.id_article = a.id
LEFT JOIN client AS c
	ON c.id = p.id_client
WHERE a.libelle = 'Chocolat';
```
>- afficher les clients qui ont acheté un même article en au moins 10 exemplaires
```
SELECT DISTINCT c.nom
FROM panier AS p
LEFT JOIN client AS c
	ON c.id = p.id_client
WHERE p.quantite >= 10;
```
>- pour chaque achat d'un article à au moins 10 exemplaires, affichez une ligne avec :
>nom du client, libellé de l'article, quantité achetée
```
SELECT DISTINCT c.nom, a.libelle, p.quantite
FROM panier AS p
LEFT JOIN client AS c
	ON c.id = p.id_client
LEFT JOIN article AS a
	ON a.id = p.id_article
WHERE p.quantite >= 10;
```