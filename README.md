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

### 🛠️ Outils de Développement & Commandes
Une suite d'outils in-game pour cartographier et débugger la génération :
* `/map_backrooms <rayon>` : Exporte une carte PNG topographique de la dimension.
* `/locate_mega <nom>` : Radar algorithmique pointant vers la mégastructure la plus proche.
* `/place_mega <nom>` : Assemble instantanément une mégastructure via les NBT.
* `/slice_room <nom> <x> <y> <z>` : Découpe l'environnement et génère les fichiers `.nbt` automatiquement.
* `/tpdim <dimension>` : Téléportation sécurisée (Y=72) inter-dimensions.

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
