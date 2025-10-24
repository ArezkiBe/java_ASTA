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
