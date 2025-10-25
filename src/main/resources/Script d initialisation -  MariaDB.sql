-- Script de création des tables pour l'application ASTA
-- Schéma : base_asta_altn72

CREATE DATABASE IF NOT EXISTS base_asta_altn72;
USE base_asta_altn72;

CREATE TABLE tuteur_enseignant (
    id INT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(50) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(100) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL
);

CREATE TABLE entreprise (
    id INT AUTO_INCREMENT PRIMARY KEY,
    raison_sociale VARCHAR(200) NOT NULL,
    adresse VARCHAR(500),
    informations_utiles_acces_locaux VARCHAR(200)
);

CREATE TABLE annee_academique (
    id INT AUTO_INCREMENT PRIMARY KEY,
    annee VARCHAR(20) NOT NULL UNIQUE,
    est_courante BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE maitre_apprentissage (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    poste VARCHAR(100),
    email VARCHAR(100),
    telephone VARCHAR(15),
    remarques VARCHAR(500)
);

CREATE TABLE mission (
    id INT AUTO_INCREMENT PRIMARY KEY,
    mots_cles VARCHAR(200),
    metier_cible VARCHAR(100),
    commentaires VARCHAR(500)
);

CREATE TABLE apprenti (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    telephone VARCHAR(15),
    programme VARCHAR(50),
    majeure VARCHAR(100),
    est_archive BOOLEAN NOT NULL DEFAULT FALSE,
    tuteur_enseignant_id INT NOT NULL,
    entreprise_id INT,
    annee_academique_id INT NOT NULL,
    maitre_apprentissage_id INT,
    mission_id INT,
    feedback_tuteur_enseignant VARCHAR(500),
    FOREIGN KEY (tuteur_enseignant_id) REFERENCES tuteur_enseignant(id),
    FOREIGN KEY (entreprise_id) REFERENCES entreprise(id),
    FOREIGN KEY (annee_academique_id) REFERENCES annee_academique(id),
    FOREIGN KEY (maitre_apprentissage_id) REFERENCES maitre_apprentissage(id),
    FOREIGN KEY (mission_id) REFERENCES mission(id)
);

CREATE TABLE visite (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE,
    format VARCHAR(50),
    commentaires VARCHAR(500),
    apprenti_id INT,
    FOREIGN KEY (apprenti_id) REFERENCES apprenti(id)
);

CREATE TABLE evaluation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50), -- Memoire/Rapport ou Soutenance
    theme_sujet VARCHAR(200),
    note_finale DOUBLE,
    date_soutenance VARCHAR(50),
    commentaires VARCHAR(500),
    apprenti_id INT,
    FOREIGN KEY (apprenti_id) REFERENCES apprenti(id)
);

-- Table utilisateur : stocke les identifiants pour l'authentification
CREATE TABLE IF NOT EXISTS utilisateur (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    roles VARCHAR(255), -- ex: 'ROLE_USER,ROLE_ADMIN'
    prenom VARCHAR(100),
    actif TINYINT(1) DEFAULT 1,
    must_change_password TINYINT(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Exemple d'insertion (commenté) :
-- Remplacer LE_HASH_BCRYPT par un hash BCrypt généré côté application ou via un outil.
-- INSERT INTO utilisateur (username, password, roles, prenom, actif, must_change_password) VALUES
-- ('admin', 'LE_HASH_BCRYPT', 'ROLE_ADMIN,ROLE_USER', 'Admin', 1, 1);

-- Table password_reset_token : stocke les tokens de réinitialisation
CREATE TABLE IF NOT EXISTS password_reset_token (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    utilisateur_id BIGINT NOT NULL,
    expiry_date DATETIME NOT NULL,
    CONSTRAINT fk_prt_user FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Exemple d'insertion commentée (à utiliser uniquement pour tests ; le token doit être généré côté application):
-- INSERT INTO password_reset_token (token, utilisateur_id, expiry_date) VALUES
-- ('UUID_TOKEN_EXEMPLE', 1, DATE_ADD(NOW(), INTERVAL 2 HOUR));
