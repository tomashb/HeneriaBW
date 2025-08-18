# Changelog - HeneriaBedwars

## [0.5.1] - En développement

### Corrigé
- Correction d'erreurs de compilation dues à la mise à jour des noms de constantes de l'API Spigot 1.21.

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
