# Exercice

> Requêtes :
> - pour les 50 achats de billets les plus récents, afficher le mail des clients les ayant acheté
> et la date d'achat
```
select c.mail client, b.date_action date_achat
from billet b
left join client c
	on c.id = b.achat_client_id
where achat_client_id is not null
order by date_action desc
limit 50;
```
> - afficher le nombre d'événements pour chaque organisation en une seule requête 
```
select o.mail organisateur, count(*) nb_evenements
from organisateur o
left join evenement e
	on e.organisateur_id = o.id
group by o.id;
```
> - classez les organisateurs par nombre de billets vendus décroissant
```
select o.mail organisateur, count(*) nb_achats
from billet b
left join evenement e
	on e.id = b.evenement_id
left join organisateur o
	on e.organisateur_id = o.id
where b.achat_client_id is not null
group by o.id
order by nb_achats desc;
```

>- pour chaque événement, afficher **4** colonnes : le nombre de billets encore disponibles à la vente, le nombre de billets
>en panier, le nombre de billets achetés et le nombre total de billets  

```
WITH
	total AS (
        SELECT
            cb.evenement_id id,
            SUM(cb.nombre_places) nb
        FROM categorie_billet cb
        GROUP BY cb.evenement_id
	),
	panier AS (
        SELECT
        	b.evenement_id id,
        	COUNT(*) nb
        FROM billet b
        WHERE b.panier_client_id is not null
        GROUP BY b.evenement_id
    ),
    achat AS (
        SELECT
        	b.evenement_id id,
        	COUNT(*) nb
        FROM billet b
        WHERE b.achat_client_id is not null
        GROUP BY b.evenement_id
    )
SELECT 
	total.id,
    total.nb nb_places_total,
    panier.nb nb_places_panier,
    achat.nb nb_places_achat,
    total.nb - (panier.nb + achat.nb) nb_places_dispo
FROM total
INNER JOIN panier USING(id)
INNER JOIN achat USING(id);
```

> - affichez les organisateurs ayant entre 1000 et 2000 tickets encore en panier.
> Faites-le sans utiliser de subqueries
```
select o.mail, count(*) nb_paniers
from organisateur o
left join evenement e
	on e.organisateur_id = o.id
left join billet b
	on b.evenement_id = e.id
where b.panier_client_id is not null
group by o.id
having nb_paniers between 1000 and 2000
order by nb_paniers desc;
```
> - qu'est ce que cela implique pour l'application ?

La procédure de remise en vente des billets après X jours en panier ne fonctionne pas

> - rédigez une requête (sans l'exécuter !) qui puisse corriger ce problème

Exemple pour une sauvegarde de 2 jours max dans le panier : (requête à exécuter toutes les heures par exemple)
```
DELETE billet
WHERE panier_client_id is not null
AND date_action < ADDDATE(NOW(), INTERVAL -2 DAY);
```
> - pour chaque organisateur, affichez les 3 clients (mail) ayant dépensé le plus d'argent pour leurs événements respectifs
```
select * FROM (
    select 
        *,
        ROW_NUMBER() OVER (PARTITION BY organisateur ORDER BY depenses DESC) rang
    from (
        select 
            o.mail organisateur, 
            c.mail client, 
            sum(cb.prix) depenses
        from organisateur o
        left join evenement e
            on e.organisateur_id = o.id
        left join billet b
            on b.evenement_id = e.id
        left join client c
            on c.id = b.achat_client_id
        left join categorie_billet cb
            on cb.id = b.categorie_billet_id
        where b.achat_client_id is not null
        group by o.id, c.id
    ) tmp
) tmp2
where rang <= 3;
```

Ou mieux avec un CTE :

```
WITH
    tmp1 AS (
        select 
            o.mail organisateur, 
            c.mail client, 
            sum(cb.prix) depenses
        from organisateur o
        left join evenement e
            on e.organisateur_id = o.id
        left join billet b
            on b.evenement_id = e.id
        left join client c
            on c.id = b.achat_client_id
        left join categorie_billet cb
            on cb.id = b.categorie_billet_id
        where b.achat_client_id is not null
        group by o.id, c.id
    ),
    tmp2 AS (
        select 
            *,
            ROW_NUMBER() OVER (PARTITION BY organisateur ORDER BY depenses DESC) rang
        from tmp1
    )
select * 
FROM tmp2
where rang <= 3;
```

> - afficher le nombre de filleuls pour chaque compte organisateur. Attention, si A parraine B et que B parraine C,
> il faut compter 2 filleuls pour A.  

On souhaite donc effectuer des jointures pour chainer les parrains et filleuls,
cependant nous ne connaissons pas le nombre de jointures nécessaires
pour parcourir toute la chaine de [parrain-filleuls][parrain-filleuls]...

Dans ces situations il faut faire une requête récursive

```
WITH RECURSIVE rec AS
(
    SELECT
    	id,
    	CAST(id AS CHAR(30)) path,
    	mail mail_root,
    	mail mail_terminal,
    	parrain_id,
    	0 profondeur
    FROM organisateur
    WHERE parrain_id IS NULL
    UNION ALL
    	SELECT
    		org.id,
    		CONCAT(rec.path, '>', CAST(org.id AS CHAR(30))) path,
    		rec.mail_root mail_root,
    		org.mail mail_terminal,
    		org.parrain_id,
    		profondeur+1
    FROM organisateur org
    INNER JOIN rec
    	ON rec.id = org.parrain_id
)
SELECT path, mail_root, mail_terminal, profondeur FROM rec;
```

Path nous donne l'enchaînement des id (du parrain root jusqu'au dernier filleul).
Profondeur nous indique la génération.

