# post_images
Serveur web d'envoi d'images

## Installation : 
- Téléchargez le projet sur votre serveur
- Utilisez la commande `npm install`
- Copiez le fichier `config_sampl.json` vers `config.json` et éditez son contenu pour configurer le projet
- Démarez le projet avec la commande `npm start`

## Prérequit : 
- Une base de donnée avec une table `staff` contenant les colonnes : 
`id` (Nombre en autoIncrémente, clé primaire) 
`pseudo` (Varchar 255) 
`staff_id` (Varchar 255) 
`rank_name` (Text) 
`password` (Text)
`service` (Text)

Le projet a été testé avec une base de donnée mysql, si vous utilisez un autre système de base de donnée consultez la doccumentation de `knex` et changez la valeur de `sql_client` dans votre fichier `config.json`
