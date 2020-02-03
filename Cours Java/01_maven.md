# Maven
Maven est une gestionnaire de projet qui gère de nombreux éléments (dépendances, compilation, documentation, etc).
Le premier outil de ce type était nommé Ant. Maven lui a succédé. Aujourd'hui il existe également Gradle comme alternative.

## Création d'un projet Maven
Maven est préinstallé via IntelliJ. Pour en bénéficier : créez un nouveau Projet de type Maven.
Il vous sera demandé :
- GroupId : généralement une url inversée représentant votre entreprise (ex : `com.company.appname`)
- ArtefactId : le nom du projet / application (ex : `my-app`)
- Version : laissez `1.0-SNAPSHOT`, il s'agit d'une proposition qui suit une convention de releases. Il s'agit alors 
de la version 1.0 mais dans sa version avant release (=SNAPSHOT).

## Arborescence
Le projet créé, vous obtenez une arborescence avec une partie des répertoires décrits ici :  
https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html

A la racine il existe un fichier pom.xml qui renferme l'ensemble de la configuration de Maven.

## Ajouter une dépendance
Les dépendances (packages externes) peuvent être automatiquement téléchargés, importés et joints
à votre application finale par Maven.

> Pour ajouter une dépendance, rendez vous sur le site https://mvnrepository.com/ et recherchez
> `mysql-connector-java`, package qui npermet de se connecter aux bases MySQL.
> Trouvez sa dernière version et copiez les balises XML proposées (`<dependency>...`).  
> Au sein du pom.xml, après les balises <version>, ajoutez une paire de balises <dependencies></dependencies>
> et insérez y le code de votre dépendance.

## Les goals
Après modification du pom.xml rien ne se passe. Pour exécuter Maven on utilise des goals :
des ensembles d'opérations regroupées sous certains libellés.  

> Créez une classe quelconque avec un main affichant un texte console.  
> Sur la droite de l'interface d'IntelliJ, déployez l'onglet Maven,
>puis dépliez le dossier Lifecycle.

Les goals affichés sont décrits ici : https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html  
Pour l'instant nous n'avons ni tests, ni déploiement à effectuer, ni packaging...
Seul le goal compile nous intéresse.
C'est d'ailleurs lui qui va vérifier si toutes nos dépendances sont chargées
et s'occuper d'ajouter celles qui manquent.

> Exécutez le goal compile en double cliquant dessus, votre dépendance
>devrait se télécharger.
> Puis exécutez votre programme comme à l'accoutumée.

Notez que le dossier target a été alimenté. Le goal clean permet (entre autre)
de vider ce répertoire.

## Packaging
Jusqu'à présent, vos programmes compilés étaient composés de différents fichiers compilés.
Nous venons d'ajouter une dépendance qui apporte les siennes.
Lors de l'exécution de votre programme, observez la commande exécutée
par IntelliJ : celle-ci est affreusement longue.
Plus les dépendances vos s'ajouter et plus la ligne va s'allonger.
Pour résoudre le problème il est possible de packager le tout en un fichier jar.

> Dans votre pom.xml, après le bloc des dépendances, ajoutez le code suivant en
> remplaçant le nom de la classe contenant Main par le votre :
```
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-assembly-plugin</artifactId>
            <version>3.2.0</version>
            <configuration>
                <descriptorRefs>
                    <descriptorRef>jar-with-dependencies</descriptorRef>
                </descriptorRefs>
                <archive>
                    <manifest>
                        <mainClass>com.company.app.MaClasse</mainClass>
                    </manifest>
                </archive>
            </configuration>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>single</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

> Exécutez ensuite le goal package et observez le répertoire target.
> Vous pouvez maintenant exéutez votre programme à l'aide de la ligen de commande : 
>`java -jar target/ArtifactId-1.0-SNAPSHOT-jar-with-dependencies.jar`