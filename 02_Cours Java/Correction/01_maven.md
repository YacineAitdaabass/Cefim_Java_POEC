# Maven

## Ajouter une dépendance
Les dépendances (packages externes) peuvent être automatiquement téléchargés, importés et joints
à votre application finale par Maven.

> Pour ajouter une dépendance, rendez vous sur le site https://mvnrepository.com/ et recherchez
> `mysql-connector-java`, package qui npermet de se connecter aux bases MySQL.
> Trouvez sa dernière version et copiez les balises XML proposées (`<dependency>...`).  
> Au sein du pom.xml, après les balises <version>, ajoutez une paire de balises <dependencies></dependencies>
> et insérez y le code de votre dépendance.

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>eu.cefim.java</groupId>
    <artifactId>CoursJava</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.19</version>
        </dependency>
    </dependencies>
</project>
```