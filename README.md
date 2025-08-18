# HeneriaBedwars

**HeneriaBedwars** est un plugin BedWars moderne, performant et entièrement configurable pour les serveurs Spigot 1.21. Conçu pour être à la fois puissant pour les administrateurs et amusant pour les joueurs, il offre un cycle de jeu complet et une gestion intuitive.

---

## ✨ Fonctionnalités Principales

Le plugin est structuré autour d'un cycle de jeu complet et d'outils d'administration puissants.

### Pour les Administrateurs

- ⚔️ **Gestion 100% en Jeu** : Créez, gérez et configurez vos arènes sans jamais avoir à éditer de fichiers manuellement grâce à une interface graphique complète (`/bw admin`).
- 🧙‍♂️ **Assistant de Création Intuitif** : Un système de création d'arène simple via le chat vous guide pour définir les paramètres de base.
- 📍 **Configuration Précise** : Utilisez un outil de positionnement en jeu pour définir avec précision l'emplacement du lobby, des lits, des points de spawn, des générateurs et des PNJ pour chaque équipe.
- ⚙️ **Haute Personnalisation** : Prenez le contrôle total du gameplay en modifiant les fichiers de configuration dédiés :
  - `generators.yml` : Réglez la vitesse et la quantité de chaque générateur de ressources.
  - `shop.yml` : Personnalisez entièrement les catégories et les objets de la boutique d'items.
  - `upgrades.yml` : Définissez les améliorations d'équipe et les pièges de base.
  - `scoreboard.yml` : Personnalisez le titre et les lignes du tableau de bord en jeu.
  - `messages.yml` : Traduisez et personnalisez tous les messages du plugin.

Ce fichier `messages.yml` est généré automatiquement et permet d'adapter le plugin à n'importe quelle langue ou style.

### Pour les Joueurs

- 🕹️ **Cycle de Jeu Complet** : Rejoignez une arène, attendez dans le lobby avec un décompte, et lancez-vous dans la bataille.
- 🛏️ **Mécaniques Classiques** : Protégez votre lit pour pouvoir réapparaître, et détruisez celui de vos ennemis pour les éliminer définitivement.
- 💰 **Système Économique** : Collectez du Fer, de l'Or, des Diamants et des Émeraudes à des vitesses différentes pour acheter de l'équipement.
- 🛒 **Boutiques Fonctionnelles** : Interagissez avec les PNJ pour acheter des objets dans la boutique d'items ou des améliorations permanentes pour votre équipe.
- 🧱 **Construction de Blocs** : Achetez, placez et cassez des blocs pour bâtir ponts et défenses.
- 🛡️ **Kit de départ lié** : Vous réapparaissez avec une armure en cuir teintée aux couleurs de votre équipe et une épée en bois impossible à jeter.
- 🌈 **Achats intelligents** : La laine achetée s'adapte automatiquement à la couleur de votre équipe et toute nouvelle épée remplace la précédente.
- 📊 **Tableau de Bord Dynamique** : Consultez en un coup d'œil l'état des équipes et le prochain événement.
- 🏆 **Conditions de Victoire** : La partie se termine automatiquement lorsque la dernière équipe en vie est déclarée vainqueur, et l'arène se réinitialise pour le prochain combat.

---

## 🚀 Installation

1.  Téléchargez la dernière version du plugin depuis la page [Releases](https://github.com/tomashb/HeneriaBW/releases).
2.  Placez le fichier `.jar` téléchargé dans le dossier `plugins` de votre serveur Spigot 1.21.
3.  Redémarrez votre serveur.
4.  Les fichiers de configuration par défaut seront générés dans le dossier `plugins/HeneriaBedwars/`.

---

## 📖 Guide d'Utilisation

### Commandes Administrateur

- `/bw admin` (alias: `/bedwars admin`)
  - Ouvre le menu principal de gestion des arènes.
  - **Permission :** `heneriabw.admin`

### Commandes Joueurs

- `/bw join <nom_de_l_arene>`
  - Permet de rejoindre une arène en attente.
  - **Permission :** `heneriabw.player.join`
- `/bw leave`
  - Permet de quitter l'arène actuelle.
  - **Permission :** `heneriabw.player.leave`
- `/bw stats [joueur]`
  - Affiche vos statistiques ou celles d'un autre joueur.
  - **Permission :** `heneriabw.admin.stats` pour consulter celles d'un autre joueur.

### Créer et Configurer une Arène (Flux de travail)

1.  Tapez `/bw admin` et cliquez sur **"Créer une Arène"**.
2.  Suivez les instructions dans le chat pour nommer votre arène et définir ses paramètres de base.
3.  Une fois créée, retournez dans `/bw admin` et cliquez sur **"Gérer les Arènes existantes"**.
4.  Cliquez sur votre nouvelle arène pour ouvrir son menu de configuration.
5.  Utilisez les différentes options et l'outil de positionnement pour définir le lobby, les équipes (lits, spawns, PNJ) et les générateurs.
6.  Quand tout est prêt, cliquez sur **"Activer l'Arène"** pour la rendre accessible aux joueurs.

### Configuration des Pièges d'Équipe

Les pièges sont définis dans le fichier `upgrades.yml` sous la section `traps`. Chaque piège possède un nom, un item de menu, un coût en diamants et un effet de potion appliqué à l'intrus.

```yaml
traps:
  miner-fatigue-trap:
    name: "&cPiège de Fatigue"
    item: PRISMARINE_SHARD
    cost: 1
    description:
      - "&7Le prochain ennemi qui entre"
      - "&7dans votre base recevra Fatigue de Minage."
    effect:
      type: SLOW_DIGGING
      duration: 10
      amplifier: 1
```

### Configuration de la Base de Données

Les statistiques des joueurs sont sauvegardées dans une base de données configurée via `config.yml` :

```yaml
database:
  type: sqlite # sqlite ou mysql
  save-interval: 5 # minutes entre chaque sauvegarde automatique
  mysql:
    host: localhost
    port: 3306
    database: bedwars
    username: root
    password: ""
    useSSL: false
```



## 📌 Placeholders

Si [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) est installé, HeneriaBedwars enregistre automatiquement les placeholders suivants :

- `%heneriabw_kills%`
- `%heneriabw_deaths%`
- `%heneriabw_wins%`
- `%heneriabw_losses%`
- `%heneriabw_beds_broken%`
- `%heneriabw_games_played%`
- `%heneriabw_kdr%`

Vous pouvez les utiliser dans n'importe quel plugin compatible avec PlaceholderAPI pour afficher les statistiques des joueurs.

---

## 🔧 Compilation (pour les développeurs)

1.  Clonez ce dépôt : `git clone https://github.com/tomashb/HeneriaBW.git`
2.  Naviguez dans le dossier : `cd HeneriaBW`
3.  Compilez avec Maven : `mvn clean package`
4.  Le JAR final se trouvera dans le dossier `target/`.
