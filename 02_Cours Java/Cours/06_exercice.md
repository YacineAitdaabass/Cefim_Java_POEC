# Exercice
L'objectif de l'exercice est de mettre en application différentes compétences
au sein d'un même projet :
- manipulation sql en java
- développement application graphique avec Swing
- respect des bonnes pratiques en termes de multithreading
- application du pattern MVC
- usage de Maven (dépendances, build et arborescence du projet)
- usage de Git pour un travail collaboratif

En partant de la base de données de l'application EventCode, vous allez,
apr équipe de 3, développer une partie de l'interface graphique nécessaire
à l'usage et l'administration de l'application.

Il ne sera pas possible de développer toute l'application. Concentrez-vous
sur le respect des bonnes pratiques telles que vues précédemment plutôt 
que sur la quantité de fonctionnalités.

Une fois qu'une étape est atteinte, nous validerons ensemble le code rédigé,
puis vous pourrez passer à l'étape suivante.
L'ensemble des étapes est à rédiger au sein de la même application.

## 1e étape
Développement d'un accès organisateur.  
User stories :
- en tant qu'organisateur, je souhaite pouvoir me connecter
avec mon email et mon mot de passe
- en cas de succès, l'application doit afficher mes données (id, mail,
type de compte, éventuel mail du parrain)
- en cas d'erreur, un message doit m'indiquer que mes identifiants
sont incorrects et me laisser réessayer

Indice : vous aurez besoin d'un composant JPassword

## 2 étape
Toujours en tant qu'organisateur :
- une fois connecté, une liste de mes événements doit apparaitre sous forme de 
tableau avec les informations suivantes : nom, date et heure, nb billets max,
nb billets vendus, nb billets en panier
- seuls le nom de l'événement et la date et l'heure doivent être modifiables
- les informations de l'étape précédente (id, mail, etc de l'organisateur)
doivent être visibles sur cette même fenêtre au dessus du tableau

Il va de soit que les modifications faites sur les événements doivent être persistées en base.

## 3e étape
Toujours en tant qu'organisateur
- pour chaque événement dans le tableau, l'organisateur doit pouvoir accéder
aux catégories de billets. Imaginez une solution (double clic sur la ligne de l'événement,
bouton sur la ligne... au choix)
- la liste des catégories doit apparaitre dans une fenêtre supplémentaire (popup)
- dans cette fenêtre, la liste des catégories doit être affichée (méthode libre)
et on doit pouvoir :
    - modifier le nom de la catégorie
    - modifier le prix de la catégorie
    - modifier le nombre de places de la catégorie
    - ajouter une catégorie
    
Si l'organisateur change le nombre de places, il ne doit pas pouvoir
descendre ce nombre en dessous de la somme des billets achetés et des billets en panier
pour ce même événement.

Il n'est pas utile de permettre la suppression des catégories car une contrainte
de clé bloquerait de toute façon le DELETE (toutes les catégories
de la base d'exemple possèdent déjà au moins un billet acheté/en panier).

## 4e étape : peut être faite en parallèle à tout moment
En tant qu'administrateur
- je peux renseigner l'id d'un billet dans un champ
- le QR code associé est alors affiché

Le QR code doit refléter la valeur du champ qrcode en base.
Vous êtes libre de choisir la dépendance de votre choix permettant la génération
de QR code. Importez là via Maven.
