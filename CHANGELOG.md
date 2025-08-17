# Changelog - HeneriaBedwars

## [0.1.1] - En développement

### Corrigé
- Suppression définitive de l'avertissement de dépréciation récurrent dans `ArenaNameMenu.java` pour assurer un build 100% propre.
- Correction d'un bug critique où le wizard de création d'arène ne continuait pas après la saisie du nom dans l'enclume.

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
