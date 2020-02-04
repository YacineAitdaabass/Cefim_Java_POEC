#Types de données

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

Proposition de réponse (à débattre à l'oral) :
```
CREATE TABLE `personnel` (
 `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
 `nom` varchar(50) NOT NULL,
 `prenom` varchar(50) NOT NULL,
 `date_naissance` date DEFAULT NULL,
 `age_inscription` tinyint(4) unsigned DEFAULT NULL,
 `email` varchar(50) NOT NULL,
 `telephone` char(10) DEFAULT NULL,
 `biographie` text,
 `photo` mediumblob,
 `sexe` enum('homme','femme') DEFAULT NULL,
 `date_derniere_connexion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
 `jours_presence` bit(7) NOT NULL DEFAULT b'1100000',
 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

```

Quelle longueur pour les champs nom, prénom et email ?  
Difficile à dire au premier abord.
Il existe toujours des cas rares d'identité/mail à rallonge.
On peut commencer par une valeur raisonnable et surveiller les logs applicatifs pour augmenter la taille au besoin.

Quel type pour l'âge à l'inscription ?  
L'âge en années complètes n'atteindra probablement pas la limite 128 du TINYINT (record actuel de 122 ans :
https://fr.wikipedia.org/wiki/Doyen_de_l%27humanit%C3%A9).
Comme il est certain qu'un âge négatif est impossible, on peut même ajouter UNSIGNED pour augmenter cette limite
à 256.

Quel type pour le téléphone ?  
Bien que composé de chiffres, un numéro de téléphone (sans indicateur) commence par un 0, ce n'est donc pas un nombre.
On le stockera donc sous forme textuelle. Les numéros français font tous 10 caractères, un CHAR(10) est donc optimal
dans cette situation.

Quel champ BLOB pour un fichier de 1Mo ? 1Mo = 1 024 Ko = 1 048 576 octets
- TINYBLOB = 2^8 octets = 256 octets
- BLOB = 2^16 octets = 64 Ko
- MEDIUMBLOB = 2^24 octets = 16 Mo
- LONGBLOB = 2^32 octets = 4 Go

On prendra donc un MEDIUMBLOB

Quel type pour biographie ?  
Il s'agit de texte donc un type TEXT (ou dérivé). Aucune indication n'était donnée sur le volume de la biographie
donc le choix parmi les dérivés de TEXT était libre (bien qu'un LONGTEXT autorisant + de 16 Mo de texte semble démesuré).

Quel type pour date_derniere_connexion ?  
S'agissant d'un instant de connexion, on préfèrera un type DATETIME ou TIMESTAMP. Comme nous avions vu précédemment
que seuls des numéros français seraient utilisés, on en déduit que la gestion du fuseau horaire n'est pas nécessaire
donc un DATETIME suffit. Cependant un TIMESTAMP occupe un octet de moins par champ, ce n'est donc pas un mauvais
choix non plus si le plafond de 2038 n'est pas un problème.

Quel type pour jours_presence ?  
Nous avons vu que le BIT était idéal pour cette situation. On pensera bien à définir la valeur par défaut avec un b
précédent '1100000'