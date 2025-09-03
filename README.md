# HeneriaBedwars

**HeneriaBedwars** est un plugin BedWars moderne, performant et entièrement configurable pour les serveurs Spigot 1.21. Conçu pour être à la fois puissant pour les administrateurs et amusant pour les joueurs, il offre un cycle de jeu complet et une gestion intuitive.

---

## ✨ Fonctionnalités Principales

Le plugin est structuré autour d'un cycle de jeu complet et d'outils d'administration puissants.

### Pour les Administrateurs

- ⚔️ **Gestion 100% en Jeu** : Créez, gérez et configurez vos arènes sans jamais avoir à éditer de fichiers manuellement grâce à une interface graphique complète (`/bw admin`).
- 🧙‍♂️ **Assistant de Création Intuitif** : Un système de création d'arène simple via le chat vous guide pour définir les paramètres de base.
- 🧍 **Assistant PNJ Guidé** : Créez des PNJ du lobby via une conversation étape par étape dans le chat.
- 🛑 **Messages Administrateur Lisibles** : Les retours de commandes sont préfixés et colorés pour se distinguer du chat.
- 📍 **Configuration Précise** : Utilisez un outil de positionnement en jeu pour définir avec précision l'emplacement du lobby, des lits, des points de spawn, des générateurs et des PNJ pour chaque équipe.
- ⚙️ **Haute Personnalisation** : Prenez le contrôle total du gameplay en modifiant les fichiers de configuration dédiés :
  - `generators.yml` : Réglez la vitesse et la quantité de chaque générateur de ressources.
  - `shop.yml` : Personnalisez entièrement les catégories et les objets de la boutique d'items.
  - `upgrades.yml` : Définissez les améliorations d'équipe et les pièges de base.
  - `scoreboard.yml` : Personnalisez les tableaux de bord du lobby principal, du lobby d'attente et de la partie via les sections `main-lobby`, `lobby` et `game`. La section `main-lobby` inclut une rubrique `Infos` (grade, rang, Elo, Henacoins) reposant sur PlaceholderAPI (`%luckperms_prefix%`, `%vault_eco_balance_formatted%`).
  - `tablist.yml` : Configurez l'en-tête et le pied de page du lobby principal (`main-lobby`), du lobby d'attente (`waiting-lobby`) et de la partie (`game`) avec couleurs, sauts de ligne (`\n`) et placeholders.
  - `events.yml` : Planifiez les événements automatiques (amélioration des générateurs, Mort Subite, apparition de dragons) et définissez un `display-name` lisible pour l'affichage du prochain événement sur le scoreboard.
  - `config.yml` : Ajustez les réglages globaux, comme les dégâts infligés par le Golem de Fer (`mobs.iron-golem.damage`), la hauteur de téléportation anti-vide (`void-teleport-height`), personnalisez le format du chat via `chat-format`, contrôlez les animations du lobby via `animations.lobby-npc` (`enable`, `levitation-strength`, `presentation-speed`) et définissez les textures des items du lobby (`team-selector-item.skin`, `leave-item.skin`, `lobby-shop-item.skin`).
  - `special_shop.yml` : Définissez les objets uniques vendus par le PNJ spécial de milieu de partie, avec l'option `purchase-limit` pour limiter le nombre d'achats par joueur.
  - `messages.yml` : Traduisez et personnalisez tous les messages du plugin, y compris `server.join-message` et `server.leave-message` (préfixe vide par défaut).

Ce fichier `messages.yml` est généré automatiquement et permet d'adapter le plugin à n'importe quelle langue ou style.

### Pour les Joueurs

