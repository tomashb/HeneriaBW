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
- Assistant de création d'arène en plusieurs étapes (GUI).
- Système de persistance des données : les arènes sont désormais sauvegardées dans des fichiers YAML.
- Chargement automatique des arènes au démarrage du serveur.
- Menu de base pour lister les arènes existantes.

### Corrigé
- Avertissement de compilation Maven en remplaçant les flags `<source>`/`<target>` par `<release>` pour une meilleure compatibilité avec le JDK 21.
