-- ============================================================
-- Système de Gestion des Cours Particuliers
-- Script SQL MySQL
-- ============================================================

CREATE DATABASE IF NOT EXISTS cours_particuliers
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE cours_particuliers;

-- Table des utilisateurs (base commune)
CREATE TABLE IF NOT EXISTS utilisateurs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telephone VARCHAR(20),
    mot_de_passe VARCHAR(255) NOT NULL,
    role ENUM('ADMINISTRATEUR', 'PROFESSEUR', 'ELEVE') NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Professeurs (OneToOne avec utilisateurs)
CREATE TABLE IF NOT EXISTS professeurs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id BIGINT NOT NULL UNIQUE,
    description TEXT,
    tarif_horaire DECIMAL(10,2),
    photo_profil VARCHAR(500),
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE
);

-- Matières enseignées (ElementCollection)
CREATE TABLE IF NOT EXISTS professeur_matieres (
    professeur_id BIGINT NOT NULL,
    matiere VARCHAR(100) NOT NULL,
    FOREIGN KEY (professeur_id) REFERENCES professeurs(id) ON DELETE CASCADE
);

-- Élèves (OneToOne avec utilisateurs)
CREATE TABLE IF NOT EXISTS eleves (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id BIGINT NOT NULL UNIQUE,
    niveau_scolaire VARCHAR(50),
    adresse VARCHAR(500),
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE
);

-- Cours (ManyToOne avec professeurs)
CREATE TABLE IF NOT EXISTS cours (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(200) NOT NULL,
    description TEXT,
    matiere VARCHAR(100) NOT NULL,
    niveau VARCHAR(50) NOT NULL,
    prix DECIMAL(10,2) NOT NULL,
    professeur_id BIGINT NOT NULL,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    statut ENUM('ACTIF', 'INACTIF', 'ARCHIVE') NOT NULL DEFAULT 'ACTIF',
    FOREIGN KEY (professeur_id) REFERENCES professeurs(id) ON DELETE CASCADE
);

-- Plannings / Disponibilités (ManyToOne avec professeurs)
CREATE TABLE IF NOT EXISTS plannings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    professeur_id BIGINT NOT NULL,
    date DATE NOT NULL,
    heure_debut TIME NOT NULL,
    heure_fin TIME NOT NULL,
    disponible BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (professeur_id) REFERENCES professeurs(id) ON DELETE CASCADE
);

-- Réservations (ManyToOne avec eleves, professeurs, cours + OneToOne planning)
CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eleve_id BIGINT NOT NULL,
    professeur_id BIGINT NOT NULL,
    cours_id BIGINT NOT NULL,
    planning_id BIGINT UNIQUE,
    date_reservation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    statut ENUM('EN_ATTENTE', 'CONFIRMEE', 'ANNULEE', 'TERMINEE') NOT NULL DEFAULT 'EN_ATTENTE',
    FOREIGN KEY (eleve_id) REFERENCES eleves(id) ON DELETE CASCADE,
    FOREIGN KEY (professeur_id) REFERENCES professeurs(id) ON DELETE CASCADE,
    FOREIGN KEY (cours_id) REFERENCES cours(id) ON DELETE CASCADE,
    FOREIGN KEY (planning_id) REFERENCES plannings(id) ON DELETE SET NULL
);

-- Séances (ManyToOne avec reservations)
CREATE TABLE IF NOT EXISTS seances (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_id BIGINT NOT NULL,
    date DATE NOT NULL,
    heure_debut TIME NOT NULL,
    heure_fin TIME NOT NULL,
    compte_rendu TEXT,
    statut ENUM('PLANIFIEE', 'REALISEE', 'ANNULEE') NOT NULL DEFAULT 'PLANIFIEE',
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE
);

-- Évaluations (ManyToOne avec eleves et professeurs)
CREATE TABLE IF NOT EXISTS evaluations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eleve_id BIGINT NOT NULL,
    professeur_id BIGINT NOT NULL,
    note INT NOT NULL CHECK (note BETWEEN 1 AND 5),
    commentaire TEXT,
    date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (eleve_id) REFERENCES eleves(id) ON DELETE CASCADE,
    FOREIGN KEY (professeur_id) REFERENCES professeurs(id) ON DELETE CASCADE
);

-- Notifications (ManyToOne avec utilisateurs)
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lu BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE
);

-- Refresh Tokens (OneToOne avec utilisateurs)
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    utilisateur_id BIGINT NOT NULL,
    date_expiration DATETIME NOT NULL,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE
);

-- Index pour les performances
CREATE INDEX idx_cours_matiere ON cours(matiere);
CREATE INDEX idx_cours_niveau ON cours(niveau);
CREATE INDEX idx_cours_professeur ON cours(professeur_id);
CREATE INDEX idx_plannings_date ON plannings(date);
CREATE INDEX idx_reservations_statut ON reservations(statut);
CREATE INDEX idx_notifications_utilisateur ON notifications(utilisateur_id);
