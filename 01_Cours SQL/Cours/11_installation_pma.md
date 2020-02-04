# PhpMyAdmin

PhpMyAdmin est une interface web (PHP) d'administration de bases de données MySQL et MariaDB.
A la date actuelle (décembre 2019), il existe 2 versions principales :
- la 4.9 : stable mais vieillissante en terme d'interface/accessibilité
- la 5.0 : plus ergonomique mais encore au stade release candidate (Qu'est-ce qu'une release candidate ? 
➡ https://en.wikipedia.org/wiki/Software_release_life_cycle)

Nous installerons les 2 versions en parallèle.

Son fonctionnement nécessite au préalable un serveur web (Apache ou nginx par exemple), PHP
ainsi que MySQL ou MariaDB.

Nous allons donc commencer par ajouter Apache et PHP à notre server Debian 10.

## IMPORTANT
La procédure décrite sur ce document est volontairement simplifiée dans le cadre d'un déploiement sur un serveur de test.
Cette procédure ne doit en aucun cas être utilisée pour un déploiement en production ! Pour sécuriser un déploiement en
production, de nombreux paramétrages supplémentaires sont conseillés (à commencer par des mots de passe forts et un cryptage SSL).   

## Installation d'Apache 2

Via ssh :
1) Mettez à jour la liste des paquets disponibles avant toute installation : \
`sudo apt update`
2) Installez Apache 2 : \
`sudo apt install apache2`

Une fois Apache installé, celui-ci configure un port d'écoute (le n°80) pour afficher du contenu web.
Comme pour le ssh, cette VM est préconfigurée pour rediriger le port 8080 de l'hôte vers le port 80 du guest.
Ainsi, en allant à l'adresse http://localhost:8080 dans votre navigateur, c'est le port 80 de votre 
Debian 10 qui est sollicité.

Testez donc l'adresse http://localhost:8080, si tout va bien vous devriez obtenir une page d'acceuil
avec pour titre "Apache2 Debian Default Page".

Sinon... prenez un café ☕

## Installation de PHP

Via ssh :
1) Installez PHP 7 et les bibliothèques nécessaires via : \
    `sudo apt install php libapache2-mod-php php-mysql php-mbstring php-zip php-gd php-bz2 php-xml`
2) Si tout a fonctionné, la commande suivante devrait afficher la version de PHP installée :\
    `php -v`
3) La configuration par défaut de PHP impose des limites très restrictives sur l'upload de fichiers,
    ce qui pose fréquemment problème pour la fonction "Import" de PMA. Nous allons donc modifier
    les paramètres suivant dans le fichier de configuration de PHP :\
    `sudo nano /etc/php/7.3/apache2/php.ini`\
    Modifiez les valeurs suivantes :
    - `post_max_size = 256M` (8M par défaut)
    - `upload_max_filesize = 256M` (2M par défaut)
    
    Parfois la limite de RAM allouée à PHP peut aussi être limitante dans l'import de fichiers :
    - `memory_limit = 256M` (128M par défaut) 
4) Après modification de la config PHP, il faut redémarrer Apache pour la prendre en compte :\
    `sudo systemctl restart apache2`

## PhpMyAdmin 4.9
### Installation
1) Rendez-vous sur la page de téléchargement https://www.phpmyadmin.net/downloads/
2) Dans la section 4.9, faites un [clic droit > copier l'adresse du lien] sur le fichier 
finissant par "-all-languages.tar.gz"
3) Via ssh, téléchargez le fichier à l'aide de wget. Exemple avec la version 4.9.2 :\
    `wget https://files.phpmyadmin.net/phpMyAdmin/4.9.2/phpMyAdmin-4.9.2-all-languages.tar.gz`
4) Dézippez le fichier via\
    `tar xvf phpMyAdmin-4.9.2-all-languages.tar.gz` (remplacez le nom du fichier par celui téléchargé)
5) Déplacez les fichiers dézippés :\
    `sudo mv phpMyAdmin-4.9.2-all-languages/ /usr/share/phpmyadmin`

