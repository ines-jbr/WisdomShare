# WisdomShare 📚
> Application web de partage de livres

## Table des matières
- [Description](#description)
- [Technologies](#technologies)
- [Prérequis](#prérequis)
- [Lancement des services Docker](#lancement-des-services-docker)
- [Configuration de Keycloak](#configuration-de-keycloak)
- [Lancement du Backend](#lancement-du-backend)
- [Lancement du Frontend](#lancement-du-frontend)
- [Accès à l'application](#accès-à-lapplication)
- [Notes importantes](#notes-importantes)

---

## Description

WisdomShare est une application web de partage de livres permettant aux utilisateurs de :
- 📖 Publier et partager des livres
- 🤝 Emprunter des livres d'autres utilisateurs
- ⭐ Laisser des avis et notes sur les livres
- 📋 Gérer leurs emprunts et retours

---

## Technologies

| Couche | Technologies |
|--------|-------------|
| Backend | Java 25, Spring Boot 3.4.5, Spring Security OAuth2, Spring Data JPA, PostgreSQL |
| Frontend | Angular 19+, Keycloak JS, Bootstrap 5, Font Awesome |
| Auth | Keycloak 24 |
| Infrastructure | Docker, Docker Compose, MailDev |
| Documentation | SpringDoc OpenAPI (Swagger UI) |

---

## Prérequis

Assurez-vous d'avoir installé les outils suivants :

- **Java JDK 25** — https://openjdk.org/
- **Maven 3.8+** — https://maven.apache.org/
- **Node.js 20+** — https://nodejs.org/
- **Angular CLI** — `npm install -g @angular/cli`
- **Docker Desktop** — https://www.docker.com/products/docker-desktop/

Vérification des versions :
```bash
java -version
mvn -version
node -version
ng version
docker -version
```

---

## Lancement des services Docker

> ⚠️ **Docker doit être lancé AVANT le backend Spring Boot.**

Depuis la racine du projet :

```bash
docker compose up -d
```

Vérifier que les conteneurs sont actifs :

```bash
docker ps
```

Les conteneurs suivants doivent être en cours d'exécution :

| Conteneur | Service | Port |
|-----------|---------|------|
| `postgres-sql-bsn` | PostgreSQL | 5432 |
| `keycloak-bsn` | Keycloak | 9090 |
| `mail-dev-bsn` | MailDev | 1080 / 1025 |

Identifiants PostgreSQL :
- **Base de données** : `book_social_network`
- **Utilisateur** : `username`
- **Mot de passe** : `password`

Pour arrêter les services :
```bash
docker compose down
```

---

## Configuration de Keycloak

> ⚠️ Cette étape est à effectuer **une seule fois** après le premier démarrage de Docker.

1. Ouvrir **http://localhost:9090** et se connecter avec `admin` / `admin`

2. **Créer un Realm :**
    - Cliquer sur "Create realm"
    - Nom : `wisdomshare`
    - Cliquer sur "Create"

3. **Créer un Client :**
    - Aller dans Clients → "Create client"
    - Client ID : `wisdomshare`
    - Client type : `OpenID Connect`
    - Activer "Standard flow"
    - Cliquer sur "Save"

4. **Configurer le Client :**
    - Client authentication : **OFF** (client public)
    - Valid redirect URIs : `http://localhost:4200/*`
    - Web origins : `http://localhost:4200`
    - Cliquer sur "Save"

5. **Créer un utilisateur de test :**
    - Aller dans Users → "Create new user"
    - Remplir les champs requis
    - Onglet "Credentials" → définir un mot de passe, désactiver "Temporary"

> 💡 Le nom du realm `wisdomshare` doit correspondre exactement à la valeur dans `application-dev.yml` :
> ```
> spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9090/realms/wisdomshare
> ```

---

## Lancement du Backend

Le backend se trouve dans le dossier `wisdomshare-backend/`.

### Avec IntelliJ IDEA (recommandé)

1. Ouvrir le projet dans IntelliJ IDEA
2. Attendre que Maven télécharge les dépendances
3. S'assurer que Docker est démarré
4. Lancer la classe `com.wisdomshare.WisdomshareBackendApplication`
5. Vérifier dans les logs : `Started WisdomshareBackendApplication`

### Avec Maven en ligne de commande

```bash
cd wisdomshare-backend
mvn clean install -DskipTests
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

> ⚠️ Le profil `dev` est **obligatoire** pour charger `application-dev.yml`.

Configuration active (`application-dev.yml`) :
- Port : `8088`
- Base de données : `jdbc:postgresql://localhost:5432/book_social_network`
- Keycloak : `http://localhost:9090/realms/wisdomshare`

---

## Lancement du Frontend

Le frontend se trouve dans le dossier `wisdomshare-ui/`.

### Installation des dépendances (une seule fois)

```bash
cd wisdomshare-ui
npm install
```

### Lancer le serveur de développement

```bash
ng serve
```

L'application sera accessible sur **http://localhost:4200**

> ⚠️ Le backend et Docker doivent être démarrés **avant** le frontend. Angular initialise Keycloak au démarrage et redirige immédiatement vers la page de connexion.

---

## Accès à l'application

| Service | URL |
|---------|-----|
| Application Web | http://localhost:4200 |
| API REST | http://localhost:8088/api/v1 |
| Swagger UI | http://localhost:8088/api/v1/swagger-ui/index.html |
| Keycloak Admin | http://localhost:9090 |
| MailDev | http://localhost:1080 |

---

## Notes importantes

### ✅ Ordre de démarrage obligatoire

```
1. docker compose up -d
2. Attendre ~15 secondes (Keycloak démarre lentement)
3. Lancer le backend Spring Boot
4. Lancer le frontend : ng serve
```

### 🗄️ Base de données
- La base est créée automatiquement par Docker au premier lancement
- Les tables sont générées automatiquement par Hibernate (`ddl-auto=update`)
- Aucun script SQL n'est nécessaire

### 🔐 Keycloak
- La configuration Keycloak est perdue si les volumes Docker sont supprimés
- Ne **pas** utiliser `docker compose down -v` pour conserver la configuration

### 📧 Emails
- Les emails sont interceptés par MailDev et visibles sur http://localhost:1080
- Aucun vrai email n'est envoyé en développement

### 🧪 Tester l'API avec Swagger
Pour tester les endpoints sécurisés via Swagger UI :
1. Se connecter sur http://localhost:4200 pour obtenir un token Keycloak
2. Ouvrir Swagger UI : http://localhost:8088/api/v1/swagger-ui/index.html
3. Cliquer sur "Authorize" et entrer : `Bearer <votre_token>`