# HeneriaBedwars

**HeneriaBedwars** est un plugin de BedWars complet et moderne pour les serveurs Spigot 1.21, conçu pour offrir une expérience de jeu et d'administration exceptionnelle.

Notre objectif principal est de fournir un système de gestion d'arène via une interface graphique (GUI) simple, rapide et puissante, éliminant le besoin de commandes complexes et de modifications manuelles de fichiers de configuration.

## ✨ Fonctionnalités (v0.0.1)

- **Gestion d'Arène 100% GUI** : Créez, configurez et gérez vos arènes sans taper une seule commande de configuration.
- **Système d'Arène Flexible** : Définissez les points de spawn, les générateurs, les PNJ de boutique, et plus encore, directement en jeu.
- **Persistance des Données** : Les configurations d'arène sont sauvegardées de manière fiable dans des fichiers locaux.
- **Activation d'Arène** : Activez ou désactivez vos arènes une fois leur configuration complétée.
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

## 📘 Guide de l'Administrateur

1. **Ouvrir le menu d'administration** : `/bw admin`.
2. **Créer une nouvelle arène** : cliquez sur "Créer une Arène" et entrez un nom.
3. **Configurer l'arène** : dans la liste des arènes, sélectionnez l'arène puis utilisez les options pour :
   - Définir le lobby d'attente.
   - Configurer les équipes (spawns et lits).
   - Ajouter ou retirer des générateurs de ressources.
   - Placer les PNJ de boutique et d'améliorations.
4. **Activer l'arène** : lorsque tout est configuré, utilisez le bouton d'activation pour rendre l'arène disponible.