### Configuration d'Apache pour exposer PMA
https://docs.phpmyadmin.net/en/latest/config.html#
1) Créez un fichier de configuration d'Apache via \
    `sudo nano /etc/apache2/conf-available/phpmyadmin.conf`\
    Insérez y le code suivant : (**Attention !** pour coller du texte multiligne dans Cmder : utilisez SHIFT + INSERT)
    ```
    # phpMyAdmin default Apache configuration
    
    Alias /phpmyadmin /usr/share/phpmyadmin
    
    <Directory /usr/share/phpmyadmin>
        Options SymLinksIfOwnerMatch
        DirectoryIndex index.php
    
        <IfModule mod_php5.c>
            <IfModule mod_mime.c>
                AddType application/x-httpd-php .php
            </IfModule>
            <FilesMatch ".+\.php$">
                SetHandler application/x-httpd-php
            </FilesMatch>
    
            php_value include_path .
            php_admin_value upload_tmp_dir /var/lib/phpmyadmin/tmp
            php_admin_value open_basedir /usr/share/phpmyadmin/:/etc/phpmyadmin/:/var/lib/phpmyadmin/:/usr/share/php/php-gettext/:/usr/share/php/php-php-gettext/:/usr/share/javascript/:/usr/share/php/tcpdf/:/usr/share/doc/phpmyadmin/:/usr/share/php/phpseclib/
            php_admin_value mbstring.func_overload 0
        </IfModule>
        <IfModule mod_php.c>
            <IfModule mod_mime.c>
                AddType application/x-httpd-php .php
            </IfModule>
            <FilesMatch ".+\.php$">
                SetHandler application/x-httpd-php
            </FilesMatch>
    
            php_value include_path .
            php_admin_value upload_tmp_dir /var/lib/phpmyadmin/tmp
            php_admin_value open_basedir /usr/share/phpmyadmin/:/etc/phpmyadmin/:/var/lib/phpmyadmin/:/usr/share/php/php-gettext/:/usr/share/php/php-php-gettext/:/usr/share/javascript/:/usr/share/php/tcpdf/:/usr/share/doc/phpmyadmin/:/usr/share/php/phpseclib/
            php_admin_value mbstring.func_overload 0
        </IfModule>
    
    </Directory>
    
    # Authorize for setup
    <Directory /usr/share/phpmyadmin/setup>
        <IfModule mod_authz_core.c>
            <IfModule mod_authn_file.c>
                AuthType Basic
                AuthName "phpMyAdmin Setup"
                AuthUserFile /etc/phpmyadmin/htpasswd.setup
            </IfModule>
            Require valid-user
        </IfModule>
    </Directory>
    
    # Disallow web access to directories that don't need it
    <Directory /usr/share/phpmyadmin/templates>
        Require all denied
    </Directory>
    <Directory /usr/share/phpmyadmin/libraries>
        Require all denied
    </Directory>
    <Directory /usr/share/phpmyadmin/setup/lib>
        Require all denied
    </Directory>
    ```

3) Activez cette nouvelle configuration : \
    `sudo a2enconf phpmyadmin.conf`
4) Redémarrez Apache pour qu'elle soit prise en compte : \
    `sudo systemctl reload apache2`

### Configuration

1) Créez le répertoire temporaire de PMA :\
    `sudo mkdir -p /var/lib/phpmyadmin/tmp`
2) Attribuez les droits nécessaires à l'application :\
    `sudo chown -R www-data:www-data /var/lib/phpmyadmin`
3) Créez un nouveau fichier de configuration en vous basant sur le modèle fourni :\
    `sudo cp /usr/share/phpmyadmin/config.sample.inc.php /usr/share/phpmyadmin/config.inc.php`
4) Editez le fichier de configuration créé :\
    `sudo nano /usr/share/phpmyadmin/config.inc.php`
    - Ajouter une chaîne de 32 caractères au paramètre blowfish_secret :\
    ```
    $cfg['blowfish_secret'] = 'gPHxRGcqiPNIsrRX4NP8T54oM4su2tR8'; /* YOU MUST FILL IN THIS FOR COOKIE AUTH! */
    ```
   - Configurez le user pma (qui sera créé plus bas), permet d'attribuer les droits de configuration de PMA
        à d'autres users. Décommentez les lignes suivantes :
        ```
        $cfg['Servers'][$i]['controluser'] = 'pma';
        $cfg['Servers'][$i]['controlpass'] = 'pmapass';
        ```    
        https://docs.phpmyadmin.net/en/latest/config.html#cfg_Servers_controluser
    - Décommentez tout le bloc débutant par : `/* Storage database and tables */`
    - Ajouter en fin de fichier : (emplacement de notre répertoire temporaire)
    ```
    $cfg['TempDir'] = '/var/lib/phpmyadmin/tmp';
    ```
