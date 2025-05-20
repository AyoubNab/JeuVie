# 🌍 LLM World Sim - Une Expérience d'Évolution Sociétale IA 🤖

Bienvenue dans LLM World Sim ! Ce projet ambitieux est un **jeu de simulation** conçu pour être un laboratoire d'observation pour les **Modèles de Langage Étendus (LLM)**. Imaginez un monde virtuel où des intelligences artificielles ne sont pas de simples PNJ, mais les **protagonistes principaux**, interagissant, évoluant et peut-être même construisant des civilisations.

L'objectif fondamental est d'implémenter des LLM comme agents autonomes au sein de cet univers et d'étudier leurs interactions complexes, leurs prises de décision et l'émergence de dynamiques sociales.

## 🎯 Le But de l'Expérience

Nous cherchons à répondre à des questions fascinantes :
*   Comment des IA dotées d'une certaine autonomie vont-elles **évoluer ensemble** ?
*   Verrons-nous l'émergence de **structures sociales** ?
*   Les IA tendront-elles vers des modèles économiques identifiables (ex: **capitaliste, communiste, coopératif**) ?
*   Quelles stratégies de survie, de collaboration ou de conflit vont-elles développer ?

Une fois la simulation mature, nous pourrons analyser les données collectées pour tirer des conclusions sur le comportement des IA en environnement complexe et multi-agents.

## 🚀 Fonctionnalités des "Joueurs" IA

Les entités IA (nos "joueurs") auront un large éventail d'interactions possibles avec le monde et entre elles, permettant d'observer toute la complexité de leurs relations :

*   **🗺️ Se Déplacer** : Explorer le monde, case par case.
*   **💬 Communiquer** : Échanger des informations, négocier, former des alliances (ou des rivalités !).
*   **🏹 Chasser & Collecter** : Trouver des ressources pour survivre.
*   **🍎 Se Nourrir & 💧 S'Hydrater** : Gérer leurs besoins vitaux.
*   **🔨 Construire** : Ériger des abris, des outils, ou d'autres structures.
*   **💰 Dérober (Voler)** : Tenter de prendre des objets ou ressources à d'autres entités.
*   **⚔️ Frapper & Se Combattre** : Se défendre, attaquer d'autres entités, entrer en conflit pour des ressources ou des territoires.
*   **🤝 S'Allier** : Former des groupes pour atteindre des objectifs communs, coopérer ou se protéger.
*   **👶 Avoir des Enfants** : Se reproduire et assurer la survie de leur lignée.
*   **🛡️ Protéger** : Veiller sur leur progéniture ou leurs alliés.

## ⚙️ Comment ça Marche ?

1.  **Le Monde (<code>Monde.java</code>, <code>Block.java</code>)** :
    *   Un environnement en grille 2D composé de `Block`.
    *   Chaque `Block` a des propriétés (traversable, constructible, destructible) et peut contenir des `Entite`.
    *   Les bords du monde sont initialisés comme non traversables.

2.  **Les Entités (<code>Entite.java</code>, <code>Humain.java</code>, <code>Animaux.java</code>)** :
    *   Classe de base `Entite` gérant la position, les statistiques vitales (faim, soif, énergie, âge) et les actions de base (manger, se déplacer).
    *   Les entités ont un cycle de vie : elles peuvent mourir de faim, de soif, ou d'autres causes.
    *   Les `Humain` (et potentiellement d'autres sous-classes) seront les candidats pour être contrôlés par les LLM.

3.  **Interaction des LLM** :
    *   Les IA interagiront avec le jeu via un système de **commandes textuelles simples** (par exemple : `<manger pomme>`, `<deplacer nord>`, `<construire abri_bois>`, `<communiquer "Besoin d'aide" joueur_2>`).
    *   Le jeu interprétera ces commandes et mettra à jour l'état du monde et de l'IA concernée.
    *   Les LLM recevront des informations sur leur état et leur environnement pour prendre leurs décisions.

4.  **Besoins et Survie** :
    *   `faimTick` et `soifTick` : Déterminent à quelle vitesse une entité a faim et soif.
    *   L'énergie est affectée par les actions (ex: `coutDeplacement`).
    *   La survie dépendra de la capacité de l'IA à gérer ses besoins et à interagir efficacement avec son environnement et les autres.

## 🏗️ Structure Actuelle du Code (Java)

*   **<code>Package Monde</code>**
    *   `Monde.java`: Gère la grille du monde et sa taille. Initialise les `Block`.
    *   `Block.java`: Représente une case du monde. Contient une liste d'`Entite` et des booléens pour `estTraversable`, `estConstructible`, `estDestructible`.
*   **<code>Package Entite</code>**
    *   `Entite.java`: Classe abstraite pour toutes les créatures. Gère :
        *   Position (X, Y) et le `Block` actuel.
        *   Statistiques : `faim`, `soif`, `energie`, `age`, `vivant`.
        *   Métriques : `faimTick`, `soifTick`, `coutDeplacement`.
        *   Méthodes : `prochainTick()` (mise à jour des stats), `manger(Aliment, quantite)`, `deplacerHaut/Bas/Gauche/Droite()`.
    *   **<code>Package Entite.Heritage</code>**
        *   `Humain.java`: Sous-classe d'`Entite`, pourrait avoir un `Inventaire`. Prévu pour être contrôlé par un LLM.
        *   `Animaux.java`: Autre sous-classe d'`Entite`.

## 🛠️ Technologies Utilisées

*   **Langage principal** : Java ☕
*   **Futur** : Intégration avec des APIs de LLM (ex: OpenAI, Hugging Face, modèles locaux).

## 🗺️ Feuille de Route (Roadmap)

*   [ ] **Phase 1 : Fondations (En cours)**
    *   [x] Système de monde et de blocks.
    *   [x] Système d'entités avec besoins basiques (faim, soif, énergie) et déplacement.
    *   [ ] Implémentation des actions de base (manger, attaquer simple).
*   [ ] **Phase 2 : Intégration LLM**
    *   [ ] Définir un protocole de communication Jeu <-> LLM.
    *   [ ] Interface de commandes pour les LLM.
    *   [ ] Intégrer un premier LLM capable d'actions simples.
    *   [ ] Fournir aux LLM des informations sensorielles sur leur environnement.
*   [ ] **Phase 3 : Complexification des Interactions**
    *   [ ] Système de communication entre IA.
    *   [ ] Mécaniques de construction avancées.
    *   [ ] Système de reproduction et de gestion des enfants.
    *   [ ] Alliances et conflits.
*   [ ] **Phase 4 : Observation & Analyse**
    *   [ ] Outils de logging et de visualisation des comportements.
    *   [ ] Analyse des données pour identifier les modèles émergents.

## 💡 Idées & Contributions

Ce projet est une toile vierge pour l'étude fascinante de l'intelligence artificielle en société.
Si vous avez des idées, des suggestions, ou si vous souhaitez contribuer, n'hésitez pas à :
*   Ouvrir une **Issue** pour discuter d'une fonctionnalité ou signaler un bug.
*   Proposer une **Pull Request** avec vos améliorations.