- 🎡 **Lobby Principal Immersif** : Les joueurs apparaissent dans un lobby central et choisissent leur mode via des PNJ interactifs. Un message de bienvenue personnalisé et un scoreboard de statistiques les accueillent.
- 🎮 **Hub de Jeu Intuitif** : En cliquant sur un PNJ de mode, un menu propose de lancer une partie, consulter ses statistiques ou se reconnecter.
- 🪧 **Stats par Mode** : Chaque PNJ affiche un hologramme indiquant en temps réel le nombre de joueurs pour son mode.
- 🕹️ **Cycle de Jeu Complet** : Rejoignez une arène, attendez dans le lobby avec un décompte, et lancez-vous dans la bataille.
- 👁️ **Vue Spectateur Optimisée** : Après la mort, les joueurs sont téléportés au-dessus du lobby pour observer la partie.
- ⚔️ **PvP 1.8** : Combat sans délai de recharge avec particules de coup critique à chaque attaque pour un ressenti classique.
- 🎽 **Sélecteur d'équipe** : Menu 3x9 centré avec bannières colorées, accessible par clic gauche ou droit.
- 🚪 **Menu Quitter la partie** : Confirmation stylisée (3x9) s'ouvrant via clic gauche ou droit.
- 💬 **Chat et Tablist isolés** : Les messages et la liste des joueurs sont limités à votre partie pour éviter le spam entre arènes.
- 🗨️ **Préfixe coloré dans le chat** : Les messages en partie indiquent clairement la couleur de l'équipe du joueur.
- ⏱️ **Décompte épuré** : Affiché uniquement en titre, il n'est annoncé dans le chat qu'à 10 puis de 5 à 1 seconde(s).
- 🧴 **Effets réinitialisés** : Tous les bonus temporaires sont supprimés lorsque vous retournez au lobby.
- 🎨 **Couleurs d'équipe dynamiques** : Les pseudos des joueurs prennent la couleur de leur équipe dans la tablist et au-dessus de leur tête.
- 📋 **Tablist en Jeu Détaillée** : Affiche pour chaque joueur la couleur exacte de son équipe et l'état de son lit.
- 🎭 **Icônes de Lobby Personnalisées** : Boutique, sélecteur d'équipe et sortie utilisent des têtes texturées uniques.
- 🛏️ **Mécaniques Classiques** : Protégez votre lit pour pouvoir réapparaître, détruisez celui de vos ennemis pour les éliminer définitivement et utilisez des boules de feu pour percer la laine.
- 💰 **Système Économique** : Collectez du Fer, de l'Or, des Diamants et des Émeraudes à des vitesses différentes pour acheter de l'équipement.
- 🎯 **Système de primes à paliers** : Devenez recherché en enchaînant les éliminations et offrez des récompenses croissantes à ceux qui vous arrêtent.
- 📡 **Hologrammes Intégrés** : Compte à rebours dynamique au-dessus des générateurs de Diamants et d'Émeraudes sans dépendance externe.
- 🔥 **Forge évolutive** : Améliorez la Forge de votre équipe pour accélérer le Fer et l'Or, le dernier niveau produisant même des Émeraudes sur votre île.
- 🛒 **Boutiques Fonctionnelles** : Interagissez avec les PNJ pour acheter des objets dans une boutique colorée (vitres teintées par catégorie, section d'achats rapides enrichie) ou des améliorations permanentes pour votre équipe.
- 🧍 **PNJ personnalisés** : Les vendeurs d'objets et d'améliorations arborent désormais des skins distincts.
- 🪧 **Hologramme stable** : L'affichage du PNJ central du lobby reste unique et statique au-dessus de sa tête.
- 🧱 **Achat rapide soigné** : Les vitres trempées ne sont plus proposées et les cases vides sont remplies de vitres décoratives.
- 💎 **Forge Max fiable** : Le niveau maximal de forge génère immédiatement des émeraudes dans votre base.
- 🏥 **Soin de Base** : Achetez une aura de régénération autour de votre lit pour soigner vos défenseurs.
- 🔔 **Alarme Anti-Intrusion** : Un son puissant alerte toute l'équipe lorsqu'un piège est déclenché.
- ⚔️ **Butin de Guerre** : Récoltez du Fer et de l'Or bonus à chaque élimination.
- 🛍️ **Réduction d'Équipe** : Bénéficiez de 10% puis 20% de remise sur tous les achats de la boutique.
- 🪤 **Menu "Upgrades & Traps" remanié** : Les améliorations et pièges sont affichés sur deux rangées centrées avec descriptions colorées, les pièges achetés apparaissant dans une barre dédiée.
  - Blindness Trap : applique Cécité aux intrus.
  - Counter-Offensive Trap : donne Speed II et Jump Boost II aux alliés pendant 15 s.
  - Reveal Trap : révèle et illumine les ennemis invisibles.
  - Miner Fatigue Trap : ralentit le minage des ennemis.
- 📦 **Gestion du stock de pièges** : une équipe ne peut avoir que 3 pièges actifs simultanément et un piège déclenché libère immédiatement un emplacement.
- 💬 **Messages d'achat stylisés** : chaque achat affiche un message coloré indiquant l'objet et son prix.
- 🧱 **Construction de Blocs** : Achetez, placez et cassez des blocs pour bâtir ponts et défenses. La catégorie « Blocs » propose désormais du Grès, de l'Obsidienne, des Échelles et de la Toile d'Araignée. Des limites configurables empêchent de construire hors de la zone de jeu.
 - 🛡️ **Kit de départ lié** : Vous réapparaissez avec une armure en cuir teintée aux couleurs de votre équipe ainsi qu'une épée, une pioche et une hache en bois impossibles à jeter.
- 🛡️ **Armures Directes** : Achetez directement l'armure de votre choix (mailles, fer ou diamant) et conservez-la après la mort, tandis que les outils et épées doivent être rachetés.
- 🗡️ **Catégorie Mêlée** : Progression d'armes de corps à corps, du Bâton de Répulsion à l'Épée en Diamant.
- 🌈 **Achats intelligents** : La laine achetée s'adapte automatiquement à la couleur de votre équipe et toute nouvelle épée remplace la précédente.
- 📊 **Tableau de Bord Dynamique** : Consultez en un coup d'œil l'état des équipes et le prochain événement.
- 🛍️ **Marchand Mystérieux** : Un PNJ spécial apparaît au centre en milieu de partie pour vendre des objets uniques comme le Golem de Fer de Poche.
- 💄 **Boutique de Cosmétiques** : Personnalisez votre expérience dans le lobby avec des particules, skins de PNJ, effets de kill et messages, accessible via un objet dédié dans votre inventaire.
- 🏆 **Conditions de Victoire** : La partie se termine automatiquement lorsque la dernière équipe en vie est déclarée vainqueur, et l'arène se réinitialise pour le prochain combat.

---

## 🚀 Installation

1.  Téléchargez la dernière version du plugin depuis la page [Releases](https://github.com/tomashb/HeneriaBW/releases).
2.  Placez le fichier `.jar` téléchargé dans le dossier `plugins` de votre serveur Spigot 1.21.
3.  Redémarrez votre serveur.
4.  Les fichiers de configuration par défaut seront générés dans le dossier `plugins/HeneriaBedwars/`.

## 🔌 Placeholders et dépendances

Le plugin peut s'intégrer avec plusieurs dépendances :
- [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI) pour afficher des informations dynamiques.
- [Vault](https://github.com/MilkBowl/Vault) pour gérer l'économie du serveur. Vault est requis pour la boutique de cosmétiques du lobby et pour les placeholders économiques.

Placeholders disponibles :
- `%luckperms_prefix%` : grade du joueur (nécessite LuckPerms).
- `%vault_eco_balance_formatted%` : solde de l'économie.

PlaceholderAPI reste optionnel mais Vault et un plugin d'économie compatible sont nécessaires pour la boutique de cosmétiques.

---

## 📖 Guide d'Utilisation

### Commandes Administrateur

- `/bw admin` (alias: `/bedwars admin`)
  - Ouvre le menu principal de gestion des arènes.
  - **Permission :** `heneriabw.admin`

- `/bw admin delete <nom_de_l_arene>`
  - Supprime une arène (confirmation requise via `/bw admin confirmdelete <nom_de_l_arene>`).
  - **Permission :** `heneriabw.admin.delete`
- `/bw admin setmainlobby`
  - Définit la position du lobby principal BedWars.
  - **Permission :** `heneriabw.admin.setmainlobby`
- `/bw admin lobby`
  - Ouvre le panneau de contrôle pour gérer les PNJ du lobby (création, édition, suppression).
  - **Permission :** `heneriabw.admin.lobby`
- `/bw admin confirmnpc`
  - Finalise la création d'un PNJ du lobby à votre position actuelle.
  - **Permission :** `heneriabw.admin.lobby`
- `/bw admin setshopnpc <équipe> <type_boutique> [<plastron> <jambieres> <bottes>]`
  - Place un PNJ de boutique (`item` ou `upgrade`) sous forme de support d'armure pour l'équipe spécifiée. L'armure en cuir est automatiquement teinte à la couleur de l'équipe.
  - **Permission :** `heneriabw.admin.setshopnpc`
- `/bw admin bypass`
  - Active ou désactive le mode bypass pour modifier la carte sans restrictions.
  - **Permission :** `heneriabw.admin.bypass`

Pour créer un PNJ de sélection d'arène, ouvrez le menu `/bw admin lobby`, cliquez sur « Créer un PNJ » puis répondez aux questions dans le chat (skin, mode, nom, équipement...). Placez-vous à l'endroit souhaité et validez avec `/bw admin confirmnpc`.

### Commandes Joueurs

La boutique de cosmétiques est accessible via un objet spécial dans votre inventaire du lobby.

- `/bw join <nom_de_l_arene>`
  - Rejoint une arène en attente (principalement pour les tests, l'accès se fait désormais via les PNJ du lobby).
  - **Permission :** `heneriabw.player.join`
- `/bw leave`
  - Permet de quitter l'arène actuelle.
  - **Permission :** `heneriabw.player.leave`
- `/bw stats [joueur]`
  - Affiche vos statistiques ou celles d'un autre joueur.
  - **Permission :** `heneriabw.admin.stats` pour consulter celles d'un autre joueur.
- `/spawn`
  - Téléporte le joueur au lobby principal BedWars. Utilisable en jeu pour quitter la partie ; déclenche une vérification de victoire.
- `/hub`
  - Envoie le joueur vers le serveur lobby principal si BungeeCord est activé, sinon fonctionne comme `/spawn`.

#### Permissions de Cosmétiques
- `heneria.cosmetics.kill_effect.lightning` : exemple de permission accordée après achat.

### Créer et Configurer une Arène (Flux de travail)

1.  Tapez `/bw admin` et cliquez sur **"Créer une Arène"**.
2.  Suivez les instructions dans le chat pour nommer votre arène et définir ses paramètres de base.
3.  Une fois créée, retournez dans `/bw admin` et cliquez sur **"Gérer les Arènes existantes"**.
4.  Cliquez sur votre nouvelle arène pour ouvrir son menu de configuration.
5.  Utilisez les différentes options et l'outil de positionnement pour définir le lobby, les équipes (lits, spawns, PNJ) et les générateurs.
6.  Quand tout est prêt, cliquez sur **"Activer l'Arène"** pour la rendre accessible aux joueurs.

### Configuration de la Boutique d'Items

La boutique mélange améliorations permanentes et achats temporaires, tous définis dans le fichier `shop.yml`. Ce dernier est organisé en trois sections :

- `category-tabs` : définit les onglets du menu.
- `quick-buy-items` : liste des objets d'achats rapides affichés par défaut.
- `shop-categories` : toutes les catégories détaillées de la boutique.

```yaml
category-tabs:
  blocks:
    material: BRICKS
    slot: 1
    category: blocks_category
quick-buy-items:
  stone-sword:
    material: STONE_SWORD
    cost:
      resource: IRON
      amount: 10
    slot: 20
shop-categories:
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
```

Les armures (jambières et bottes) utilisent des paliers `upgrade_tier` de type `ARMOR` et sont conservées après la mort. Les pioches et haches sont vendues par paliers (`PICKAXE`, `AXE`) dont le niveau reste débloqué, mais l'outil doit être racheté après chaque mort. Les épées sont listées directement et sont toujours perdues à la mort.

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

### Configuration de la Boutique de Cosmétiques

Le fichier `lobby_shop.yml` définit les cosmétiques disponibles dans le lobby. Chaque objet comporte :

- `display-item` : l'item affiché dans le menu.
- `vault-cost` : coût en monnaie via Vault.
- `command-on-purchase` : commande exécutée après l'achat (placeholder `{player}`).

Exemple :

```yaml
items:
  kill_effect_lightning:
    display-item: NETHER_STAR
    name: "&eEffet Kill Foudre"
    lore:
      - "&7Coût: &61000"
    vault-cost: 1000
    command-on-purchase: "lp user {player} permission set heneria.cosmetics.kill_effect.lightning"
    slot: 11
```

La commande attribue généralement une permission comme `heneria.cosmetics.kill_effect.lightning` au joueur.

### Configuration des Améliorations et Pièges d'Équipe

Le fichier `upgrades.yml` fonctionne désormais comme `shop.yml` avec un menu principal listant des catégories. Chaque entrée du menu pointe vers une catégorie contenant soit des améliorations permanentes, soit des pièges.

```yaml
main-menu:
  title: "Améliorations d'équipe"
  rows: 3
  items:
    general:
      material: DIAMOND_SWORD
      name: "&aAméliorations Générales"
      slot: 11
      category: general
    traps:
      material: TRIPWIRE_HOOK
      name: "&cPièges"
      slot: 15
      category: traps

upgrade-categories:
  traps:
    title: "Pièges"
    rows: 3
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

Les améliorations d'équipe suivent la même structure dans la catégorie `general` avec une section `upgrades` listant chaque amélioration et ses paliers.

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

Le fichier `scoreboard.yml` est divisé en trois sections : `main-lobby` pour le lobby principal, `lobby` pour le lobby d'attente et `game` pour la partie. Chaque section possède son propre `title` et sa liste de `lines`, avec des placeholders dédiés.

- **Lobby principal :** `{player}`, `{wins}`, `{kills}`, `{beds_broken}`
- **Lobby d'attente :** `{date}`, `{map_name}`, `{current_players}`, `{max_players}`, `{status}`
- **En jeu :** `{date}`, `{next_event_name}`, `{next_event_time}`, `{team_status}`

Exemple complet :

```yaml
# Scoreboard pour le lobby principal
main-lobby:
  title: "&b&lHeneria Network"
  lines:
    - "&7Joueur: &f{player}"
    - "&1"
    - "&f&lStatistiques"
    - " &fVictoires: &a{wins}"
    - " &fKills: &a{kills}"
    - " &fLits détruits: &a{beds_broken}"
    - "&2"
    - "&eplay.votreserveur.com"

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
    - "&a{next_event_name} &fdans &a{next_event_time}"
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
  max-height: 150
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

### Configuration BungeeCord

```yaml
bungeecord:
  enabled: false
  lobby-server-name: "lobby"
```

Activez `bungeecord.enabled` pour que la commande `/hub` connecte les joueurs au serveur défini. Sinon, `/hub` se comporte comme `/spawn`.


### Configuration des Mobs

Le fichier `config.yml` permet aussi d'ajuster certains paramètres d'équilibrage :

```yaml
mobs:
  iron-golem:
    damage: 4.0
```

Cette valeur contrôle les dégâts infligés par les Golems de Fer invoqués par les joueurs.

### Animations du Lobby

Les PNJ du lobby disposent d'animations configurables dans `config.yml` :

```yaml
animations:
  lobby-npc:
    enable: true
    levitation-strength: 0.1
    presentation-speed: 1.0
```

- `enable` : active ou désactive totalement les animations.
- `levitation-strength` : amplitude de la lévitation verticale (en blocs).
- `presentation-speed` : vitesse des mouvements de présentation d'objet.

---

## 🔧 Compilation (pour les développeurs)

1.  Clonez ce dépôt : `git clone https://github.com/tomashb/HeneriaBW.git`
2.  Naviguez dans le dossier : `cd HeneriaBW`
3.  Compilez avec Maven : `mvn clean package`
4.  Le JAR final se trouvera dans le dossier `target/`.

### Notes de développement

- Correction d'une erreur de compilation en renommant `PotionEffectType.JUMP` en `PotionEffectType.JUMP_BOOST`.
- Suppression d'avertissements Maven liés à une API dépréciée et à des opérations non vérifiées.
- Résolution d'une erreur de compilation due à la redéfinition de la variable `key` dans `ShopManager#parseItem`.
- Mise à jour de la gestion des textures de têtes personnalisées avec `PlayerProfile`.
- Correction d'une incompatibilité de type dans `ItemBuilder#setSkullTexture` en convertissant les chaînes d'URL en objets `URL`.
- Remplacement de `PotionEffectType#getByName` par `PotionEffectType#getByKey` pour supprimer les avertissements de dépréciation.
- Correction d'un bug critique de duplication infinie des PNJ du lobby provoquant une chute drastique des performances.
- Suppression d'une redéclaration de variable dans `ShopMenu#handleClick` causant un échec de compilation.
- Remplacement de l'API dépréciée `getByKey` par `Registry` dans `ShopManager`.
- Correction d'une erreur de compilation en remplaçant `Registry.POTION_EFFECT` par `Registry.POTION_EFFECT_TYPE` et migration de `UpgradeManager` vers cette API pour supprimer l'avertissement de dépréciation.
- Correction d'une erreur de compilation en remplaçant `Registry.POTION_EFFECT_TYPE` par `Registry.EFFECT` dans `ShopManager` et `UpgradeManager`.
- Ajustement de la hauteur de l'hologramme des PNJ du lobby pour éviter tout chevauchement avec leur nametag.
- Suppression du bouton "Ressusciter" qui clignotait brièvement après la mort des joueurs.
- Déplacement de Butin de Guerre et Réduction d'Équipe vers la troisième rangée (slots 6 et 7) du menu d'améliorations.
- Correction de l'affichage de la couleur d'équipe dans le chat en partie.
- Rafraîchissement instantané des interfaces visuelles (scoreboard, tablist) pour une meilleure réactivité.
