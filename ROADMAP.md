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

* **[✔] 1.3 : Système de Commandes & Permissions**
    * [✔] Implémenter le gestionnaire de commandes pour `/bedwars`.
    * [✔] Créer la sous-commande `/bedwars admin` avec la permission `heneriabw.admin`.

* **[WIP] 1.4 : Développement du GUI d'Administration**
    * [✔] Créer le GUI Principal (`/bw admin`).
    * [✔] Créer le GUI de Création d'Arène (Wizard).
    * [ ] Créer le GUI de Configuration d'Arène.

* **[✔] 1.5 : Persistance des Données**
    * [✔] Développer un `ArenaManager` qui charge les configurations au démarrage.
    * [✔] Sauvegarder les configurations d'arène dans des fichiers YAML dédiés.
    * [✔] Assurer que les modifications via GUI sont sauvegardées.

---

## 🎯 **Étape 2 : Cycle de Jeu & Lobby (Version Cible : 0.2.0)**
*Objectif : Rendre les arènes jouables avec un cycle de vie complet, de l'attente au décompte, jusqu'à la fin de partie.*

## 🎯 **Étape 3 : Systèmes Économiques & PNJ (Version Cible : 0.3.0)**
*Objectif : Intégrer les boutiques d'objets et d'améliorations d'équipe.*

## 🎯 **Étape 4 : Polissage & Fonctionnalités Avancées (Version Cible : 1.0.0)**
*Objectif : Ajouter les fonctionnalités spéciales, optimiser le code et préparer la version stable.*
