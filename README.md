# Mahery Foot — Gestion de tournois sportifs

Application **Spring Boot** (L3 — Framework Java) pour créer, planifier et suivre des compétitions
sportives : championnats (aller-retour ou match unique) et coupes à élimination directe.

---

## 1. Stack technique

| Couche | Technologie |
| --- | --- |
| Framework | Spring Boot **4.1.1** (Spring MVC, Spring Security, Spring Data JPA) |
| Vue | Thymeleaf + Bootstrap 5 |
| Persistance | Hibernate / JPA — base **H2** fichier `./data/tournoi` (H2 en mémoire pour les tests) |
| Authentification | Spring Security, comptes stockés en base (BCrypt) |
| Tests | JUnit 5, AssertJ, MockMvc, `spring-security-test` |
| Build | Maven (`./mvnw`) |

---

## 2. Compilation et lancement

```bash
# Lancer la suite de tests
./mvnw clean test

# Construire le jar exécutable
./mvnw clean package

# Démarrer l'application
java -jar target/tournoi-0.0.1-SNAPSHOT.jar
# ou, en développement :
./mvnw spring-boot:run
```

L'application écoute sur **http://localhost:8082**

* Accueil : <http://localhost:8082/>
* Console H2 (URL JDBC : `jdbc:h2:file:./data/tournoi`) : <http://localhost:8082/h2-console>

Au premier démarrage, un jeu de données de démo est créé (8 équipes, 32 joueurs, 3 tournois)
ainsi que les comptes utilisateurs ci-dessous. Les données suivantes ne sont pas réinitialisées.

---

## 3. Comptes utilisateurs

| Utilisateur | Mot de passe | Rôles |
| --- | --- | --- |
| `admin` | `admin123` | ADMIN |
| `manager` | `manager123` | MANAGER |
| `operator` | `operator123` | OPERATOR |
| `user` | `user123` | USER |
| `demo` | `demo123` | ADMIN, MANAGER, OPERATOR |

Répartition des droits :

* **ADMIN / MANAGER** : création et modification des tournois, des équipes et des joueurs,
  inscription/désinscription des équipes, génération du calendrier.
* **OPERATOR / MANAGER / ADMIN** : saisie des résultats et replanification des matchs.
* **USER** : consultation seule (interfaces et API).
* Les pages `/`, `/login`, les fichiers statiques sont publics.

Les règles sont déclarées dans `SecurityConfig` (HTTP) **et** via `@PreAuthorize` sur les
points d'entrée de l'API ; l'interface masque les boutons inaccessibles avec `sec:authorize`.

---

## 4. Fonctionnalités

### 4.1 Tournois
* Types : `CHAMPIONNAT_SIMPLE`, `CHAMPIONNAT_ALLER_RETOUR`, `ELIMINATION_DIRECTE`.
* Points paramétrables (victoire / nul / défaite), **chaîne de critères de classement
  configurable** et ordonnée : `POINTS`, `DIFFERENCE_BUTS`, `BUTS_MARQUES`,
  `CONFRONTATION_DIRECTE` (classement restreint des équipes à égalité).
* Statuts pilotés automatiquement : `BROUILLON → INSCRIPTION → PROGRAMME → EN_COURS → TERMINE`
  (transitions contrôlées, `ANNULE` possible).

### 4.2 Calendrier automatique (`util/MatchScheduler`)
* Round-robin (algorithme circulaire) pour les championnats, avec journées aller-retour.
* Créneaux de 2 h entre **09:00 et 20:00**, terrains définis dans le tournoi (champ `terrains`),
  passage au jour suivant quand les créneaux sont épuisés.
* **Aucun conflit** : une équipe ne joue jamais deux matchs au même moment et un terrain
  n'est jamais réservé deux fois sur le même créneau (contrôle `verifierConflits`
  + `ConflictException` en cas de replanification litigieuse).
* Le calendrier doit tenir dans les dates du tournoi (`dateDebut` … `dateFin`).

### 4.3 Élimination directe (`util/BracketUtil`)
* Arbre dimensionné à la puissance de 2 supérieure (byes pour les équipes en surnombre),
  interleaving des têtes de série.
* Les matchs des tours suivants sont **créés d'avance** (placeholders) : la qualification
  automatique remplit le match suivant au fil de l'eau.
* Phases : `PRELIMINAIRE`, `SEIZIEME`, `HUITIEME`, `QUART`, `DEMI`, `FINALE`,
  `TROISIEME_PLACE` (cohérentes avec le nombre de tours, y compris plus de 16 équipes).