5) Créez les tables de configurations de PMA via la commande :\
`mysql -u root -p < /usr/share/phpmyadmin/sql/create_tables.sql`
6) Créez le user pma avec les droits nécessaires\
    > Attention, sur les versions récentes de MySQL (8.0+) la méthode par défaut de stockage des mots de passe a été améliorée.
    > Cependant de nombreuses applications ne gèrent pas encore cette fonctionnalité. L'usage de PHP 7.4+ corrigera le problème.
    > Pour tout user accédant à PMA il faut que le mdp soit stocké via l'ancien système (mysql_native_password)
    
    Pour créer le user pma :
    ```
    CREATE USER 'pma'@'localhost' IDENTIFIED WITH mysql_native_password BY 'pmapass';
    GRANT SELECT, INSERT, UPDATE, DELETE ON phpmyadmin.* TO 'pma'@'localhost';
    ```

    Si le user pma a déjà été créé mais avec la mauvaise méthode de stockage de mot de passe, voilà comment
    corriger :
    ```
    ALTER USER 'pma'@'localhost' IDENTIFIED WITH mysql_native_password BY 'pmapass';
    ```

### Testez
Rendez vous à l'adresse http://localhost:8080/phpmyadmin et connectez vous avec le compte pma


## PhpMyAdmin 5

L'installation de PMA 5 est très similaire. Cependant si nous souhaitons faire cohabiter les 2, il faut veiller 
à créer des répertoires et des Alias sous Apache distincts.

### Installation
1) Rendez vous sur la page de téléchargement https://www.phpmyadmin.net/downloads/
2) Dans la section 5, faites un [clic droit > copier l'adresse du lien] sur le fichier 
finissant par "-all-languages.tar.gz"
3) Via ssh, téléchargez le fichier à l'aide de wget. Exemple avec la version 5.0.0-rc1 :\
    `wget https://files.phpmyadmin.net/phpMyAdmin/5.0.0-rc1/phpMyAdmin-5.0.0-rc1-all-languages.tar.gz`
4) Dézippez le fichier via\
    `tar xvf phpMyAdmin-5.0.0-rc1-all-languages.tar.gz` (remplacez le nom du fichier par celui téléchargé)
5) Déplacez les fichiers dézippés : (notez le répertoire de destination avec l'ajout du "5")\
    `sudo mv phpMyAdmin-5.0.0-rc1-all-languages/ /usr/share/phpmyadmin5`

### Configuration d'Apache pour exposer PMA
1) Créez un fichier de configuration d'Apache via \
    `sudo nano /etc/apache2/conf-available/phpmyadmin5.conf`\
    Insérez y le code suivant : (Attention ! pour coller du texte multiligne dans Cmder : utilisez SHIFT + INSERT)
    ```
    # phpMyAdmin default Apache configuration
    
    Alias /phpmyadmin5 /usr/share/phpmyadmin5
    
    <Directory /usr/share/phpmyadmin5>
        Options SymLinksIfOwnerMatch
        DirectoryIndex index.php
    
        <IfModule mod_php5.c>
            <IfModule mod_mime.c>
                AddType application/x-httpd-php .php
            </IfModule>
            <FilesMatch ".+\.php$">
                SetHandler application/x-httpd-php
            </FilesMatch>
    
            php_value include_path .
            php_admin_value upload_tmp_dir /var/lib/phpmyadmin5/tmp
            php_admin_value open_basedir /usr/share/phpmyadmin5/:/etc/phpmyadmin5/:/var/lib/phpmyadmin5/:/usr/share/php/php-gettext/:/usr/share/php/php-php-gettext/:/usr/share/javascript/:/usr/share/php/tcpdf/:/usr/share/doc/phpmyadmin5/:/usr/share/php/phpseclib/
            php_admin_value mbstring.func_overload 0
        </IfModule>
        <IfModule mod_php.c>
            <IfModule mod_mime.c>
                AddType application/x-httpd-php .php
            </IfModule>
            <FilesMatch ".+\.php$">
                SetHandler application/x-httpd-php
            </FilesMatch>
    
            php_value include_path .
            php_admin_value upload_tmp_dir /var/lib/phpmyadmin5/tmp
            php_admin_value open_basedir /usr/share/phpmyadmin5/:/etc/phpmyadmin5/:/var/lib/phpmyadmin5/:/usr/share/php/php-gettext/:/usr/share/php/php-php-gettext/:/usr/share/javascript/:/usr/share/php/tcpdf/:/usr/share/doc/phpmyadmin5/:/usr/share/php/phpseclib/
            php_admin_value mbstring.func_overload 0
        </IfModule>
    
    </Directory>
    
    # Authorize for setup
    <Directory /usr/share/phpmyadmin5/setup>
        <IfModule mod_authz_core.c>
            <IfModule mod_authn_file.c>
                AuthType Basic
                AuthName "phpMyAdmin Setup"
                AuthUserFile /etc/phpmyadmin5/htpasswd.setup
            </IfModule>
            Require valid-user
        </IfModule>
    </Directory>
    
    # Disallow web access to directories that don't need it
    <Directory /usr/share/phpmyadmin5/templates>
        Require all denied
    </Directory>
    <Directory /usr/share/phpmyadmin5/libraries>
        Require all denied
    </Directory>
    <Directory /usr/share/phpmyadmin5/setup/lib>
        Require all denied
    </Directory>
    ```

