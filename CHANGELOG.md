# Changelog - HeneriaBedwars

## [3.1.2] - 2024-??-??

### Modifié
- Pied de page de la tablist du lobby principal avec message promotionnel et affichage du nombre de joueurs connectés.

### Corrigé
- Protection contre le vide du lobby téléportant correctement les joueurs au spawn.

## [3.1.1] - 2024-??-??

### Modifié
- Amélioration du scoreboard du lobby principal avec de nouveaux placeholders.

## [3.1.0] - 2024-??-??

### Ajouté
- Message de bienvenue configurable à la connexion.
- Scoreboard du lobby principal affichant les statistiques des joueurs.

## [3.0.1] - 2024-??-??

### Corrigé
- Rétablissement de la visibilité des joueurs après les parties.
- Suppression des blocs placés lors du reset d'arène.
- Empêchement de rejoindre une arène pleine ou déjà en cours.

## [2.6.0] - 2024-??-??

### Ajouté
- Commandes `/spawn` et `/hub` pour faciliter la navigation des joueurs.

## [2.5.0] - 2024-??-??

### Ajouté
- Ajout de l'isolation du chat et de la tablist par arène.

## [2.4.2] - 2024-??-??

### Corrigé
- Déclaration correcte du vainqueur si un joueur se déconnecte.
- Le menu des améliorations utilise désormais un titre localisé et est divisé en catégories.
- Les explosions de boules de feu ne détruisent que la laine placée par les joueurs.

### Modifié
- Coût de la Tour Instantanée ajusté à 25 Fer.
- Coût de la Plateforme de Secours ajusté à 1 Émeraude.

## [2.4.1] - 2024-??-??

### Corrigé
- Correction d'un bug critique où tous les PNJ ouvraient le mauvais menu.

## [2.4.0] - 2024-??-??

### Ajouté
- Nouveau **Hub de Jeu** remplaçant la liste d'arènes avec accès rapide au jeu, aux statistiques et à la reconnexion.
- Système expérimental de reconnexion permettant aux joueurs de revenir en partie pendant une courte durée.

## [2.3.1] - 2024-??-??

### Corrigé
- Correction d'un crash lors de la création de PNJ avec un nom ou un skin trop long.
- Téléportation de fin de partie redirige désormais les joueurs vers le lobby principal.
- Les items de lobby fonctionnent correctement lors d'un clic dans le vide.
- La victoire est déclarée si un joueur se déconnecte et qu'il ne reste qu'une équipe.

## [2.3.0] - 2024-??-??

### Ajouté
- Ajout d'une interface graphique complète pour la gestion des PNJ du lobby via `/bw admin lobby`.
- Finalisation de l'assistant de création de PNJ par conversation chat.

### Corrigé
- Correction d'une erreur de compilation liée à un accès protégé dans les menus.

## [2.2.2] - 2024-??-??

### Ajouté
- Ajout d'animations (pose statique et respiration) pour les PNJ.
- Ajout d'animations de présentation d'objet et de lévitation pour les PNJ du lobby.

### Corrigé
- Correction d'un bug critique qui empêchait l'ouverture des menus de boutique via les PNJ.

### Modifié
- Remplacement du système d'hologrammes par une solution interne pour une meilleure stabilité.

## [2.2.1] - 2024-05-09

### Ajouté
- Ajout de la commande /bw admin removenpc pour supprimer les PNJ du lobby.
- Ajout de la possibilité d'équiper les PNJ avec une armure.

### Corrigé
- Correction d'un bug où l'armure n'était pas appliquée aux PNJ lors de leur création par commande.

## [2.2.0] - 2024-05-08

### Modifié
- Remplacement des PNJ Villageois par un système de Supports d'Armure personnalisables.

## [2.1.2] - 2024-05-07

### Modifié
- Suppression de l'intégration de Citizens et retour à un système de PNJ Villageois pour améliorer la stabilité.

### Corrigé
- Correction d'une erreur de compilation dans le `NpcManager` liée au typage des données de configuration.

## [2.1.0] - 2024-05-06

### Corrigé
- Correction d'une erreur de build en ajoutant le dépôt Maven officiel de Citizens.

### Ajouté
- Intégration de l'API Citizens pour créer des PNJ joueurs aux skins personnalisés.

## [2.0.0] - 2024-05-05

### Ajouté
- Introduction d'un lobby principal avec PNJ de sélection d'arène.

## [1.3.0] - 2024-05-04

### Ajouté
- Nouvelle amélioration d'équipe : Soin de Base créant une aura de régénération autour du lit.
- Nouvelle amélioration d'équipe : Alarme Anti-Intrusion jouant un son lorsque vos pièges se déclenchent.

## [1.2.0] - 2024-05-03