* Scores : pas de nul en élimination, scores validés, et **impossible de changer un résultat
  dont le match suivant est déjà joué** (`ConflictException`).

### 4.4 Interfaces
* Tableau de bord avec indicateurs (tournois, équipes, matchs terminés / programmés).
* Listes **paginées, triées et filtrées** (recherche, statut, taille de page) :
  tournois, équipes, matchs (global et par tournoi), classements (filtre tournoi).
* Fiche équipe : effectif, **statistiques** (J/G/N/P, buts, différence, points) et
  **historique des matchs**.
* Arbre d'élimination (`/tournaments/{id}/matches/bracket`), saisie de résultat, classements
  par tournoi et classements généraux.

---

## 5. API REST

Toutes les routes `/api/**` demandent une authentification ; les écritures respectent les rôles
(voir §3). Les erreurs renvoient du JSON (page HTML côté interfaces).

| Méthode | URL | Description |
| --- | --- | --- |
| `GET` | `/api/tournaments?page=0&size=10&sort=nom,asc&nom=&statut=` | Liste paginée + filtres |
| `POST` | `/api/tournaments` | Création (ADMIN, MANAGER) |
| `GET/PUT/DELETE` | `/api/tournaments/{id}` | Lecture / modification / suppression |
| `POST` | `/api/tournaments/{id}/teams?teamId=` | Inscription d'une équipe |
| `DELETE` | `/api/tournaments/{id}/teams/{teamId}` | Désinscription |
| `POST` | `/api/tournaments/{id}/generate-matches` | Génération du calendrier |
| `GET` | `/api/tournaments/{id}/matches?phase=` | Matchs du tournoi |
| `GET` | `/api/tournaments/{id}/ranking` | Classement |
| `GET` | `/api/teams?page=0&size=10&nom=&statut=` | Équipes paginées |
| `POST/PUT/DELETE` | `/api/teams[/{id}]` | CRUD équipes (ADMIN, MANAGER) |
| `POST` | `/api/teams/{id}/players` | Ajout d'un joueur |
| `GET` | `/api/matches?size=20&tournamentId=&phase=&statut=&date=&equipeId=` | Matchs paginés + filtres |
| `GET` | `/api/matches/{id}` | Un match |
| `PUT` | `/api/matches/{id}/result` | Saisie du score (OPERATOR+) |
| `PUT` | `/api/matches/{id}/schedule` | Replanification (OPERATOR+) |

Codes HTTP : `200/201/204` (succès), `400` (validation / métier), `403` (rôle insuffisant),
`404` (ressource inconnue), `409` (conflit : planning, arbre, doublon), `500` (erreur interne).

---

## 6. Tests

```bash
./mvnw clean test
```

| Classe | Couverture |
| --- | --- |
| `MatchGenerationTest` | Génération round-robin et arbre (3 à 32 équipes), byes, conflits de planning, dates |
| `RankingTest` | Classement dynamique, ordre des critères, validations des critères, statuts |
| `MatchFlowTest` | Qualification en élimination, interdiction du nul, verrouillage des résultats, replanification |
| `WebPagesTest` | Rendu de toutes les pages (filtres, tri, pagination), pages d'erreur, droits |
| `SecurityTest` | Connexion avec les comptes en base, rôles sur l'API |
| `RestApiTest` | Pagination, filtres, validations et codes HTTP de l'API |
| `TournoiApplicationTests` | Démarrage du contexte Spring |

Les tests utilisent une base H2 en mémoire (`src/test/resources/application.properties`) :
le fichier de données de démo n'est jamais modifié par les tests.

---

## 7. Structure du projet

```
src/main/java/com/tpe/tournoi/
├── config/        DataInitializer (données de démo + comptes utilisateurs)
├── controller/
│   ├── rest/      API JSON (Tournament, Team, Match)
│   └── web/       Pages Thymeleaf + WebUiAdvice / WebExceptionHandler
├── dto/           Requêtes (validation Jakarta) et réponses
├── entity/        JPA (Tournament, Team, Player, TournamentMatch, Ranking, AppUser…)
├── exception/     TournoiException et spécialisations + GlobalExceptionHandler (API)
├── mapper/        Entité ↔ DTO
├── repository/    Spring Data JPA (requêtes nommées, pagination, conflits)
├── security/      SecurityConfig, CustomUserDetailsService
├── service/       Interfaces + implémentations (métier, transactions)
├── util/          RankingUtil, MatchScheduler, BracketUtil, FilterParams
└── src/main/resources/templates/   Vues Thymeleaf (+ fragments partagés)
```
