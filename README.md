# HeneriaBedwars

**HeneriaBedwars** est un plugin de BedWars complet et moderne pour les serveurs Spigot 1.21, conçu pour offrir une expérience de jeu et d'administration exceptionnelle.

Notre objectif principal est de fournir un système de gestion d'arène via une interface graphique (GUI) simple, rapide et puissante, éliminant le besoin de commandes complexes et de modifications manuelles de fichiers de configuration.

## ✨ Fonctionnalités (v0.0.1)

- **Gestion d'Arène 100% GUI** : Créez, configurez et gérez vos arènes sans taper une seule commande de configuration.
- **Menus de Configuration Complets** : Lobby, équipes, lits, spawns, générateurs et PNJ configurables via interface.
- **Outil de Positionnement** : Un simple bâton permet d'enregistrer les positions directement en jeu.
- **Activation d'Arène** : Activez ou désactivez une arène une fois sa configuration terminée.
- **Persistance des Données** : Les configurations d'arène sont sauvegardées de manière fiable dans des fichiers locaux.
- **Conçu pour la 1.21** : Entièrement développé sur l'API Spigot 1.21 pour une performance et une stabilité optimales.

## 🚀 Roadmap

Consultez notre [ROADMAP.md](ROADMAP.md) pour suivre le développement du projet étape par étape.

## 🔧 Compilation

1.  Clonez ce dépôt : `git clone https://github.com/tomashb/HeneriaBW.git`
2.  Naviguez dans le dossier : `cd HeneriaBW`
3.  Compilez avec Maven : `mvn clean package`
4.  Le JAR final se trouvera dans le dossier `target/`.

## 🎮 Commandes & Permissions

### Commandes Administrateur
- `/bedwars admin` (Alias: `/bw admin`, `/hbw admin`)
  - Ouvre le menu principal de gestion des arènes.
  - **Permission :** `heneriabw.admin`

## 🛠️ Guide de l'Administrateur

1. **Ouvrir le panneau d'administration** : `/bw admin`.
2. **Créer une arène** : cliquez sur "Créer une Arène" et entrez un nom.
3. **Configurer l'arène** : dans "Gérer les Arènes Existantes", sélectionnez l'arène puis utilisez le menu pour définir le lobby, les équipes, les générateurs et les PNJ via l'outil de positionnement.
4. **Activer l'arène** : lorsque tous les points essentiels sont définis, utilisez le bouton d'activation pour rendre l'arène jouable.