### Ajouté
- Nouvelles catégories de boutique : Potions et Combat à Distance.
- Potions de Vitesse, Saut et Invisibilité avec leurs effets configurables.
- Flèches, Arc et Arc Puissant (Puissance I).
- Pomme d'Or et Seau d'eau dans les Utilitaires.
- Ajout de la catégorie Mêlée à la boutique avec 4 nouvelles armes.

### Corrigé
- Lecture robuste des listes `potion-effects` et `enchantments` dans `ShopManager`.
- Remplacement de `PotionEffectType.valueOf` par `PotionEffectType.getByName`.

## [1.1.0] - 2024-05-01

### Ajouté
- Système de primes récompensant les joueurs mettant fin aux séries d'éliminations.
- Nouveaux objets spéciaux : Boussole Traqueuse, Verre Trempé, Éponge Magique, Plateforme de Secours et Lait du Guérisseur.
- Protection contre les explosions pour le Verre Trempé.

### Corrigé
- Utilisation de `Bukkit.getPlayer` pour la logique du Lait du Guérisseur afin d'éviter l'erreur de compilation.

## [1.0.1] - 2024-05-02

### Ajouté
- Ajout du Grès, de l'Obsidienne, des Échelles et de la Toile d'Araignée à la boutique.

## [1.0.0] - 2024-04-30

### Ajouté
- Sélecteur d'équipe interactif permettant aux joueurs de choisir leur camp avant le début de la partie.
- Protections complètes du lobby d'attente (PvP désactivé, mode aventure).
- Limites de construction configurables incluant une distance maximale au centre de l'arène.
- Toutes les fonctionnalités prévues pour la version initiale sont désormais implémentées.

### Corrigé
- Le sélecteur d'équipe fonctionne aussi lors d'un clic dans les airs et son menu affiche des bordures en verre ainsi que les équipes pleines.
- La vitesse des générateurs est réinitialisée entre les parties et les émeraudes de forge apparaissent désormais sur les générateurs des îles.
- Les joueurs ne perdent plus de faim dans une arène.
- Détection du clic droit dans les items de lobby pour corriger l'erreur de compilation et le clic dans le vide.
- Le Verre Trempé a été déplacé dans la catégorie "Blocs" et la génération d'or a été ralentie.
- L'item "Quitter la partie" occupe désormais le 9ème slot et le titre du sélecteur d'équipe est correctement chargé.
- Les minerais apparaissent maintenant au centre exact des générateurs.

## [1.0.0-RC4] - En développement

### Ajouté
- Message de chat lors de la destruction d'un lit.
- Commande `/bw admin delete` avec confirmation pour supprimer une arène.
- Jour permanent dans les arènes (cycle jour/nuit désactivé).
- Item de lobby pour quitter rapidement une arène.

### Corrigé
- Réinitialisation des améliorations d'équipe à la fin de chaque partie.
- Sélecteur d'équipe : clic dans l'air, nom coloré et menu avec bordure repensé.

## [1.0.0-RC3] - En développement

### Corrigé
- Correction d'un bug de blocage du décompte de démarrage de partie.
- Correction d'un bug critique qui empêchait le démarrage des parties (monde non défini).
- Tous les équipements fournis ou achetés sont désormais incassables.
- La Forge d'équipe accélère le Fer et l'Or avec des paliers rééquilibrés et génère des Émeraudes au niveau final.
- Les nouvelles armes achetées héritent correctement des enchantements d'équipe débloqués.
- Correction d'une faute de frappe dans le scoreboard par défaut.

## [1.0.0-RC2] - En développement

### Ajouté
- Réinitialisation complète des arènes avec restauration des lits et nettoyage des items.
- Limites de construction configurables par arène via `boundaries.max-y`.
- Mort instantanée dans le vide grâce à `void-kill-height`.
- Ajout d'un scoreboard distinct et configurable pour le lobby d'attente.

### Corrigé
- Mise à jour de l'API Spigot : remplacement de `EntityType.DROPPED_ITEM` par `EntityType.ITEM`.

## [1.0.0-RC] - En développement

### Corrigé
- Le scoreboard affiche désormais un nom d'événement lisible grâce au champ `display-name`.
- La météo est forcée sur un temps clair et le cycle de pluie est désactivé dans les arènes.
- Les dégâts du Golem de Fer sont maintenant configurables via `mobs.iron-golem.damage`.
- Les joueurs ne peuvent plus interagir avec les lits pour dormir pendant une partie.

## [1.0.0-BETA] - En développement

### Modifié
- Refonte finale du système de progression de l'équipement : les armures sont des améliorations permanentes, les outils et épées sont des achats temporaires.

## [0.9.2] - En développement

