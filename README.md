# Système de Gestion des Cours Particuliers

Application web complète pour la gestion des cours particuliers entre professeurs, élèves et administrateurs.

## Stack technique

| Composant | Technologie |
|-----------|-------------|
| Backend | Spring Boot 3.2, Java 21, Maven |
| Base de données | MySQL 8 |
| Frontend | React 18 + Vite + Bootstrap 5 |
| Sécurité | Spring Security + JWT + Refresh Token |
| ORM | Spring Data JPA / Hibernate |
| API Docs | Swagger / OpenAPI 3 |
| Notifications | JavaMailSender + notifications in-app |

## Arborescence du projet

```
├── backend/                    # API REST Spring Boot
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/coursparticuliers/
│       │   ├── config/         # Security, OpenAPI, DataInitializer
│       │   ├── controller/   # REST Controllers
│       │   ├── dto/            # Request/Response DTOs
│       │   ├── entity/         # Entités JPA
│       │   ├── exception/      # Gestion centralisée des erreurs
│       │   ├── mapper/         # Entity → DTO
│       │   ├── repository/     # Spring Data JPA
│       │   ├── security/       # JWT Filter & Service
│       │   └── service/        # Logique métier
│       └── resources/
│           └── application.yml
├── frontend/                   # Application React
│   ├── src/
│   │   ├── api/                # Client Axios + intercepteurs JWT
│   │   ├── components/         # Layout, DataTable, Pagination
│   │   ├── context/            # AuthContext
│   │   └── pages/              # Pages CRUD + Dashboards
│   └── package.json
├── database/
│   └── schema.sql              # Script SQL MySQL complet
└── docs/
    └── diagramme-classes.md    # UML + relations JPA
```

## Prérequis

- **Java 21** (JDK)
- **Maven 3.8+**
- **MySQL 8+**
- **Node.js 18+** et npm

## Installation

### 1. Base de données MySQL

```bash
mysql -u root -p < database/schema.sql
```

Ou laissez Hibernate créer les tables automatiquement (`ddl-auto: update`).

### 2. Configuration backend

Modifiez `backend/src/main/resources/application.yml` :

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cours_particuliers
    username: root
    password: VOTRE_MOT_DE_PASSE
  mail:
    username: votre-email@gmail.com
    password: votre-app-password

app:
  jwt:
    secret: VotreCleSecreteTresLongue256BitsMinimum
```

Variables d'environnement optionnelles : `MAIL_USERNAME`, `MAIL_PASSWORD`, `JWT_SECRET`.

### 3. Lancer le backend

```bash
cd backend
mvn spring-boot:run
```

- API : http://localhost:8080
- Swagger UI : http://localhost:8080/swagger-ui.html
- API Docs : http://localhost:8080/api-docs

### 4. Lancer le frontend

```bash
cd frontend
npm install
npm run dev
```

- Application : http://localhost:5173

## Compte administrateur par défaut

| Email | Mot de passe |
|-------|--------------|
| `admin@coursparticuliers.fr` | `admin123` |

## API REST

| Endpoint | Description |
|----------|-------------|
| `POST /api/auth/login` | Connexion JWT |
| `POST /api/auth/refresh` | Refresh token |
| `GET/POST/PUT/DELETE /api/users` | CRUD utilisateurs (Admin) |
| `GET /api/professeurs` | Liste professeurs |
| `GET /api/eleves` | Liste élèves |
| `GET/POST/PUT/DELETE /api/cours` | CRUD cours |
| `GET/POST/PUT/DELETE /api/plannings` | Gestion disponibilités |
| `GET/POST /api/reservations` | Réservations |
| `PATCH /api/reservations/{id}/confirmer` | Confirmer |
| `PATCH /api/reservations/{id}/annuler` | Annuler |
| `GET/POST /api/seances` | Séances |
| `GET/POST/PUT/DELETE /api/evaluations` | Évaluations |
| `GET /api/notifications/utilisateur/{id}` | Notifications |
| `GET /api/dashboard/admin` | Dashboard admin |
| `GET /api/dashboard/professeur/{id}` | Dashboard professeur |
| `GET /api/dashboard/eleve/{id}` | Dashboard élève |

Toutes les routes (sauf auth et consultation publique) nécessitent un header :

```
Authorization: Bearer <accessToken>
```

## Fonctionnalités

### Administrateur
- CRUD utilisateurs (professeurs, élèves)
- Activer/désactiver les comptes
- Statistiques globales

### Professeur
- Gérer ses cours et disponibilités
- Confirmer les réservations
- Marquer les séances comme réalisées
- Consulter revenus et évaluations

### Élève
- Parcourir le catalogue de cours
- Réserver un créneau disponible
- Consulter historique et évaluer les professeurs

### Notifications
- Email de confirmation/annulation de réservation
- Rappel automatique 24h avant la séance (cron 8h)
- Notifications in-app

## Build production

```bash
# Backend
cd backend && mvn clean package -DskipTests

# Frontend
cd frontend && npm run build
```

Le JAR sera dans `backend/target/cours-particuliers-api-1.0.0.jar`.

## Licence

Projet éducatif — libre d'utilisation.
