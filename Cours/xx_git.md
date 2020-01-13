# Git
Git est un outil de versioning (VCS : Version Control System).
Au-delà de la gestion des différents versions d'une application en développement, git
permet de travailler en équipe sur un même code source.
Comment ça marche ?

## Vue globale

Le remote repository est un serveur de dépôt sur lequel l'équipe envoie les éléments
qu'elle souhaite partager. C'est le point de fusion du code des différents développeur.
C'est de là que vous récupérerez les dernières modifications effectuées par vos collègues.

De l'autre côté il y a votre environnement de développement (propre à chaque développeur).
Celui-ci comprend 3 éléments :
- Working directory : le répertoire de travail
- Staging area : liste des modifications en cours
- Local Repository : modifications regroupées en "commits"

## Remote repository

Le remote repository est donc un serveur qui centralise le développement d'une équipe.
Tout serveur peut héberger ce type de type de dépôt, mais il existe également des services
clé en main comme Github ou Bitbuckets.

Nous allons créer un dépôt distant sur Github. Pour cela connectez vous à https://github.com 
(créez vous un compte si besoin) puis créez un nouveau repo public.
Laissez les options de reamde, de licence et de gitignore vierges.

Votre dépôt distant est prêt, on va maintenant s'occuper du côté environnement de travail.

## Local repository

### Installer Git

Git est déjà compris dans l'installateur de Cmder. Néanmoins si vous ne souhaitez pas installer Cmder
vous pouvez installer git via https://git-scm.com/download/win

Vérifiez que git est installé et dans le PATH via la commande suivante (via Cmder ou autre console) :
`git --version`

Si git est installé, sa version s'affiche.

### Initialiser un dépôt local

Votre dépôt local est propre à un projet. Celui-ci doit être contenu dans un répertoire.
- Créez un répertoire de test : `mkdir test`
- Positionnez-vous dans le répertoire : `cd test`
- Initialisez un repo local dans ce répertoire : `git init`

## Effectuer un premier commit

Le cycle de fonctionnement de git est le suivant :
1) Récupérer la dernière version du projet sur le dépôt distant (actuellement le dépôt est vide donc pas utile)
2) Effectuer des modifications (ajout/suppression/modification de fichiers)
3) Déclarer les fichiers modifiés à git (qui les place en "stage")
4) Regrouper ces modifications en commits (ensembles logiques, ex : l'ajout d'une fonctionnalité)
5) Pousser ces ommits vers le repo distant

### Effectuer des modifications : créer un fichier readme.md

La page d'accueil d'un repo Github peut être personnalisée en générant un fichier readme.md (fichier
au format Markdown).
Dans votre répertoire `test`, créez un fichier readme.md et collez-y le contenu suivant :
```
# Dépôt de test

Ceci est un dépôt de test 
```

### Staging : déclarer les modifications

Pour que git analyse les modifications effectuées, il faut lui spécifier le/les fichiers
à observer, ils sont alors placés en "stage". Pour cela vous pouvez :
- préciser manuellement le fichier readme via `git add readme.md`
- demander d'ajouter tous les fichiers modifiés du projet via `git add .`

Vous pouvez ensuite afficher le contenu du stage via la commande `git status`

### Commit : regrouper les modifications

Ici notre seule modification dans le stage est la création d'un fichier.
On va donc déclarer notre commit avec le message approprié (**il est impératif de mettre un message dans un commit !**)

`git commit -m "création du fichier readme.md"`

### Push : envoyer les commits vers le dépôt distant

Notre dépôt local ne connait pas encore l'emplacement du dépôt distant créé précédemment.
Il ne sait pas non plus comment nous authentifier sur ce dépôt.

#### Déclarer notre repo distant

Pour déclarer localement notre dépôt distant il nous faut son adresse .git  
Celle-ci est disponible sur la page d'accueil du dépôt, après clic sur le bouton [Clone or download]

Copiez l'adresse et exécutez la commande suivante localement :  
```git add remote origin "adresse ici"```  
La commande `add remote` ajoute un repo distant et le nomme `origin` (nom traditionnellement donné
au repo distant principal).  
Vous pouvez à tout moment lister le/les dépôts distants du projet via :
`git remote -v`  
En cas d'erreur lors de l'ajout du dépôt distant, vous pouvez le supprimer via `git remove remote origin`
puis le recréer via un `git add remote`

Exécutez ensuite votre push via `git push origin master`  
La commande déclare que le repo distant sur lequel poussé est celui précédemment déclaré sous le nom `origin`.  
`master` se réfère à la branche sur laquelle on pousse nos données. Par défaut il n'y a qu'une branche principale
nommée `master`.

Cependant la requête précédente ne marche pas, plusieurs éléments sont d'abord à corriger.

#### Déclarer son identité

Git nécessite de connaitre quelques informations pour signer vos commits.
Renseignez votre nom, votre username (le même que sur github) et mail (le même que sur github) via les
3 commandes suivantes :
```
git config --global user.name "Prénom Nom"
git config --global user.email "email.utilisé@sur.github"
git config --global user.username "UsernameSurGithub"
``` 
  
#### Se connecter via une clé ssh

Github sécurise par défaut ses connexions à l'aide de clés rsa. Ces clés fonctionnent par paire :
- une privée : à ne jamais divulguer (reste sur votre poste)
- une publique : à transmettre à Github

Vos données de connexion seront cryptée via votre clé privée, et Github les décryptera via
la clé publique que nous allons lui fournir. De cette façon, un hacker en possession de votre
clé privée pourrait éventuellement intercepté vos connexions, mais il ne sera jamais en capacité 
d'envoyer des données à Github sans votre clé privée.

Générez une paire de clés via la commande suivante (via Cmder sous Windows) :  
`ssh-keygen -t rsa -b 4096 -C "your_email@example.com"`  
Il vous est demandé de confirmé le nom du fichier généré (libre à vous de le changer mais
laissez le absolument dans le répertoire .ssh proposé, sans quoi votre clé ne fonctionnera pas)  
Une passphrase vous est ensuite demandée (elle est optionnelle), cela permet de rajouter un mot de passe
protégeant l'usage de votre clé privée.

Une fois la procédure terminée, 2 fichiers ont été générés dans le répertoire .ssh de votre user système :
- id_rsa : votre clé privée (à ne pas divulguer)
- id_ras.pub : votre clé publique

Ouvrez votre fichier de clé publique avec un éditeur de texte et copiez en la totatlité du contenu.

Rendez-vous dans l'onglet Settings de votre repo distant sur Github, puis allez dans Deploy keys >
Add deploy key :
- Donnez un nom à votre clé (ce nom doit vous identifier vous et votre poste, ex : Prénom@HP-Probook)
- Collez le contenu de votre clé dans le champ Key
- Cochez Allow write access
- Cliquez sur Add key

#### Effectuez votre push

Tentez à nouveau d'effectuer un push via : `git push origin master`

Si un popup s'ouvre : sélctionnez "Manager" et cochez ne plus demander.

Si votre push a fonctionné, vous devriez retrouver votre fichier sur la page Github de votre repo,
et vous devriez voir la page d'acceuil modifiée en fonction car nous avons poussé un readme.md

Pour les push ultérieurs, un `git push` ser asuffisant. Le repo et la branche cibles sont mémorisés.
Vous pouvez les changer à tout moment en les reprécisant dans un push.