Pour obtenir le nombre de filleuls par parrain, il nous faut encore agréger ces informations
par parrain :

```
WITH RECURSIVE rec AS
(
    SELECT
    	id,
    	CAST(id AS CHAR(30)) path,
    	mail mail_root,
    	mail mail_terminal,
    	parrain_id,
    	0 profondeur
    FROM organisateur
    WHERE parrain_id IS NULL
    UNION ALL
    	SELECT
    		org.id,
    		CONCAT(rec.path, '>', CAST(org.id AS CHAR(30))) path,
    		rec.mail_root mail_root,
    		org.mail mail_terminal,
    		org.parrain_id,
    		profondeur+1
    FROM organisateur org
    INNER JOIN rec
    	ON rec.id = org.parrain_id
)
SELECT mail_root AS parrain, COUNT(*) - 1 AS nb_filleuls
FROM rec
GROUP BY mail_root;
```


> - victime de son succès, eventcode voit sa base atteindre des volumes 
> important et les requêtes sont de plus en plus longues à être exécutées.
> La table billet approche les 100 millions de lignes, c'est elle qui pose
> le plus de problèmes. 
> On note que les requêtes impliquant cette table ne concerne
> généralement qu'un seul organisateur à la fois.
> Que proposez vous pour améliorer la latence des requêtes sur cette
> table ? Quelles en seront les conséquences ?

La solution consiste à partitionner la table billet selon l'organisateur.
Cependant celle-ci ne possède pas de référence à l'organisateur mais seulement à l'événement.
Il faut donc ajouter une clé étrangère référençant l'id de la table organisateur

```
-- ajout du champ organisateur_id à la table billet
ALTER TABLE `billet`
ADD `organisateur_id` INT NOT NULL;

-- mise à jour de ce champ
UPDATE billet b
LEFT JOIN categorie_billet cb
	ON cb.id = b.categorie_billet_id
LEFT JOIN evenement e
	ON e.id = cb.evenement_id
SET b.organisateur_id = e.organisateur_id;

-- application de la contrainte
ALTER TABLE `billet`
ADD FOREIGN KEY (`organisateur_id`)
    REFERENCES `organisateur`(`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT;
```

Il y a aussi une contrainte : les contrainte UNIQUE (et donc aussi les PRIMARY) doivent
comprendre tous les champs utilisés pour partitionner.
Ici on va partitionner sur l'organisateur_id, lequel n'est pas dans la PRIMARY_KEY.
Il faut donc l'intégrer à notre PRIMARY KEY de façon à avoir : PRIMARY(id, organisateur_id).

```
ALTER TABLE `billet`
  DROP PRIMARY KEY,
   ADD PRIMARY KEY(
     `id`,
     `organisateur_id`);
```

A ce stade, si on essaye de partitionner on obtient l'erreur suivante :  
1506 - Foreign keys are not yet supported in conjunction with partitioning

Il faut donc supprimer nos contraintes de clé (vers et depuis la table billet) avant de poursuivre.
➡ supprimer toutes les contraintes de clé impliquées. Attention ! Les index eux peuvent être conservés !

On peut ensuite créer des partitions sur la base du champ organisateur_id.
Il s'agit alors de choisir le bon type.
On élimine d'office RANGE qui s'applique sur des plages de données.
LIST nécessiterait de sépcifier les valeurs une à une pour l'organisateur_id : peu pratique.
HASH semble tout indiqué, on précise alors le nombre de partitions à créer (soit le 
nombre d'organisateurs existants en base) :

```
ALTER TABLE `billet` 
PARTITION BY HASH (organisateur_id) 
    PARTITIONS 16;
```


Cependant la performance des requêtes inter-partitions sera dégradée. De plus il n'est pas possible d'ajouter
des partitions automatiquement. En cas de nouvel organisateur il faudra ajouter une partition dédiée.

Il est maintenant possible de supprimer de façon très rapide tous les billets d'un organisateur 
en supprimant simplement une partition (revient à supprimer un fichier au lieu d'un DELETE WHERE coûteux).

>- **Afficher les recettes cumulées depuis le 09/12/2019 pour l'organisateur dont l'id vaut 1 (une ligne par billet acheté pour un des événements de l'organisateur)**

Pour commencer on affiche la liste des achats de l'organisateur demandé :
```
SELECT b.date_action, cb.prix
FROM evenement e
LEFT JOIN categorie_billet cb
    ON cb.evenement_id = e.id
