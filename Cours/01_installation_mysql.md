# Installation de MySQL Server/MariaDB
MySQL Server/MariaDB peut être installé sur la plupart des systèmes d'exploitation.
Néanmoins, dans le cadre d'applications en production, celui-ci est généralement
installé sur un serveur Linux. Nous allons donc reproduire cet environnement
et installer MariaDB sur la distribution Linux Debian 10.

L'objectif de ce cours n'étant pas la configuration d'un environnement Linux, nous
allons nous contenter de charger un environnement virtuel prêt à l'usage.

## IMPORTANT
La procédure décrite sur ce document est volontairement simplifiée dans le cadre d'un déploiement sur un serveur de test.
Cette procédure ne doit en aucun cas être utilisée pour un déploiement en production ! Pour sécuriser un déploiement en
production, de nombreux paramétrages supplémentaires sont conseillés (à commencer par des mots de passe forts).

## Installation de Oracle VM VirtualBox

Pour utiliser un autre système d'exploitation, le plus simple est de le virtualiser au
travers d'une application comme VirtualBox.

1) Téléchargement de l'installateur : https://www.virtualbox.org/wiki/Downloads  
(Sélectionnez Windows hosts)
2) Exécutez l'installateur
3) Démarrez VirtualBox

## Installation de Debian 10

Traditionnellement, il faut créer une nouvelle machine virtuelle sous VirtualBox,
charger une image du CD d'installation de Debian, exécuter tout le process d'installation... etc. 
Pour gagner du temps nous allons utiliser une machine virtuelle pré-configurée pour le cours :

1) Téléchargez la machine virtuelle à l'adresse : https://github.com/JeremyPasco/Cefim_SQL/blob/master/Cefim_SQL_init.ova  
Il s'agit d'un serveur Debian 10 avec un minimum d'outils et de configuration (clavier AZERTY, serveur ssh,
configuration réseau, grub...). L'interface graphique a été désactivée comme pour un serveur classique.
2) Dans VirtualBox faites Fichier > Importer un appareil virtuel
3) Sélectionnez le fichier téléchargé CEFIM_SQL_init.ova
4) Terminez l'importation en laissant les paramètres par défaut
5) Sélectionnez la nouvelle VM importée et cliquez sur [Démarrer]

## Se connecter en ssh

On accède rarement à la console d'un serveur en se connectant physiquement sur la machine
(nécessiterait d'être dans le data center de l'entreprise). A la place on accède à la console au travers d'une connexion
via le protocole ssh.

Pour cela il nous faut un client SSH. Pas de chance, Windows gère très mal le SSH.
Il existe bien un outil nommé PuTTY mais qui est très peu pratique.
Heureusement, il y a l'outil cmder qui simplifie tout ça.
Il s'agit d'une console embarquant de nombreux outils (tel que ssh), nous l'utiliserons pour plein d'autres choses.

1) Téléchargez Cmder (version full) : https://cmder.net/
2) Exécutez l'installateur
3) Lancez Cmder

Pour se connecter en ssh, on demande à Cmder :
- de se connecter à une machine (définie par son IP)
- via un port d'écoute (le n°22 par défaut pour le ssh)
- et un protocole (ssh)
- avec un login (cefim)
- et un mot de passe (cefim)

Notre VM Debian est bien configurée pour écouter le port 22.
Mais elle n'a pas vraiment d'IP... car elle est virtuelle et n'existe qu'au sein de notre machine.
On a donc recours à une astuce : le port forwarding.
Notre VM a été configurée pour créer un port d'écoute 2222 sur l'Host (Windows), lequel transmet tout ce qui lui
parvient au port 22 du Guest (Debian).
Ainsi en se connectant à notre propre machine Windows au port 2222, c'est en fait 
notre Debian et son port 22 qui sera au bout du fil.

4) Connectez-vous en exécutant la commande `ssh cefim@localhost -p 2222`
5) A la première connexion on vous demande de confirmer l'identité du serveur (répondez yes)
6) Puis on vous demande de renseigner le mot de passe pour le user cefim : cefim
7) Bravo, vous êtes connecté !

**PS** : n'oubliez pas que pour vous connecter en ssh au serveur, celui-ci doit déjà être démarré via VirtualBox

**Remarque** : nous aurions pu utiliser directement la fenêtre console de VirtualBox, mais celle-ci pose de 
nombreuses limitations, à commencer par l'absence de la fonction copier-coller.
 
## Installation de MySQL Server/MariaDB
Pour installer des packages sous Debian on utilise l'utilitaire apt (diminutif de "aptitude").
L'installation de package nécessite des droit admin, que l'on obtient en commençant nos commandes par `sudo` 
(diminutif de "superuser do"). Le user cefim est dans le groupe sudo, ce qui l'autorise à utiliser `sudo`.

1) Avant toute installation on rafraichit la liste des paquets disponibles : `sudo apt update`  
Le premier usage de `sudo` nécessite de retaper le mot de passe du user cefim