### Corrigé
- Boutique spéciale : ajout des limites d'achat et remplacement de la Pioche en Diamant par la Super Boule de Feu.
- Le Golem de Fer ne peut être posé que sur l'île de son propriétaire et cible maintenant les ennemis.
- Correction de l'événement `SPAWN_DRAGONS` pour que les dragons apparaissent correctement.
- Désactivation de la perte de faim pendant les parties.

### Ajouté
- Ajout de l'achat d'outils et d'armures par paliers individuels dans la boutique d'objets.

## [0.9.1] - En développement

### Corrigé
- Correction d'une `NullPointerException` en initialisant l'état par défaut des arènes à leur création.
- Correction d'un bug dans l'algorithme de vérification d'espace qui empêchait la Tour Instantanée de fonctionner.

### Ajouté
- Apparition d'un PNJ spécial de milieu de partie et ajout de l'objet "Golem de Fer de Poche".

## [0.9.0] - En développement

### Corrigé
- Correction de l'URL du dépôt Maven pour PlaceholderAPI, résolvant les erreurs de build.
- Correction d'une erreur de compilation due à une mauvaise utilisation de l'API `BlockFace` pour la Tour Instantanée.

### Ajouté
- Système d'événements temporisés améliorant automatiquement les générateurs de Diamants et d'Émeraudes avec annonces et intégration au scoreboard.
- Ajout des événements de Mort Subite (destruction des lits) et d'apparition de Dragons.
- Ajout de l'objet spécial "Tour Instantanée".

## [0.8.0] - En développement

### Ajouté
- Ajout d'un système de statistiques joueurs avec support SQLite et MySQL.

## [0.7.0] - En développement

### Ajouté
- Ajout d'un fichier `messages.yml` pour permettre la traduction complète du plugin.

## [0.6.1] - En développement

### Ajouté
- Ajout d'un scoreboard en jeu dynamique et configurable via `scoreboard.yml`.
- Ajout des pièges de base configurables dans la boutique d'améliorations.

### Corrigé
- Correction d'un bug de duplication de la TNT.
- Les épées achetées dans la boutique sont maintenant liées au joueur et ne peuvent plus être jetées.

## [0.6.0] - En développement

### Ajouté
- Ajout de la mécanique de construction : les joueurs peuvent maintenant acheter, poser et casser des blocs.
- Amélioration des kits de départ (armure colorée liée, épée non-jetable). La laine achetée est désormais de la couleur de l'équipe et les nouvelles épées remplacent les anciennes.
- Ajout des objets spéciaux : Boule de Feu, TNT à allumage rapide, Pont en œuf et Perle de l'End.

### Corrigé
- Correction de l'utilisation de l'API de détection des clics pour la Boule de Feu (remplacement de `isRightClick()` par la vérification de `Action.RIGHT_CLICK_AIR`/`BLOCK`).

## [0.5.1] - En développement

### Corrigé
- Correction d'erreurs de compilation dues à la mise à jour des noms de constantes de l'API Spigot 1.21.

### Documentation
- Refonte complète du fichier README.md pour refléter l'état actuel du projet et améliorer la présentation.

## [0.5.0] - 2024-??-??

### Ajouté
- Ajout de la boutique d'améliorations d'équipe. Le système économique de l'Étape 3 est maintenant complet.

## [0.4.1] - En développement

### Ajouté
- La boutique d'objets est maintenant entièrement fonctionnelle, permettant l'achat d'items avec les ressources du jeu.

### Modifié
- Re-architecture du système de PNJ pour une gestion par équipe au lieu de par arène.

## [0.4.0] - En développement

### Ajouté
- Mise en place des fondations du système de boutique avec PNJ interactifs et configuration via `shop.yml`.

### Corrigé
- Reconstruction complète du système de détection des lits pour une fiabilité à 100% en synchronisant la sauvegarde et la lecture des positions.
- Correction d'un bug où les PNJ n'étaient pas gérés par arène.
- L'orientation des PNJ au spawn est désormais normalisée.

## [0.3.1] - En développement

### Corrigé
- Refactorisation complète du système de détection des lits avec un cache de blocs pour une fiabilité à 100%.
- Correction de la régression où l'écran de mort réapparaissait lors de l'élimination finale.
- Correction de la condition de victoire qui ne se déclenchait pas à la fin de la partie.

## [0.3.0] - En développement

### Ajouté
- Implémentation des conditions de victoire et de la séquence de fin de partie. Le cycle de jeu de l'Étape 2 est maintenant complet.

## [0.2.1] - En développement

### Ajouté
- Amélioration complète du cycle de mort/réapparition (void kill, timer visuel, rééquipement)
- Ajout de boutons de retour pour la navigation dans les menus d'administration
- Amélioration : les joueurs en mode spectateur sont désormais téléportés sur leur île d'équipe.

