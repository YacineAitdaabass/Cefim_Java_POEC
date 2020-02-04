# Base de données
## Création d'une base de données [CREATE DATABASE]
> Consigne : créez une base de données nommée `cefim` avec l'encodage et la collation cités
> ci-dessus
```
CREATE DATABASE cefim
    CHARACTER SET 'utf8mb4'
    COLLATE 'utf8mb4_0900_ai_ci'
;
```

## Modification une base de données [ALTER DATABASE]
> Consigne : modifier la base de données précédente et attribuez lui la collation latin1_german1_ci
```
ALTER DATABASE cefim
    CHARACTER SET 'latin1'
    COLLATE 'latin1_german1_ci'
;
```

## Supression d'une base de données [DROP DATABASE]
> Consigne : supprimez la base de données nommée `cefim`
```
 DROP DATABASE cefim;
```