2) Installez le paquet gnupg (nécessaire pour l'installation de MySQL) : `sudo apt install gnupg`
(confirmez en appuyant sur Enter si besoin)
3) Rendez vous sur la page de téléchargement de MySQL : https://dev.mysql.com/downloads/repo/apt/
4) Cliquez sur le bouton [Download]
5) Sur la page suivante, faites un clic droit sur le lien [No thanks, just start my download] > Copier l'adresse du lien
6) Téléchargez le fichier depuis le serveur Debian à l'aide de la commande `wget`  
Exemple avec le lien disponible de 10/12/2019 : `wget https://dev.mysql.com/get/mysql-apt-config_0.8.14-1_all.deb`
7) Installez le fichier à l'aide de la commande `sudo dpkg -i mysql-apt-config*`
8) Lorsque le menu apparait, sélectionnez [OK] en laissant les options par défaut.
Cette étape a eu pour effet d'ajouter le repo de MySQL (serveur où figurent les installateurs). Il reste maintenant à :
9) Rafraîchir à nouveau la liste des paquets disponibles (pour voir ceux de ce nouveau repo) : `sudo apt update`
10) Et installer MySQL : `sudo apt install mysql-server`
(confirmez en appuyant sur Enter si besoin)
11) Lorsque le mot de passe du compte roo vous est demandé, renseignez : cefim (et confirmez sur l'écran suivant)  
(choisissez toujours un mot de passe fort pour un serveur de production)
12) Confirmez l'usage de cryptage fort à l'écran suivant

13) Pour sécuriser l'installation exécutez la commande `sudo mysql_secure_installation`  
Voici les réponses à fournir aux différentes questions :
- mot de passe root : renseignez 'cefim'
- VALIDATE PASSWORD COMPONENT : mettez N (car on veut exceptionnellement autoriser les mots de passe faible lors de nos exercice)
- Change the password for root : mettez N (car déjà changé pour la valeur : cefim)
- Remove anonymous users : Y
- Disallow root login remotly : Y (c'est une bonne pratique que de désactiver ce type d'accès)
- Remove test database and access to it : Y (nous ne les utiliserons pas)
- Reload privilege tables now : Y (nécessaire pour prendre en compte les modifications choisies)


> Note : pour installer MariaDB (Utilisez MySQL pour ce cours !) voici les étapes à suivre (remplacer les étapes précédentes 2 à 12) :
> 2) On installe MariaDB : `sudo apt install mariadb-server`  
> Cette commande installe la dernière version stable de MariaDB.  
> Pour installer une version spécifique : https://mariadb.com/kb/en/library/installing-mariadb-deb-files/
> 3) On vous demandera de confirmer l'installation en appuyant sur Enter
> 4) Pour sécuriser l'installation exécutez la commande `sudo mysql_secure_installation`  
 Plusieurs propositions d'amélioration de la sécurité vous seront proposées avec un texte décrivant les risques.  
 Acceptez toutes les propositions. Lorsqu'il vous est proposé de changer le mot de passe du compte root, dites oui
 et remplacez le par `cefim`

**Important** :  
Les users créés sous MySQL/MariaDB sont distincts des users du système d'exploitation.  
Ainsi le compte root Debian est sans rapport avec le compte root de MySQL/MariaDB.  
Pour des raisons de sécurité, le compte root Debian ne doit pas être utilisé (sauf administration de la VM),
on préfère attribuer les droits sudo à un autre user (ici cefim) qui exécutera les commandes précédées de sudo.  
De la même façon en SQL, on préfèrera créer un user par application/rôle avec le minimum de droits nécessaires 
et garder le compte root pour les opérations d'administration : aucune application ne devrait accéder à une base SQL
avec un user root ! 

## Gérer le service MySQL
Comme tout service sous Linux, on peut accéder au statut de MySQL et le démarrer/arrêter via la commande `systemctl`  
Remarque : le nom de la commande varie selon la distribution Linux.  
Remarque : sur certaines distribution le service est nommé mysql même s'il s'agit de MariaDB 😯

Statut : `sudo systemctl status mysql` : doit afficher 
- la version : 8.0
- le statut : active (running)
- la RAM allouée (ligne Memory)
- les derniers logs
- etc

Opérations :
- stop : `sudo systemctl stop mysql` (vérifiez ensuite avec status)
- start : `sudo systemctl start mysql` (vérifiez ensuite avec status)
- restart : `sudo systemctl restart mysql`

## Connexion au shell SQL
Pour se connecter au CLI (Command Line Interface) de MySQL on utilise la commande `mysql` (là encore pas de différence MariaDB/MySQL)
Par défaut le login utilisé pour le CLI est le même que le user Linux. Mais nous n'avons aucun user "cefim" créé dans MySQL.  
Ainsi la commande `mysql` renvoie `ERROR 1698 (28000): Access denied for user 'cefim'@'localhost'`

Seul le user root existe actuellement dans MySQL.  
Vous pouvez vous connectez via la commande `mysql -u root -p` et en renseignant ensuite le mot de passe

> Sous MariaDB, le compte root est un peu particulier, pour l'utiliser il faut faire : `sudo mysql`  
  Aucun mot de passe n'est demandé car MariaDB considère qu'un user ayant les droits sudo possède déjà tout pouvoir.  
  Ce mode de connexion n'est valable que pour le compte root.  

Une fois connecté, vous obtenez une console différente, préfixée par "mysql...".  
A tout moment pour quitter la console faites CTRL+C