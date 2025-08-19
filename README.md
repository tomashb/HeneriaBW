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
  - `scoreboard.yml` : Personnalisez les tableaux de bord du lobby d'attente et de la partie via les sections `lobby` et `game`.
  - `events.yml` : Planifiez les événements automatiques (amélioration des générateurs, Mort Subite, apparition de dragons) et définissez un `display-name` lisible pour l'affichage du prochain événement sur le scoreboard.
  - `config.yml` : Ajustez les réglages globaux, comme les dégâts infligés par le Golem de Fer (`mobs.iron-golem.damage`).
  - `special_shop.yml` : Définissez les objets uniques vendus par le PNJ spécial de milieu de partie, avec l'option `purchase-limit` pour limiter le nombre d'achats par joueur.
  - `messages.yml` : Traduisez et personnalisez tous les messages du plugin.

Ce fichier `messages.yml` est généré automatiquement et permet d'adapter le plugin à n'importe quelle langue ou style.

### Pour les Joueurs

- 🕹️ **Cycle de Jeu Complet** : Rejoignez une arène, attendez dans le lobby avec un décompte, et lancez-vous dans la bataille.
- 🎽 **Sélecteur d'équipe** : Choisissez votre camp grâce à un menu interactif avant le début de la partie.
- 🛏️ **Mécaniques Classiques** : Protégez votre lit pour pouvoir réapparaître, et détruisez celui de vos ennemis pour les éliminer définitivement.
- 💰 **Système Économique** : Collectez du Fer, de l'Or, des Diamants et des Émeraudes à des vitesses différentes pour acheter de l'équipement.
- 🔥 **Forge évolutive** : Améliorez la Forge de votre équipe pour accélérer le Fer et l'Or, le dernier niveau produisant même des Émeraudes sur votre île.
- 🛒 **Boutiques Fonctionnelles** : Interagissez avec les PNJ pour acheter des objets dans la boutique d'items ou des améliorations permanentes pour votre équipe.
- 🏥 **Soin de Base** : Achetez une aura de régénération autour de votre lit pour soigner vos défenseurs.
- 🔔 **Alarme Anti-Intrusion** : Un son puissant alerte toute l'équipe lorsqu'un piège est déclenché.
- 🧱 **Construction de Blocs** : Achetez, placez et cassez des blocs pour bâtir ponts et défenses. La catégorie « Blocs » propose désormais du Grès, de l'Obsidienne, des Échelles et de la Toile d'Araignée.
 - 🛡️ **Kit de départ lié** : Vous réapparaissez avec une armure en cuir teintée aux couleurs de votre équipe ainsi qu'une épée, une pioche et une hache en bois impossibles à jeter.
- 🛡️ **Progression Hybride** : Les armures achetées sont conservées après la mort, tandis que les outils et épées doivent être rachetés.
- 🗡️ **Catégorie Mêlée** : Progression d'armes de corps à corps, du Bâton de Répulsion à l'Épée en Diamant.
- 🌈 **Achats intelligents** : La laine achetée s'adapte automatiquement à la couleur de votre équipe et toute nouvelle épée remplace la précédente.
- 📊 **Tableau de Bord Dynamique** : Consultez en un coup d'œil l'état des équipes et le prochain événement.
- 🛍️ **Marchand Mystérieux** : Un PNJ spécial apparaît au centre en milieu de partie pour vendre des objets uniques comme le Golem de Fer de Poche.
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

- `/bw admin delete <nom_de_l_arene>`
  - Supprime une arène (confirmation requise via `/bw admin confirmdelete <nom_de_l_arene>`).
  - **Permission :** `heneriabw.admin.delete`

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

### Configuration de la Boutique d'Items

La boutique mélange améliorations permanentes et achats temporaires, tous définis dans le fichier `shop.yml`. Les armures (jambières et bottes) utilisent des paliers `upgrade_tier` de type `ARMOR` et sont conservées après la mort. Les pioches et haches sont vendues par paliers (`PICKAXE`, `AXE`) dont le niveau reste débloqué, mais l'outil doit être racheté après chaque mort. Les épées sont listées directement et sont toujours perdues à la mort.

```yaml
tools_category:
  items:
    stone-pickaxe:
      material: STONE_PICKAXE
      cost:
        resource: IRON
        amount: 10
      slot: 10
      upgrade_tier:
        type: 'PICKAXE'
        level: 1
    iron-pickaxe:
      material: IRON_PICKAXE
      cost:
        resource: GOLD
        amount: 4
      slot: 10
      upgrade_tier:
        type: 'PICKAXE'
        level: 2
```

Les objets peuvent aussi définir des enchantements (`enchantments`) ou des effets de potion (`potion-effects`) via des listes de mappages :

```yaml
power_bow:
  material: BOW
  enchantments:
    - type: POWER
      level: 1
speed_potion:
  material: POTION
  potion-effects:
    - type: SPEED
      duration: 45
      amplifier: 1
```

Seul le prochain palier disponible est proposé à l'achat. Après une mort, les joueurs réapparaissent avec leur meilleure armure débloquée mais uniquement les outils et armes en bois.

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

### Configuration des Événements de Jeu

