# Diagramme de Classes UML

## Diagramme de classes (Mermaid)

```mermaid
classDiagram
    class Utilisateur {
        +Long id
        +String nom
        +String prenom
        +String email
        +String telephone
        +String motDePasse
        +Role role
        +boolean actif
        +LocalDateTime dateCreation
    }

    class Professeur {
        +Long id
        +String description
        +BigDecimal tarifHoraire
        +String photoProfil
        +List~String~ matieres
    }

    class Eleve {
        +Long id
        +String niveauScolaire
        +String adresse
    }

    class Cours {
        +Long id
        +String titre
        +String description
        +String matiere
        +String niveau
        +BigDecimal prix
        +LocalDateTime dateCreation
        +StatutCours statut
    }

    class Planning {
        +Long id
        +LocalDate date
        +LocalTime heureDebut
        +LocalTime heureFin
        +boolean disponible
    }

    class Reservation {
        +Long id
        +LocalDateTime dateReservation
        +StatutReservation statut
    }

    class Seance {
        +Long id
        +LocalDate date
        +LocalTime heureDebut
        +LocalTime heureFin
        +String compteRendu
        +StatutSeance statut
    }

    class Evaluation {
        +Long id
        +Integer note
        +String commentaire
        +LocalDateTime date
    }

    class Notification {
        +Long id
        +String message
        +LocalDateTime date
        +boolean lu
    }

    class RefreshToken {
        +Long id
        +String token
        +Instant dateExpiration
    }

    Utilisateur "1" -- "0..1" Professeur : profil
    Utilisateur "1" -- "0..1" Eleve : profil
    Utilisateur "1" -- "*" Notification : reçoit
    Utilisateur "1" -- "0..1" RefreshToken : possède

    Professeur "1" -- "*" Cours : propose
    Professeur "1" -- "*" Planning : définit
    Professeur "1" -- "*" Reservation : reçoit
    Professeur "1" -- "*" Evaluation : reçoit

    Eleve "1" -- "*" Reservation : effectue
    Eleve "1" -- "*" Evaluation : donne

    Cours "1" -- "*" Reservation : concerne

    Planning "0..1" -- "0..1" Reservation : réservé par

    Reservation "1" -- "*" Seance : génère
```

## Relations JPA

| Relation | Type | Description |
|----------|------|-------------|
| Utilisateur ↔ Professeur | OneToOne | Profil professeur |
| Utilisateur ↔ Eleve | OneToOne | Profil élève |
| Professeur → Cours | OneToMany | Un professeur propose plusieurs cours |
| Professeur → Planning | OneToMany | Disponibilités du professeur |
| Professeur → Reservation | OneToMany | Réservations reçues |
| Eleve → Reservation | OneToMany | Réservations effectuées |
| Cours → Reservation | OneToMany | Réservations pour un cours |
| Planning ↔ Reservation | OneToOne | Créneau réservé |
| Reservation → Seance | OneToMany | Séances d'une réservation |
| Eleve/Professeur → Evaluation | ManyToOne | Évaluations |
| Utilisateur → Notification | OneToMany | Notifications in-app |

## Diagramme entité-relation (simplifié)

```mermaid
erDiagram
    UTILISATEURS ||--o| PROFESSEURS : "est"
    UTILISATEURS ||--o| ELEVES : "est"
    UTILISATEURS ||--o{ NOTIFICATIONS : "reçoit"
    PROFESSEURS ||--o{ COURS : "propose"
    PROFESSEURS ||--o{ PLANNINGS : "définit"
    PROFESSEURS ||--o{ RESERVATIONS : "reçoit"
    ELEVES ||--o{ RESERVATIONS : "effectue"
    COURS ||--o{ RESERVATIONS : "concerne"
    PLANNINGS ||--o| RESERVATIONS : "réservé"
    RESERVATIONS ||--o{ SEANCES : "génère"
    ELEVES ||--o{ EVALUATIONS : "donne"
    PROFESSEURS ||--o{ EVALUATIONS : "reçoit"
```
