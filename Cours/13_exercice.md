# Exercice

Nous souhaitons concevoir une application de billetterie.  
L'objectif de l'exercice est de concevoir la base de données répondant au descriptif suivant.

L'application EventCode doit permettre la vente de billets en ligne pour des événements  
et la vérification de l'authenticité des billets sur site le jour de l'événement.  
Il s'agit d'une plateforme web.
Si vous souhaitez bénéficier de l'application, vous devez créer un compte EventCode en ligne et ajoutez des événements.
Les clients peuvent alors se rendre sur la page de votre événement et acheter des billets au tarif paramétré (ou gratuit).
Une fois un billet acheté, l'acheteur peut l'imprimer sous forme d'un QRCode.  
Le jour de l'événement, une application smartphone permet de scanner les billets et affiche si celui-ci est valide.

Détails :

- Comptes EventCode
    - identifiés par un mail et un mot de passe
    - peuvent être Standard, Premium ou Platinium
    - stockent la date de dernière connexion
    - peuvent parrainer plusieurs autres comptes
- Evènements
    - sont rattachés à un compte EventCode
    - sont définis par un nom et une date
    - un évènement peut avoir plusieurs catégories de billets
    - pour chaque catégorie il y a un nombre maximum de places et un tarif (peut être gratuit)
- Clients
    - sont identifiés par un mail et un mot de passe
    - peuvent sauvegarder leur panier pour le modifier ultérieurement
    - peuvent placer plusieurs billets (de différentes catégories et/ou évènements) dans leur panier
    - une fois des billets achetés, peuvent les réimprimer à volonté
- Billets
    - conservent une trace de la date d'achat
    - on souhaite stocker le fait qu'un billet ait été utilisé (scanné) ou non 
    - on souhaite également stocker le nombre de tentatives de passage d'un billet (seul 1 passage est autorisé,
    au-delà il peut s'agir d'une tentative de fraude)
    
Besoins spécifiques : pour chacun de ces besoins, adaptez le design de votre base afin de permettre
les requêtes nécessaires pour :
- à l'affichage de la page évènement, on souhaite afficher le nombre de billets restants pour chaque catégorie
- pour traquer les fraudeurs, il est souhaitable de pouvoir lister les clients dont au moins 1 billet a été 
soumis plus d'une fois, en classant ces clients par ordre de dernière fraude décroissant.
Note : cette fonctionnalité n'est offerte que pour les évènements des comptes Premium
et Platinium
- lorsqu'un billet est placé dans un panier, il faut considérer que la place est prise.
Cependant si au bout d'un certain temps (fixé via un paramètre dans l'application) 
le billet n'est pas acheté, celui-ci sera remis en vente. Il faut donc être capable
de lister les billets dans un panier depuis X heures (= non achetés)

Réflexions en cours dans les couloirs :
- Et si on créait des évènements où les billets peuvent servir plusieurs fois ?
genre t'en achète un à ton nom mais tu peux faire passer 2 potes avec toi ?



> Requêtes à rédiger :
> - pour les 50 achats de billets les plus récents, afficher le mail des clients les ayant acheté
>et la date d'achat
>- afficher le nombre d'événements pour chaque organisation en une seule requête 
>- classez les organisateurs par nombre de billets vendus décroissant
>- pour chaque événement, afficher 3 colonnes : le nombre de billets encore disponibles à la vente, le nombre de billets
>en panier, le nombre de billets achetés et le nombre total de billets  
>- affichez les organisateurs ayant entre 1000 et 2000 tickets encore en panier. Faites-le sans utiliser de subqueries
>- qu'est ce que cela implique pour l'application ?
>- rédigez une requête (sans l'exécuter !) qui puisse corriger ce problème
>- pour chaque organisateur, affichez les 3 clients (mail) ayant dépensé le plus d'argent pour leurs événements respectifs
>- afficher le nombre de filleuls pour chaque compte organisateur. Attention, si A parraine B et que B parraine C, il faut compter 2 filleuls pour A.  
>- victime de son succès, eventcode voit sa base atteindre des volumes important et les requêtes sont de plus en plus longues à être exécutées.
>La table billet approche les 100 millions de lignes, c'est elle qui pose le plus de problèmes. 
>On note que les requêtes impliquant cette table ne concerne généralement qu'un seul organisateur à la fois.
>Que proposez vous pour améliorer la latence des requêtes sur cette table ? Quelles en seront les conséquences ?