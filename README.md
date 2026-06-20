# Backrooms

## Setup

For setup instructions, please see the [Fabric Documentation page](https://docs.fabricmc.net/develop/getting-started/creating-a-project#setting-up) related to the IDE that you are using.

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.

# 🟨 Backrooms - Fabric Mod

Un générateur de dimension procédurale pour Minecraft (Fabric 1.21). Ce mod ne se contente pas d'empiler des blocs : il utilise un moteur mathématique sur mesure pour générer un cauchemar géométrique infini, alternant entre labyrinthes claustrophobiques et open-spaces massifs, le tout parsemé de mégastructures.

## ✨ Fonctionnalités Principales

### 🗺️ Génération Procédurale Avancée
* **Moteur Cellulaire & Worley Noise :** Détermine dynamiquement l'espacement et la fusion des zones (Open-Space vs Maze) de manière 100% déterministe.
* **Arbre Binaire :** Logique de routage des murs (`WallRouter`) garantissant des chemins toujours connectés sans culs-de-sac impossibles.
* **Mégastructures :** Détection algorithmique sur une grille de 32x32 chunks (`MegaManager`) pour faire spawn des anomalies géantes (ex: Poolrooms) avec un taux d'apparition contrôlé.

### 🧱 Blocs & Matériaux
* **Plafond de Bureau :** Hitbox 3D creusée sur mesure pour un rendu réaliste des dalles de faux plafond.
* **Moquette Humide & Papier Peint :** Intégration complète avec les outils du jeu (Tags de minage spécifiques générés dynamiquement).

## 💻 Commandes Administrateur & Outils de Dev

Ce mod embarque une suite de commandes sur mesure pour manipuler la génération procédurale, tester les structures et débugger l'environnement sans avoir à charger des millions de chunks.

### `/tpdim <dimension>`
* **Description :** Téléportation inter-dimensionnelle de sécurité.
* **Comportement :** Au lieu de vous lâcher aléatoirement, la commande force l'atterrissage à la coordonnée `Y=72`. Cela évite de réapparaître au-dessus du plafond de bedrock ou de tomber dans le vide lors des tests de génération.

### `/map_backrooms <rayon>`
* **Description :** Scanner topographique.
* **Comportement :** Génère et exporte une image PNG dans le dossier du jeu. Elle cartographie la zone autour du joueur de manière algorithmique (sans générer les blocs en jeu).
* **Code couleur :**
    * ⬜ Blanc : Open-Spaces.
    * ⬛ Noir : Labyrinthes (Mazes).
    * 🟥 Rouge : Emplacements des Mégastructures.

### `/locate_mega <nom_structure>`
* **Description :** Radar mathématique.
* **Comportement :** Calcule la position de la mégastructure demandée la plus proche en interrogeant directement le `MegaManager`. L'opération est instantanée car elle repose sur la seed et le bruit procédural, sans charger les chunks.

### `/place_mega <nom_structure>`
* **Description :** Moteur d'assemblage manuel.
* **Comportement :** Instancie la structure NBT ciblée directement aux coordonnées actuelles du joueur. Calcule automatiquement l'enfoncement en Y selon le nombre d'étages de la structure. Parfait pour tester l'éclairage et l'agencement sans explorer.
### `/slice_room <nom> [taille_x taille_y taille_z]`
* **Description :** L'outil de génération de Mégastructures par excellence. Il découpe une zone de l'Overworld, analyse ses bordures intelligemment, et génère les fichiers `.nbt` compressés prêts à être injectés dans le mod.
* **Mécanique d'analyse :**
    * **Grille de découpe :** Divise la zone en sous-matrices de 16x8x16 blocs, en s'alignant automatiquement sur un multiple de Y=8.
    * **Raycasting des bordures :** La commande scanne les blocs de chaque face (Nord, Sud, Est, Ouest) pour générer la signature de connexion :
        * 🪟 **Verre (`Blocks.GLASS`) :** Force une ouverture totale de la face (signature `open`).
        * 🪨 **Bedrock (`Blocks.BEDROCK`) :** Marque les coordonnées exactes comme point de passage (ex: signature `7x1-8x1` pour une double porte).
        * 🧱 **Rien :** Mur plein, aucun passage (signature `0`).
    * **Nettoyage automatique :** Les blocs de balisage (Verre et Bedrock) sont effacés et remplacés par de l'air avant l'exportation pour ne pas polluer la structure finale.
    * **Exportation typée :** Sauvegarde le fichier directement dans `generated/backrooms/structures/` avec le format de nommage strict attendu par le routeur, par exemple : `poolroom_0_0_0_n_open_s_0_e_7x1-8x1_o_0.nbt`.

## 🏗️ Architecture & Code Standard

Ce mod est construit avec une architecture modulaire stricte (Responsabilité Unique) pour éviter l'effet "God Class" du `ChunkGenerator` :

* `LayoutProvider` : Topographie mathématique pure.
* `WallRouter` : Gestionnaire de l'état des bordures (ouvert/fermé).
* `StructurePlacer` : Moteur d'assemblage 3D des NBT.
* `MegaManager` : Gestionnaire de grille haute performance.

### 🏭 Fabric Datagen
Zéro fichier JSON écrit à la main. Tous les modèles (Blockstates, Models), Loot Tables (Drops) et Tags (Outils requis) sont générés à la compilation via l'API `FabricDataGenerator`.

### 🧪 Tests Unitaires (JUnit 5)
Le moteur mathématique est blindé par des tests unitaires garantissant :
* Le déterminisme absolu de la génération.
* La viabilité des probabilités de spawn sur des millions de chunks.
* La robustesse du parser de noms de fichiers NBT via Regex.

## 🚀 Installation & Build

**Prérequis :** Java 21

```bash
# Cloner le dépôt
git clone [https://github.com/ton-pseudo/backrooms-mod.git](https://github.com/ton-pseudo/backrooms-mod.git)
cd backrooms-mod

# Générer les fichiers JSON (Datagen)
./gradlew runDatagen

# Lancer les tests unitaires
./gradlew test

# Compiler le mod
./gradlew build
```

## 🤖 Documentation IA (Contexte du projet)

Pour une assistance optimale lors du développement ou de l'intégration de nouvelles fonctionnalités, voici le contexte technique du projet **Backrooms** :

### Stack Technique
* **Language :** Java 21
* **Framework :** Fabric Loader (Minecraft 1.21)
* **Build System :** Gradle avec `fabric-loom`
* **Génération :** Procédurale déterministe (Worley Noise, Arbre Binaire)
* **Architecture :** Modulaire (Responsabilité Unique). Chaque composant (génération, structure, routage) est isolé.

### Principes de Développement
1. **Datagen First :** Aucun fichier JSON (`models`, `tags`, `loots`, `lang`) n'est écrit manuellement. Tout passe par `FabricDataGenerator`.
2. **Robustesse par les Tests (TU) :** La logique mathématique (spawn des mégastructures, routage des murs) est testée via JUnit 5. Aucun code métier critique ne doit être ajouté sans son test unitaire associé.
3. **Déterminisme :** La génération doit être 100% reproductible via la seed. Pas d'aléatoire non-déterministe dans le `ChunkGenerator`.
4. **CI/CD :** Le build est validé automatiquement via GitHub Actions à chaque `push`.

### Règles d'interaction avec le code
* **Ne pas modifier le `ChunkGenerator` manuellement :** Tout comportement doit être délégué aux services (`LayoutProvider`, `WallRouter`, `StructurePlacer`).
* **Priorité aux logs :** En cas d'erreur de génération de pièce, utiliser le `Backrooms.LOGGER` pour tracer les coordonnées et les contraintes de bordure manquantes.
* **Sécurité :** Toute modification de la logique de spawn doit impérativement passer par le test `MegaManagerTest` pour vérifier que le taux de spawn reste cohérent (ratio ~20%).
