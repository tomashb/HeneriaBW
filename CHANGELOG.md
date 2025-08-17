# Changelog - HeneriaBedwars

## [0.0.1] - En développement

### Ajouté
- Initialisation de la structure du projet Maven pour Spigot 1.21.
- Création des modèles de données de base : `Arena`, `Team`, `Generator`.
- Ajout des énumérations `GameState`, `GeneratorType`, et `TeamColor`.
- Implémentation de la structure initiale de l' `ArenaManager` pour la gestion des arènes en mémoire.
- Commande `/bedwars admin` (alias: `/bw`, `/hbw`) avec permission `heneriabw.admin`.
- Interface graphique (GUI) principale pour l'administration.
- Gestionnaire de commandes pour supporter de futures sous-commandes.

### Corrigé
- Avertissement de compilation Maven en remplaçant les flags `<source>`/`<target>` par `<release>` pour une meilleure compatibilité avec le JDK 21.
- Correction d'une erreur de compilation critique dans `ArenaNameMenu` due à une mauvaise importation de `InventoryType`.
- Suppression d'un avertissement de dépréciation lié à `getRenameText()` dans le GUI de l'enclume.
- Suppression finale de l'avertissement de dépréciation dans `ArenaNameMenu` qui avait été manqué lors du correctif précédent.
