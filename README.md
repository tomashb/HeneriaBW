# HeneriaBedwars

**HeneriaBedwars** est un plugin de BedWars complet et moderne pour les serveurs Spigot 1.21, conçu pour offrir une expérience de jeu et d'administration exceptionnelle.

Notre objectif principal est de fournir un système de gestion d'arène via une interface graphique (GUI) simple, rapide et puissante, éliminant le besoin de commandes complexes et de modifications manuelles de fichiers de configuration.

## ✨ Fonctionnalités (v0.1.2)

- **Gestion d'Arène 100% GUI** : Créez, configurez et gérez vos arènes sans taper une seule commande de configuration.
- **Menus de Configuration Complets** : Lobby, équipes (lits, spawns et PNJ), générateurs configurables via interface.
- **Outil de Positionnement** : Un simple bâton permet d'enregistrer les positions directement en jeu.
- **Activation d'Arène** : Activez ou désactivez une arène une fois sa configuration terminée.
- **Persistance des Données** : Les configurations d'arène sont sauvegardées de manière fiable dans des fichiers locaux.
- **Conçu pour la 1.21** : Entièrement développé sur l'API Spigot 1.21 pour une performance et une stabilité optimales.

## 🚀 Roadmap

Consultez notre [ROADMAP.md](ROADMAP.md) pour suivre le développement du projet étape par étape.

## 🕹️ Gameplay

Chaque équipe possède un lit. Tant que ce lit est intact, les joueurs de l'équipe réapparaissent après quelques secondes.
Si le lit est détruit, toute mort devient définitive : le joueur est éliminé et passe en mode spectateur pour la fin de la partie.
Détruisez les lits adverses tout en protégeant le vôtre pour remporter la victoire.
La partie se termine lorsqu'une seule équipe possède encore des joueurs en vie. L'arène annonce les vainqueurs, arrête les générateurs et se réinitialise automatiquement pour le prochain match.

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

### Commandes pour les Joueurs
- `/bw join <arène>`
  - Rejoindre une arène disponible.
  - **Permission :** `heneriabw.player.join`
- `/bw leave`
  - Quitter l'arène actuelle.

## 🛠️ Guide de l'Administrateur

1. **Ouvrir le panneau d'administration** : `/bw admin`.
2. **Créer une arène** : cliquez sur "Créer une Arène" et validez le nom, le nombre d'équipes et de joueurs.
3. **Gérer les arènes existantes** : ouvrez "Gérer les Arènes Existantes" puis sélectionnez l'arène à configurer.
4. **Définir les positions** dans le menu de configuration :
   - *Définir le Lobby* : donne un bâton de configuration, faites un clic droit à l'endroit souhaité.
   - *Gérer les Équipes* : choisissez une couleur puis définissez le **spawn**, le **lit** et les **PNJ Boutique/Améliorations** de chaque équipe.
   - *Gérer les Générateurs* : ajoutez un générateur de ressource ou cliquez sur un existant pour le supprimer.
5. **Activer l'arène** : lorsque le lobby, les spawns et lits de chaque équipe sont définis, utilisez le bouton d'activation pour rendre l'arène jouable. Toutes les positions sont sauvegardées immédiatement.

## ⚙️ Configuration des Générateurs

Les vitesses et quantités des ressources générées sont définies dans le fichier `generators.yml`. Ce fichier est créé automatiquement lors du premier lancement du plugin et peut être personnalisé pour ajuster le délai (en secondes) et le nombre d'items produits pour chaque type de ressource et chaque niveau.

```yaml
IRON:
  tier-1:
    delay: 1.5
    amount: 1
  tier-2:
    delay: 1.0
    amount: 1
  tier-3:
    delay: 0.5
    amount: 2
```

Modifiez ces valeurs selon vos besoins puis rechargez le plugin pour appliquer les changements.

## 🛒 Configuration de la Boutique

Le fichier `shop.yml` définit les catégories et les objets disponibles dans la boutique. Il est généré automatiquement au premier lancement du plugin et peut être modifié pour personnaliser les menus.

Exemple de structure du menu principal :

```yaml
main-menu:
  title: "Boutique d'objets"
  rows: 4
  items:
    'quick-buy':
      material: NETHER_STAR
      name: "&aAchats Rapides"
      lore:
        - "&7Les objets les plus utiles"
      slot: 10
      category: 'quick_buy_category'
```

Chaque entrée renvoie vers une catégorie définie dans `shop-categories` où les objets à vendre seront listés.
