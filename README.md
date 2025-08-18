# HeneriaBedwars

**HeneriaBedwars** est un plugin de BedWars complet et moderne pour les serveurs Spigot 1.21, conçu pour offrir une expérience de jeu et d'administration exceptionnelle.

Notre objectif principal est de fournir un système de gestion d'arène via une interface graphique (GUI) simple, rapide et puissante, éliminant le besoin de commandes complexes et de modifications manuelles de fichiers de configuration.

## ✨ Fonctionnalités (v0.5.0)
- **Gestion d'Arène 100% GUI** : Créez, configurez et gérez vos arènes sans taper une seule commande de configuration.
- **Menus de Configuration Complets** : Lobby, équipes (lits, spawns et PNJ), générateurs configurables via interface.
- **Outil de Positionnement** : Un simple bâton permet d'enregistrer les positions directement en jeu.
- **Activation d'Arène** : Activez ou désactivez une arène une fois sa configuration terminée.
- **Persistance des Données** : Les configurations d'arène sont sauvegardées de manière fiable dans des fichiers locaux.
- **Conçu pour la 1.21** : Entièrement développé sur l'API Spigot 1.21 pour une performance et une stabilité optimales.
- **Boutique d'objets fonctionnelle** : Achetez de l'équipement en dépensant vos ressources collectées.
- **Boutique d'améliorations d'équipe** : Investissez vos diamants pour débloquer des bonus permanents.

## 🚀 Roadmap

Consultez notre [ROADMAP.md](ROADMAP.md) pour suivre le développement du projet étape par étape.

## 🕹️ Gameplay

Chaque équipe possède un lit. Tant que ce lit est intact, les joueurs de l'équipe réapparaissent après quelques secondes. Si le lit est détruit, toute mort devient définitive : le joueur est éliminé et passe en mode spectateur pour la fin de la partie. Détruisez les lits adverses tout en protégeant le vôtre pour remporter la victoire. La partie se termine lorsqu'une seule équipe possède encore des joueurs en vie. L'arène annonce les vainqueurs, arrête les générateurs et se réinitialise automatiquement pour le prochain match.

## 🔧 Compilation

1. Clonez ce dépôt : `git clone https://github.com/tomashb/HeneriaBW.git`
2. Naviguez dans le dossier : `cd HeneriaBW`
3. Compilez avec Maven : `mvn clean package`
4. Le JAR final se trouvera dans le dossier `target/`.

## 🎮 Commandes & Permissions

### Commandes Administrateur
- `/bedwars admin` (Alias: `/bw admin`, `/hbw admin`)
  - Ouvre le menu principal de gestion des arènes.
  - **Permission :** `heneriabw.admin`

## 🔨 Boutique d'Améliorations d'Équipe

Le fichier `upgrades.yml` définit les différentes améliorations disponibles ainsi que leur coût en diamants.

Exemple de configuration :

```yaml
sharpness:
  name: "&aTranchant d'équipe"
  item: IRON_SWORD
  tiers:
    1:
      cost: 4
      description: "&7Toutes les épées de l'équipe obtiennent Tranchant I."

protection:
  name: "&aProtection d'équipe"
  item: IRON_CHESTPLATE
  tiers:
    1:
      cost: 2
      description: "&7Toutes les armures de l'équipe obtiennent Protection I."
    2:
      cost: 4
      description: "&7Toutes les armures de l'équipe obtiennent Protection II."
    3:
      cost: 8
      description: "&7Toutes les armures de l'équipe obtiennent Protection III."
```

Chaque niveau ne spécifie qu'un coût en diamants et une courte description. Les joueurs peuvent accéder à cette boutique en interagissant avec le PNJ d'améliorations de leur île.
