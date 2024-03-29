# post_images
Serveur web d'envoi d'images

Bienvenue sur la nouvelle version de ce projet entièrement re-développé en Kotlin,
une refonte total du code et de la façon de penser le projet a eu lieu.

Dans cette nouvelle version plusieurs ajouts sont arrivée, les utilisateurs 
ont maintenant la possibilité de supprimé leurs propres images, les membres gradée du site ont
la possibilité de supprimé les images de membres avec un grade inférieur.

Une galleries d'image a été ajoutée pour voir vos propres images postées 
*(et pouvoir les supprimer au besoin)*

Grande refonte d'un point de vue de la sécurité, hashage des mots de passe des utilisateurs et
création de plusieurs méthodes d'authentification par formulaire avec une session enregistrée 
et par authentification basic pour la reception de nouvelles images par l'API 
*(tout en laissant aux utilisateurs avec une session la possibilité d'en poster sans 
passer par la route de l'API)*

Refonte globale du front affin de rester sur un thème sobre 
*(et surtout un thème dark pour éviter [ça](https://freezlex.com/flashbang))*

## Prérequis :

- Base de donnée mysql/mariadb *(les tables et colonnes seront automatiquement générées)*
- Java 16

## Installation

1. Téléchargez la dernière version du projet
2. Lancez une première fois le fichier `FPostImage.jar` avec java 
   1. (`java -jar FPostImage.jar`) 
   2. *(Le program crachera, cela est normal)*
3. Repérez le fichier de configuration qui ce sera générer à la racine du projet et complétez le
4. Répétez l'étape 2

## Migré depuis [l'ancienne version](https://github.com/OcelusPRO/post_images/tree/Archives_JS-Express)

Si vous voulez migrer depuis la version javascript de ce serveur d'envoi d'images, 
suivez les étapes d'installation décrite ci-dessus jusqu'a l'étape 3 *(inclus)*

Déplacez ensuite l'ensemble de vos images du dossier `/uploads/` vers `/uploads/no-sorted/`.
Continuez ensuite les étapes d'installations normales.

## SSL

Pour fonctionner l'application a besoin d'un certificat SSL, ce dernier est enregistré dans un KeyStore 
(par default `keystore.jks`) 
Si le fichier n'existe pas OU que la date de fin du certificat est dépassé, l'application génèrera automatiquement
un nouveau certificat (autosigné) d'une validité de 3 jours.