Le fichier `events.yml` permet de définir une chronologie d'événements pendant une partie, comme l'amélioration automatique des générateurs :

```yaml
game-events:
  - time: '6m'
    type: 'UPGRADE_GENERATORS'
    targets: [DIAMOND]
    new-tier: 2
    display-name: "&bDiamants II"
    broadcast-message: "&bLes générateurs de Diamants ont été améliorés au Niveau II !"
```

Chaque entrée peut préciser un temps (`time`), le type d'événement (`type`), les cibles (`targets`), le nouveau niveau (`new-tier`), un nom d'affichage (`display-name`) utilisé par le scoreboard, et le message diffusé aux joueurs.

Les types disponibles incluent :

- `UPGRADE_GENERATORS` : améliore le niveau de certains générateurs.
- `SUDDEN_DEATH` : détruit tous les lits restants et empêche toute réapparition.
- `SPAWN_DRAGONS` : fait apparaître un ou plusieurs dragons pour accélérer la fin de partie.
- `SPAWN_SPECIAL_NPC` : fait apparaître temporairement le Marchand Mystérieux au centre.
- `DESPAWN_SPECIAL_NPC` : retire le Marchand Mystérieux de l'arène.

Exemple incluant ces nouveaux événements :

```yaml
game-events:
  - time: '30m'
    type: 'SUDDEN_DEATH'
    broadcast-message: "&c&lMORT SUBITE ! &fTous les lits restants ont été détruits !"

  - time: '31m'
    type: 'SPAWN_DRAGONS'
    broadcast-message: "&c&lLES DRAGONS ARRIVENT !"

  - time: '15m'
    type: 'SPAWN_SPECIAL_NPC'
    broadcast-message: "&d&lUn Marchand Mystérieux est apparu au centre !"

  - time: '18m'
    type: 'DESPAWN_SPECIAL_NPC'
    broadcast-message: "&dLe Marchand Mystérieux est parti !"
```

### Configuration du Marchand Mystérieux

Le contenu de la boutique du PNJ spécial est défini dans le fichier `special_shop.yml`. Chaque objet peut inclure un champ `purchase-limit` pour limiter le nombre d'achats par joueur :

```yaml
title: "&5Marchand Mystérieux"
rows: 3
items:
  iron-golem:
    material: IRON_BLOCK
    name: "&fGolem de Fer de Poche"
    lore:
      - "&7Posez ce bloc pour faire apparaître"
      - "&7un Golem de Fer qui défendra votre île."
    cost:
      resource: DIAMOND
      amount: 8
    slot: 11
    action: 'SPAWN_IRON_GOLEM'
    purchase-limit: 1
  super-fireball:
    material: FIRE_CHARGE
    name: "&cSuper Boule de Feu"
    cost:
      resource: EMERALD
      amount: 4
    slot: 15
    purchase-limit: 3
```

### Configuration du Scoreboard

Le fichier `scoreboard.yml` est divisé en deux sections : `lobby` pour le lobby d'attente et `game` pour la partie. Chaque section possède son propre `title` et sa liste de `lines`, avec des placeholders dédiés.

- **Lobby :** `{date}`, `{map_name}`, `{current_players}`, `{max_players}`, `{status}`
- **Jeu :** `{date}`, `{next_event_name}`, `{next_event_time}`, `{team_status}`

Exemple complet :

```yaml
# Scoreboard pour le lobby d'attente
lobby:
  title: "&b&lHeneria BedWars"
  lines:
    - "&7{date}"
    - "&1"
    - "Carte: &a{map_name}"
    - "&2"
    - "Joueurs: &a{current_players}/{max_players}"
    - "&3"
    - "&e{status}"
    - "&4"
    - "&eheneria.com"

# Scoreboard pour la partie en cours
game:
  title: "&b&lHeneria BedWars"
  lines:
    - "&7{date}"
    - "&1"
    - "Prochain événement:"
    - "&a{next_event_name} &fen &a{next_event_time}"
    - "&2"
    - "{team_status}"
    - "&3"
    - "&eheneria.com"

team-line-format: "{team_color_code}{team_icon} {team_bed_status} &f{team_players_alive} {you_marker}"
```

### Limites de Construction de l'Arène

Chaque arène peut définir des limites pour empêcher les constructions abusives. Dans le fichier `arenas/<nom>.yml`, ajoutez :

```yaml
boundaries:
  max-y: 150
  max-distance-from-center: 100
```

Les blocs placés au‑dessus ou au‑delà de ces limites seront automatiquement annulés côté serveur.

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


### Configuration des Mobs

Le fichier `config.yml` permet aussi d'ajuster certains paramètres d'équilibrage :

```yaml
mobs:
  iron-golem:
    damage: 4.0
```

Cette valeur contrôle les dégâts infligés par les Golems de Fer invoqués par les joueurs.


---

## 🔧 Compilation (pour les développeurs)

1.  Clonez ce dépôt : `git clone https://github.com/tomashb/HeneriaBW.git`
2.  Naviguez dans le dossier : `cd HeneriaBW`
3.  Compilez avec Maven : `mvn clean package`
4.  Le JAR final se trouvera dans le dossier `target/`.