LEFT JOIN billet b
    ON b.categorie_billet_id = cb.id
WHERE
    e.organisateur_id = 1
    AND b.achat_client_id IS NOT NULL
    AND b.date_action > '2019-09-12'
ORDER BY b.date_action;
```

Les fonctions fenêtrées permettent d'appliquer une fonction glissante le long des lignes.  
Nous cherchons donc une fonction qui ajoute pour chaque nouvelle ligne le total précédent.  
https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html  
Une première piste pourrait être d'utiliser LAG() ou LEAD() pour ajouter à la ligne active le prix de la ligne précédente

```
SELECT
    b.date_action,
    cb.prix,
    LEAD(cb.prix,1) OVER (
        PARTITION BY e.organisateur_id
        ORDER BY b.date_action ) prix_suivant
FROM evenement e
LEFT JOIN categorie_billet cb
    ON cb.evenement_id = e.id
LEFT JOIN billet b
    ON b.categorie_billet_id = cb.id
WHERE
    e.organisateur_id = 1
    AND b.achat_client_id IS NOT NULL
    AND b.date_action > '2019-09-12'
ORDER BY b.date_action;
```

Mais utiliser ces valeurs pour faire une somme ne marche pas car elle ne se cumule pas avec les sommes précédentes : 

```
SELECT
    b.date_action,
    cb.prix,
    LEAD(cb.prix,1) OVER (
        PARTITION BY e.organisateur_id
        ORDER BY b.date_action ) + prix somme
FROM evenement e
LEFT JOIN categorie_billet cb
    ON cb.evenement_id = e.id
LEFT JOIN billet b
    ON b.categorie_billet_id = cb.id
WHERE
    e.organisateur_id = 1
    AND b.achat_client_id IS NOT NULL
    AND b.date_action > '2019-09-12'
ORDER BY b.date_action;
```

MySQL ne propose pas à ce jour de fonction du type CUM_SUM() pour permettre cela. A la place il faut faire appel
à la déclaration de variable. Ainsi on peut faire :

```
SET @cumul := 0;
SELECT
    b.date_action,
    cb.prix,
    @cumul := @cumul + cb.prix AS cumul
FROM evenement e
LEFT JOIN categorie_billet cb
    ON cb.evenement_id = e.id
LEFT JOIN billet b
    ON b.categorie_billet_id = cb.id
WHERE
    e.organisateur_id = 1
    AND b.achat_client_id IS NOT NULL
    AND b.date_action > '2019-09-12'
ORDER BY b.date_action;
```

Mais les résultats sont étranges... car notre cumul est calculé au fil des lignes AVANT l'application de
nos clauses WHERE ! Il faut donc calculer le cumul après : soit via une subquery, soit via un CTE.

Via une subquery :

```
SET @cumul := 0;
SELECT 
    *, 
    @cumul := @cumul + tmp.prix AS cumul
FROM ( 
    SELECT
        b.date_action,
        cb.prix
    FROM evenement e
    LEFT JOIN categorie_billet cb
        ON cb.evenement_id = e.id
    LEFT JOIN billet b
        ON b.categorie_billet_id = cb.id
    WHERE
        e.organisateur_id = 1
        AND b.achat_client_id IS NOT NULL
        AND b.date_action > '2019-09-12'
    ORDER BY b.date_action) tmp
;
```

Via un CTE :

```
SET @cumul := 0;
WITH tmp AS (
    SELECT
        b.date_action,
        cb.prix
    FROM evenement e
    LEFT JOIN categorie_billet cb
        ON cb.evenement_id = e.id
    LEFT JOIN billet b
        ON b.categorie_billet_id = cb.id
    WHERE
        e.organisateur_id = 1
        AND b.achat_client_id IS NOT NULL
        AND b.date_action > '2019-09-12'
    ORDER BY b.date_action
)
SELECT 
    *, 
    @cumul := @cumul + tmp.prix AS cumul
FROM tmp;
```

Attention à bien réinitialiser le cumul à 0 entre 2 requêtes. Pour éviter d'avoir à le faire,
voici une astuce permettant de'initialiser une variable au sein de la même requête : utiliser un cross join :

```
WITH tmp AS (
    SELECT
        b.date_action,
        cb.prix
    FROM evenement e
    LEFT JOIN categorie_billet cb
        ON cb.evenement_id = e.id
    LEFT JOIN billet b
        ON b.categorie_billet_id = cb.id
    WHERE
        e.organisateur_id = 1
        AND b.achat_client_id IS NOT NULL
        AND b.date_action > '2019-09-12'
    ORDER BY b.date_action
)
SELECT 
    tmp.date_action,
    tmp.prix, 
    @cumul := @cumul + tmp.prix AS cumul
FROM tmp
JOIN (SELECT @cumul := 0) truc;
```
 