### Corrigé
- Correction d'un bug critique où l'écran de mort de Minecraft apparaissait lors de l'élimination finale.
- Refactorisation majeure des listeners de jeu pour corriger les bugs de lits et de morts, avec ajout d'un logging de débogage.
- Correction d'un bug critique qui empêchait la destruction des lits en raison d'une mauvaise détection d'équipe.
- Correction définitive du bug de destruction des lits via une détection par distance.
- La réapparition personnalisée se déclenche désormais pour toutes les morts, y compris environnementales.
- La mort dans le vide est maintenant instantanée (hauteur configurable via `void-kill-height`).
- Correction d'une erreur de compilation liée à l'initialisation du `SetupListener`.
- Les lits détruits ne drop plus d'item.

## [0.2.0] - En développement

### Ajouté
- Système de menus paginés avec classe de base `PaginatedMenu` et refonte du menu des générateurs.
- Design unifié de toutes les interfaces avec bordures en vitres grises et lores colorées.
- Utilitaire `MessageUtils` centralisant l'envoi de messages avec préfixe et codes couleurs.
- Commandes `/bw join` et `/bw leave` pour les joueurs.
- Lobby d'attente avec décompte et barre d'expérience en progression.
- Lancement de la partie avec téléportation des joueurs et démarrage des générateurs.
- Système de vitesse de génération des ressources configurable par type et par niveau.
- Mécaniques de mort, réapparition et destruction des lits.

### Amélioré
- L'orientation des joueurs au spawn est désormais normalisée pour qu'ils regardent toujours droit devant.
- L'orientation de la vue est également normalisée lors de l'apparition dans le lobby d'attente.

## [0.1.2] - En développement

### Ajouté
- Réécriture complète du système de création d'arène via saisie par chat, remplaçant l'ancien menu d'enclume instable.
- Menu de gestion des arènes existantes permettant de configurer chaque arène.
- Système centralisé `SetupManager`/`SetupListener` pour définir les positions en jeu via un bâton de configuration.
- Menus de configuration pour le lobby, les équipes (spawns & lits), les générateurs et les PNJ.
- Possibilité d'ajouter ou de supprimer des générateurs via le GUI.
- Activation/désactivation d'une arène seulement lorsque tous les points requis sont définis.

## [0.1.1] - En développement

### Corrigé
- Suppression définitive de l'avertissement de dépréciation récurrent dans `ArenaNameMenu.java` pour assurer un build 100% propre.
- Correction d'un bug critique où le wizard de création d'arène ne continuait pas après la saisie du nom dans l'enclume.
- Correction d'un bug critique où la validation du nom d'arène échouait systématiquement, empêchant toute création.
- Correction définitive d'une régression critique où le wizard de création d'arène se bloquait silencieusement après la saisie du nom.
- Correction majeure et définitive du wizard de création d'arène, résolvant le blocage silencieux et le bug du coût en XP.

## [0.0.1] - En développement

### Ajouté
- Initialisation de la structure du projet Maven pour Spigot 1.21.
- Création des modèles de données de base : `Arena`, `Team`, `Generator`.
- Ajout des énumérations `GameState`, `GeneratorType`, et `TeamColor`.
- Implémentation de la structure initiale de l' `ArenaManager` pour la gestion des arènes en mémoire.
- Commande `/bedwars admin` (alias: `/bw`, `/hbw`) avec permission `heneriabw.admin`.
- Interface graphique (GUI) principale pour l'administration.
- Gestionnaire de commandes pour supporter de futures sous-commandes.
- Menu de gestion pour lister toutes les arènes existantes.
- Menu de configuration complet par arène.
- Outil de positionnement en jeu pour définir les emplacements (lobby, lits, spawns, etc.).
- Configuration des équipes (lits et spawns) via GUI.
- Ajout et suppression des générateurs de ressources via GUI.
- Configuration des emplacements des PNJ (boutiques) via GUI.
- Possibilité d'activer une arène une fois sa configuration terminée.
- **L'ensemble des fonctionnalités de l'Étape 1 de la roadmap est désormais implémenté.**

### Corrigé
- Avertissement de compilation Maven en remplaçant les flags `<source>`/`<target>` par `<release>` pour une meilleure compatibilité avec le JDK 21.
- Correction d'une erreur de compilation critique dans `ArenaNameMenu` due à une mauvaise importation de `InventoryType`.
- Suppression d'un avertissement de dépréciation lié à `getRenameText()` dans le GUI de l'enclume.
- Suppression finale de l'avertissement de dépréciation dans `ArenaNameMenu` qui avait été manqué lors du correctif précédent.
- Correction d'une erreur de compilation due à la méthode `addLore(String)` manquante dans `ItemBuilder`.
- Réapplication du correctif pour l'avertissement de dépréciation récurrent dans `ArenaNameMenu`.
