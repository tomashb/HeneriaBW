# 🗺️ Roadmap du Projet - HeneriaBedwars

Ce document détaille les étapes de développement prévues pour le plugin HeneriaBedwars.

---

## 🎯 **Étape 1 : Fondations et Gestionnaire d'Arène via GUI (Version Cible : 0.1.0)**
*Objectif : Mettre en place un socle technique solide et un système complet de création/gestion d'arènes via une interface graphique intuitive. À la fin de cette étape, un administrateur doit pouvoir configurer une arène de A à Z sans utiliser de commandes complexes.* 

* **[✔] 1.1 : Structure du Projet & Intégration Continue**
    * [✔] Mise en place du projet Maven pour Spigot 1.21.
    * [✔] Configuration du `plugin.yml` avec les informations de base.
    * [✔] Création du workflow GitHub Actions pour la compilation automatique (CI).
    * [✔] Initialisation de la structure des packages Java (`com.heneria.bedwars.*`).

* **[✔] 1.2 : Modèles de Données (Core Engine)**
    * [✔] Créer la classe `Arena`.
    * [✔] Créer la classe `Team`.
    * [✔] Créer la classe `Generator`.
    * [✔] Créer des Enums pour les états de jeu, types de générateurs, et couleurs d'équipe.

* **[ ] 1.3 : Système de Commandes & Permissions**
    * [ ] Implémenter un gestionnaire de commandes robuste pour la commande principale `/bedwars` (ou `/hbw`).
    * [ ] Créer la sous-commande `/bedwars admin` (permission : `heneriabw.admin`).
    * [ ] L'exécution de `/bedwars admin` ouvrira l'interface graphique principale de gestion.

* **[ ] 1.4 : Développement du GUI d'Administration**
    * [ ] **GUI Principal (`/bw admin`)** : Proposera les options "Créer une Arène" et "Gérer les Arènes Existantes".
    * [ ] **GUI de Création d'Arène (Wizard)** :
        * Étape 1 : Demander le nom de l'arène via un anvil GUI.
        * Étape 2 : Définir le nombre de joueurs min/max et le nombre d'équipes.
        * Étape 3 : Confirmer la création, ce qui génère un fichier de configuration vide et ajoute l'arène au gestionnaire.
    * [ ] **GUI de Configuration d'Arène (Menu par arène)** :
        * Item "Définir le Lobby d'attente" : Enregistre la position du joueur comme point de spawn d'attente.
        * Item "Gestion des Équipes" : Ouvre un sous-menu pour configurer chaque équipe (couleur, taille max, position du lit, point de spawn).
        * Item "Gestion des Générateurs" : Ouvre un sous-menu pour ajouter/supprimer des générateurs. L'ajout se fera via un item à placer physiquement, qui ouvrira un GUI pour choisir le type de ressource.
        * Item "Gestion des PNJ" : Permet de définir les emplacements pour le PNJ de la boutique et celui des améliorations.
        * Item "Activer/Désactiver l'arène" : Permet de rendre une arène jouable ou non.

* **[ ] 1.5 : Persistance des Données**
    * [ ] Développer un `ArenaManager` qui charge toutes les configurations d'arène au démarrage du serveur.
    * [ ] Les configurations d'arène seront stockées dans des fichiers YAML dédiés (`plugins/HeneriaBedwars/arenas/<nom_arene>.yml`).
    * [ ] Toute modification via le GUI doit être immédiatement sauvegardée dans le fichier correspondant pour éviter toute perte de données.

---

## 🎯 **Étape 2 : Cycle de Jeu & Lobby (Version Cible : 0.2.0)**
*Objectif : Rendre les arènes jouables avec un cycle de vie complet, de l'attente au décompte, jusqu'à la fin de partie.*

## 🎯 **Étape 3 : Systèmes Économiques & PNJ (Version Cible : 0.3.0)**
*Objectif : Intégrer les boutiques d'objets et d'améliorations d'équipe.*

## 🎯 **Étape 4 : Polissage & Fonctionnalités Avancées (Version Cible : 1.0.0)**
*Objectif : Ajouter les fonctionnalités spéciales, optimiser le code et préparer la version stable.*
