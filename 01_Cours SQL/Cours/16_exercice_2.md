# Exercice 2

Votre client est un Centre Hospitalier Universitaire (CHU).
Cet établissement possède des multiples applications métiers distinctes,
lesquelles ont toutes leur propre SGBD, et leur schémas sont complexes.

Pour faciliter l'exploitation des données médicales dans divers cas d'usages,
le CHU souhaite centraliser toutes ses données dans une seule et même base : c'est ce qu'on 
appelle un entrepôt de données (Data Warehouse ou DW).

Cet entrepôt de données devra permettre de stocker les divers documents produits par les applications
et de les rattacher aux patients, séjours, mouvements, etc.
 
Un patient est identifié par un code unique nommé IPP (exemple de valeur : 202015648512),
il possède un nom, un prénom, une date de naissance et un sexe.

A chaque venue (hospitalisation) du patient dans l'établissement, 
on considère qu'il effectue une séjour. Celui-ci est identifié par un code unique nommé IEP
(exemple de valeur : 516555546). Ce séjour est borné dans le temps (date d'entrée et date de sortie).

Au sein d'un même séjour, le patient passe par un ou différents services. Chaque période passée
dans un service est appelé mouvement, qui est également borné dans le temps.

On souhaite pouvoir stocker 4 types de documents dans cet entrepôt.
Ces documents ont :
- tous un format d'origine (ex : PDF, HTML)
- un texte brut extrait du document d'origine par un outil de conversion (ex : PDF to TXT)
- des éventuelles métadonnées structurées
- une date de production du document

Les documents peuvent être rattachés
- à un mouvement (ex : la cardiologie a produit un compte rendu)
- ou à un séjour (ex : facture pour le patient résumant le coût complet du séjour)
- ou globalement à un patient (ex : données administratives, telle que l'adresse, qui ne dépend pas d'un séjour ou d'un mouvement)

Voici les 4 types de documents :
- comptes rendus textuels : il s'agit de documents textuels (format varié), sans métadonnée
- résultats biologiques réalisés dans un service : un document PDF accompagné de N résultats structurés.
Ces résultats sont composés d'un type d'analyse (ex : calcémie), d'une résultat quantitaif (ex : 2.6) et d'une unité (ex : mmol/L).
Une analyse (ex : calcémie) possède des normes (bornes qui définissent les valeurs normales, ex : entre 2.2 et 3.0),
celles-ci sont stables dans le temps 
- diagnostics réalisés pendant un séjour ou un mouvement : un document HTML avec la liste structurée des diagnostics.
Ces diagnostics sont composés d'un code (ex : D52) et d'un libellé propre à ce code
- actes réalisés pendant un séjour ou un mouvement : un document HTML avec la liste structurée des actes.
Ces actes sont composés d'un code (ex : 1234HDF1554) et d'un libellé propre à ce code

On souhaite pouvoir réaliser (entre autre) les requêtes suivantes :
- lister les patients ayant un résultat biologique anormal (exemple : caclémie anormalement haute)
- lister les patients ayant la suite de mots suivante dans un compte rendu : "hypertension artérielle"

Bonus : les médecins qui rédigent les comptes rendus ont pour habitude de lister l'absence de diagnostics.
Ex : "... Mme X n'a pas d'hypertension artérielle ...". Aussi notre requête permettant de rechercher 
l'hypertension artérielle va sélectionner tous les patients ayant ce type de phrase, à tord !
Le CHU a développé un algorithme capable d'extraire les phrases négatives des comptes rendus ("n'a pas", "ne présente pas", etc).
Imaginez une solution permettant d'ignorer les phrases négatives lors de vos recherches textuelles
en stockant ces phrases négatives et en les exploitant. 