3) Activez cette nouvelle configuration : \
    `sudo a2enconf phpmyadmin5.conf`
4) Redémarrez Apache pour qu'elle soit prise en compte : \
    `sudo systemctl reload apache2`

### Configuration

1) Créez le répertoire temporaire de PMA :\
    `sudo mkdir -p /var/lib/phpmyadmin5/tmp`
2) Attribuez les droits nécessaires à l'application :\
    `sudo chown -R www-data:www-data /var/lib/phpmyadmin5`
3) Créez un nouveau fichier de configuration en vous basant sur le modèle fourni :\
    `sudo cp /usr/share/phpmyadmin5/config.sample.inc.php /usr/share/phpmyadmin5/config.inc.php`
4) Editez le fichier de configuration créé :\
    `sudo nano /usr/share/phpmyadmin5/config.inc.php`
    - Ajouter une chaîne de 32 caractères au paramètre blowfish_secret :\
    ```
    $cfg['blowfish_secret'] = 'gPHxRGcqiPNIsrRX4NP8T54oM4su2tR8'; /* YOU MUST FILL IN THIS FOR COOKIE AUTH! */
    ```
   - Configurez le user pma (qui sera créé plus bas), permet d'attribuer les droits de configuration de PMA
           à d'autres users. Décommentez les lignes suivantes :
           ```
           $cfg['Servers'][$i]['controluser'] = 'pma';
           $cfg['Servers'][$i]['controlpass'] = 'pmapass';
           ```    
           https://docs.phpmyadmin.net/en/latest/config.html#cfg_Servers_controluser
    - Décommentez tout le bloc débutant par : `/* Storage database and tables */`
    - Ajouter en fin de fichier : (emplacement de notre répertoire temporaire)
    ```
    $cfg['TempDir'] = '/var/lib/phpmyadmin/tmp';
    ```
5) Créez les tables de configurations de PMA via la commande :\
`mysql -u root -p < /usr/share/phpmyadmin5/sql/create_tables.sql`
6) Créez le user pma avec les droits nécessaires : déjà fait dans la section PMA 4.9

### Testez
Rendez vous à l'adresse http://localhost:8080/phpmyadmin5 et connectez vous avec le compte pma

## Elements de configuration supplémentaires pour PMA

A titre informatif

### Interdire l'accès à PMA pour certains users

1) Etablissez l'ordre suivant pour les règles Allow Deny :\
    ```
    $cfg['Servers'][$i]['AllowDeny']['order'] = 'deny,allow';
   ```
    https://docs.phpmyadmin.net/en/latest/config.html#cfg_Servers_AllowDeny_order 
2) Listez les users à bloquer : \
    ```
   $cfg['Servers'][$i]['AllowDeny']['rules'] = array(
        'deny user1 from all',
        'deny user2 from all'
    );
   ```
    https://docs.phpmyadmin.net/en/latest/config.html#cfg_Servers_AllowDeny_rules
    
### Interdire l'accès à PMA via le user root
```
$cfg['Servers'][$i]['AllowRoot'] = false;
```