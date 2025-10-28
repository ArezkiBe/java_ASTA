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
    tuteur_enseignant_id INT NOT NULL, -- Obligatoire : tuteur enseignant superviseur académique
    entreprise_id INT,
    annee_academique_id INT NOT NULL,
    maitre_apprentissage_id INT NOT NULL, -- Obligatoire : encadrement professionnel en entreprise
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
    statut VARCHAR(50) DEFAULT 'Programmée',
    commentaire_tuteur VARCHAR(500),
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

-- Utilisateur admin par défaut pour les tests
-- Mot de passe : "admin123" (hashé avec BCrypt)
INSERT INTO utilisateur (username, password, roles, prenom, actif, must_change_password) VALUES
('admin', '$2a$10$EQA1WZLF7M7s.Qr9CqQWD.1PtV9b9L3lYi3XxJ7g9d.ZuKqI6WUI.', 'ROLE_ADMIN,ROLE_USER', 'Administrateur', 1, 0),
('user', '$2a$10$EQA1WZLF7M7s.Qr9CqQWD.1PtV9b9L3lYi3XxJ7g9d.ZuKqI6WUI.', 'ROLE_USER', 'Utilisateur', 1, 0);

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

-- ====================================================================
-- DONNÉES DE TEST : ENTREPRISES FRANÇAISES DE LA TECH
-- ====================================================================

-- Insertion d'entreprises françaises reconnues du secteur technologique
INSERT INTO entreprise (raison_sociale, adresse, informations_utiles_acces_locaux) VALUES

-- ESN et Consulting IT de renom
('Capgemini', '11 rue de Tilsitt, 75017 Paris', 'Métro Charles de Gaulle-Étoile. Badge visiteur requis à l\'accueil.'),
('Sopra Steria', '9 bis rue de Presbourg, 75116 Paris', 'Métro Charles de Gaulle-Étoile. Rendez-vous au 12ème étage.'),
('Atos', 'River Ouest, 80 quai Voltaire, 95870 Bezons', 'RER A - Gare de Bezons. Navette depuis la gare.'),
('Accenture France', '118 avenue des Champs-Élysées, 75008 Paris', 'Métro George V. Badge visiteur à récupérer à l\'accueil.'),
('Thales', 'Tour Carpe Diem, 31 place des Corolles, 92400 Courbevoie', 'Métro Esplanade de La Défense. Contrôle sécurité obligatoire.'),
('Orange Business Services', '78 rue Olivier de Serres, 75015 Paris', 'Métro Convention. Accès par l\'entrée principale.'),
('Amadeus', '485 route du Pin Montard, 06410 Biot', 'Voiture recommandée. Parking visiteurs disponible.'),

-- Scale-ups et Licornes françaises  
('BlaBlaCar', '84 avenue de la République, 75011 Paris', 'Métro Parmentier. Accueil au rez-de-chaussée, open space moderne.'),
('Dassault Systèmes', '10 rue Marcel Dassault, 78140 Vélizy-Villacoublay', 'RER C - Gare de Vélizy. Badge visiteur obligatoire.'),
('Criteo', '32 rue Blanche, 75009 Paris', 'Métro Blanche ou Pigalle. Accès par l\'accueil sécurisé.'),
('Mirakl', '17 rue du Louvre, 75001 Paris', 'Métro Louvre-Rivoli. Interphone à l\'entrée.'),
('Doctolib', '65 rue de Richelieu, 75002 Paris', 'Métro Bourse. Badge visiteur à l\'accueil.'),
('Datadog', '14 rue de la Ville l\'Évêque, 75008 Paris', 'Métro Havre-Caumartin. Accès contrôlé par badge.'),
('ContentSquare', '42 avenue Montaigne, 75008 Paris', 'Métro Franklin D. Roosevelt. Rendez-vous au 3ème étage.'),
('Algolia', '55 rue d\'Amsterdam, 75008 Paris', 'Métro Saint-Lazare. Accueil au 2ème étage.'),

-- Startups et PME innovantes
('Yuka', '14 rue de Turbigo, 75001 Paris', 'Métro Étienne Marcel. Startup dans un espace de coworking.'),
('Alan', '2 villa Gaudelet, 75011 Paris', 'Métro République. Code d\'accès fourni par le tuteur.'),
('Ledger', '1 rue du Mail, 75002 Paris', 'Métro Sentier. Sécurité renforcée, rendez-vous obligatoire.'),
('Swile', '41 rue Réaumur, 75003 Paris', 'Métro Arts et Métiers. Badge visiteur à l\'accueil.'),
('Shift Technology', '5 parvis Alan Turing, 75013 Paris', 'Métro Bibliothèque François Mitterrand. Campus numérique moderne.'),
('Klaxoon', '2 rue de la Mabilais, 35000 Rennes', 'Gare de Rennes puis métro. Environnement de travail collaboratif.'),
('Botify', '10 rue de la Paix, 75002 Paris', 'Métro Opéra. Badge visiteur requis.'),

-- Grands groupes avec forte activité numérique
('LVMH Digital', '22 avenue Montaigne, 75008 Paris', 'Métro Franklin D. Roosevelt. Dress code business, badge visiteur.'),
('L\'Oréal Digital', '14 rue Royale, 75008 Paris', 'Métro Concorde. Accès par l\'entrée principale.'),
('Société Générale', '17 cours Valmy, 92800 Puteaux', 'Métro Esplanade de La Défense. Contrôle sécurité bancaire.'),
('BNP Paribas', '16 boulevard des Italiens, 75009 Paris', 'Métro Richelieu-Drouot. Badge visiteur obligatoire.'),
('Renault Digital', '13-15 quai Alphonse Le Gallo, 92100 Boulogne-Billancourt', 'Métro Pont de Sèvres. Parking visiteurs limité.'),
('Airbus', '1 rond-point Maurice Bellonte, 31700 Blagnac', 'Navette depuis l\'aéroport de Toulouse. Habilitation sécurité requise.'),

-- Éditeurs de logiciels français
('Murex', '103 rue de Grenelle, 75007 Paris', 'Métro Varenne. Fintech, environnement multiculturel.'),
('Centreon', '46 rue de l\'Arbre Sec, 75001 Paris', 'Métro Louvre-Rivoli. Spécialiste monitoring IT.'),
('Talend', '5-7 rue Salomon de Rothschild, 92150 Suresnes', 'Tramway T2 - Suresnes Longchamp. Open data specialist.'),
('OVHcloud', '2 rue Kellermann, 59100 Roubaix', 'Gare de Roubaix. Leader français du cloud computing.'),
('Scaleway', '8 rue de la Ville l\'Évêque, 75008 Paris', 'Métro Havre-Caumartin. Filiale cloud d\'Iliad.'),

-- Agences digitales et web agencies
('Publicis Sapient', '133 avenue des Champs-Élysées, 75008 Paris', 'Métro George V. Agence de transformation digitale.'),
('Ekino', '84 rue de Grenelle, 75007 Paris', 'Métro Solférino. Agence digitale premium.'),
('Smile', '20 rue des Jardins Saint-Paul, 75004 Paris', 'Métro Saint-Paul. Open source et e-commerce.'),
('Zenika', '10 rue de Milan, 75009 Paris', 'Métro Notre-Dame-de-Lorette. Cabinet de conseil IT.'),

-- Médias et EdTech
('Le Figaro', '14 boulevard Haussmann, 75009 Paris', 'Métro Chaussée d\'Antin. Presse et digital.'),
('OpenClassrooms', '7 cité Paradis, 75010 Paris', 'Métro Poissonnière. Plateforme d\'éducation en ligne.'),
('360Learning', '27 rue du Chemin Vert, 75011 Paris', 'Métro Bastille. EdTech collaborative learning.'),

-- Fintechs françaises
('Lydia', '40 rue de Paradis, 75010 Paris', 'Métro Gare de l\'Est. Application de paiement mobile.'),
('Qonto', '18 rue de Navarin, 75009 Paris', 'Métro Notre-Dame-de-Lorette. Néobanque pour entreprises.'),
('Bankin\'', '15 rue des Halles, 75001 Paris', 'Métro Châtelet. Agrégateur bancaire.'),

-- Gaming et Entertainment
('Ubisoft', '28 rue Armand Carrel, 93100 Montreuil', 'Métro Robespierre. Studio de développement de jeux vidéo.'),
('Gameloft', '6 rue Ménars, 75002 Paris', 'Métro Bourse. Mobile gaming, filiale Vivendi.'),
('Ankama', '75 boulevard d\'Armentières, 59100 Roubaix', 'Gare de Roubaix. Créateur de Dofus et Wakfu.'),

-- E-commerce et Retail Tech
('Cdiscount', '120-126 quai de Bacalan, 33000 Bordeaux', 'Tramway A - Bacalan. E-commerce, filiale Casino.'),
('Vente-Privee', '249 avenue du Président Wilson, 93210 Saint-Denis', 'Métro Carrefour Pleyel. Ventes privées en ligne.'),
('Spartoo', '27 avenue de l\'Europe, 38100 Grenoble', 'Tramway A - Chavant. E-commerce chaussures et mode.');

-- ====================================================================
-- DONNÉES DE TEST : TUTEURS ENSEIGNANTS
-- ====================================================================

-- Insertion de tuteurs enseignants pour les tests
-- Note: Mot de passe hashé avec BCrypt pour "password123"
INSERT INTO tuteur_enseignant (login, mot_de_passe, nom, prenom) VALUES
('prof.martin', '$2a$10$EQA1WZLF7M7s.Qr9CqQWD.1PtV9b9L3lYi3XxJ7g9d.ZuKqI6WUI.', 'Martin', 'Jean-Claude'),
('prof.bernard', '$2a$10$EQA1WZLF7M7s.Qr9CqQWD.1PtV9b9L3lYi3XxJ7g9d.ZuKqI6WUI.', 'Bernard', 'Sylvie'),
('prof.dubois', '$2a$10$EQA1WZLF7M7s.Qr9CqQWD.1PtV9b9L3lYi3XxJ7g9d.ZuKqI6WUI.', 'Dubois', 'Philippe'),
('prof.moreau', '$2a$10$EQA1WZLF7M7s.Qr9CqQWD.1PtV9b9L3lYi3XxJ7g9d.ZuKqI6WUI.', 'Moreau', 'Catherine'),
('prof.simon', '$2a$10$EQA1WZLF7M7s.Qr9CqQWD.1PtV9b9L3lYi3XxJ7g9d.ZuKqI6WUI.', 'Simon', 'François');

-- ====================================================================
-- DONNÉES DE TEST : MAÎTRES D'APPRENTISSAGE
-- ====================================================================

-- Insertion de maîtres d'apprentissage réalistes
INSERT INTO maitre_apprentissage (nom, prenom, poste, email, telephone, remarques) VALUES

-- Profils IT senior dans différents domaines
('Martin', 'Sophie', 'Lead Developer Full Stack', 'sophie.martin@capgemini.com', '01.45.67.89.12', 'Experte React/Node.js, très pédagogue avec les apprentis.'),
('Dubois', 'Alexandre', 'Architecte Solution', 'a.dubois@soprasteria.com', '01.56.78.90.23', 'Spécialiste microservices et cloud AWS. Disponible pour mentorat.'),
('Bernard', 'Caroline', 'DevOps Engineer', 'caroline.bernard@atos.fr', '01.67.89.01.34', 'Expert CI/CD et Kubernetes. Apprécie transmettre son savoir.'),
('Petit', 'Julien', 'Product Owner', 'julien.petit@accenture.com', '01.78.90.12.45', 'Méthodologies Agile/Scrum. Excellent pour projets transverses.'),
('Durand', 'Émilie', 'Tech Lead Mobile', 'emilie.durand@thales.com', '01.89.01.23.56', 'Développement iOS/Android natif et React Native.'),

-- Profils dans les scale-ups
('Moreau', 'Thomas', 'Engineering Manager', 'thomas.moreau@blablacar.com', '01.90.12.34.67', 'Management technique et leadership. Environnement startup.'),
('Simon', 'Laura', 'Data Scientist Senior', 'laura.simon@criteo.com', 'laura.simon@criteo.com', 'Machine Learning et Big Data. Projets data-driven.'),
('Michel', 'Kevin', 'Frontend Developer Senior', 'kevin.michel@doctolib.com', '01.12.34.56.78', 'Vue.js et UX/UI. Très patient avec les débutants.'),
('Lefebvre', 'Audrey', 'Backend Developer', 'audrey.lefebvre@algolia.com', '01.23.45.67.89', 'API REST, ElasticSearch. Projets internationaux.'),
('Roux', 'Maxime', 'Security Engineer', 'maxime.roux@ledger.com', '01.34.56.78.90', 'Cybersécurité et blockchain. Environnement high-tech.'),

-- Profils dans les grandes entreprises
('Fournier', 'Nathalie', 'Chef de Projet Digital', 'nathalie.fournier@loreal.com', '01.45.67.89.01', 'Transformation digitale et innovation. Projets beauty-tech.'),
('Girard', 'David', 'Architecte Enterprise', 'david.girard@societegenerale.com', '01.56.78.90.12', 'Solutions bancaires et fintech. Environnement réglementé.'),
('Bonnet', 'Marie', 'UX/UI Designer Senior', 'marie.bonnet@renault.com', '01.67.89.01.23', 'Design thinking et automotive. Projets connectés.'),
('Dupont', 'Pierre', 'Cloud Architect', 'pierre.dupont@ovhcloud.com', '01.78.90.12.34', 'Infrastructure cloud et SRE. Technologies modernes.'),
('Laurent', 'Céline', 'Scrum Master', 'celine.laurent@publicis.com', '01.89.01.23.45', 'Agilité à l\'échelle et coaching équipe. Projets clients grands comptes.');

-- ====================================================================
-- DONNÉES DE TEST : ANNÉES ACADÉMIQUES
-- ====================================================================

-- Insertion des années académiques récentes et futures
INSERT INTO annee_academique (annee, est_courante) VALUES
('2022-2023', FALSE),
('2023-2024', FALSE),
('2024-2025', TRUE),  -- Année courante
('2025-2026', FALSE),
('2026-2027', FALSE);

-- ====================================================================
-- DONNÉES DE TEST : MISSIONS TYPES
-- ====================================================================

-- Insertion de missions types pour différents profils
INSERT INTO mission (mots_cles, metier_cible, commentaires) VALUES

-- Missions développement web
('React, Node.js, MongoDB, API REST', 'Développeur Full Stack', 'Développement d\'une application web moderne avec stack MERN. Participation à toutes les phases du projet.'),
('Vue.js, Spring Boot, PostgreSQL, Docker', 'Développeur Full Stack', 'Création d\'un portail client avec architecture microservices. Focus sur les bonnes pratiques de développement.'),
('Angular, .NET Core, SQL Server, Azure', 'Développeur Full Stack', 'Application de gestion métier avec déploiement cloud. Méthodes Agile/Scrum.'),

-- Missions mobile
('React Native, TypeScript, Firebase', 'Développeur Mobile', 'Application mobile cross-platform avec notifications push et synchronisation offline.'),
('Swift, iOS, Core Data, SwiftUI', 'Développeur Mobile iOS', 'Application native iOS avec fonctionnalités avancées et intégration API.'),
('Kotlin, Android, Room, Jetpack Compose', 'Développeur Mobile Android', 'Application Android moderne avec Material Design 3 et architecture MVVM.'),

-- Missions data et IA
('Python, TensorFlow, Pandas, Jupyter', 'Data Scientist', 'Analyse prédictive et machine learning sur données clients. Modèles de recommandation.'),
('Spark, Kafka, Elasticsearch, Kibana', 'Data Engineer', 'Pipeline de traitement de données en temps réel. Architecture Big Data.'),
('Power BI, SQL, Python, Azure ML', 'Analyste Data', 'Tableaux de bord décisionnels et modèles prédictifs métier.'),

-- Missions DevOps et infrastructure
('Jenkins, Docker, Kubernetes, Terraform', 'DevOps Engineer', 'Automatisation CI/CD et orchestration de conteneurs. Infrastructure as Code.'),
('AWS, CloudFormation, Lambda, RDS', 'Cloud Engineer', 'Migration vers le cloud et optimisation des coûts. Architecture serverless.'),
('Prometheus, Grafana, ELK Stack', 'SRE Engineer', 'Monitoring et observabilité des systèmes. Amélioration de la fiabilité.'),

-- Missions cybersécurité
('SIEM, SOC, Incident Response, Forensics', 'Analyste Cybersécurité', 'Détection et réponse aux incidents de sécurité. Analyse de menaces.'),
('Pentest, OWASP, Burp Suite, Nmap', 'Consultant Sécurité', 'Tests d\'intrusion et audits de sécurité. Évaluation des vulnérabilités.'),

-- Missions UX/UI
('Figma, Sketch, Adobe XD, Prototyping', 'UX/UI Designer', 'Conception d\'interfaces utilisateur et expérience utilisateur. Tests d\'utilisabilité.'),
('Design System, Atomic Design, Accessibility', 'UX Designer', 'Création de design system et amélioration de l\'accessibilité.'),

-- Missions management et produit
('Jira, Confluence, Agile, Scrum', 'Product Owner', 'Gestion de backlog produit et coordination équipes. Méthodologies Agile.'),
('Roadmap, KPIs, Analytics, A/B Testing', 'Product Manager', 'Stratégie produit et analyse de performance. Optimisation conversion.');

-- ====================================================================
-- DONNÉES DE TEST : APPRENTIS
-- ====================================================================

-- Insertion d'apprentis réalistes pour les tests
INSERT INTO apprenti (nom, prenom, email, telephone, programme, majeure, est_archive, 
                     tuteur_enseignant_id, entreprise_id, annee_academique_id, maitre_apprentissage_id, mission_id, 
                     feedback_tuteur_enseignant) VALUES

-- Apprentis L1 (première année)
('Moreau', 'Emma', 'emma.moreau@efrei.fr', '06.12.34.56.78', 'L1', 'Informatique', FALSE, 
 1, 1, 3, 1, 1, 'Étudiante motivée avec de bonnes bases en programmation.'),
 
('Dubois', 'Lucas', 'lucas.dubois@efrei.fr', '06.23.45.67.89', 'L1', 'Informatique', FALSE, 
 2, 5, 3, 3, 3, 'Très bon niveau technique, à encourager sur les soft skills.'),
 
('Martin', 'Chloé', 'chloe.martin@efrei.fr', '06.34.56.78.90', 'L1', 'Cybersécurité', FALSE, 
 3, 10, 3, 10, 11, 'Passionnée de sécurité, projets ambitieux.'),

-- Apprentis L2 (deuxième année) 
('Bernard', 'Thomas', 'thomas.bernard@efrei.fr', '06.45.67.89.01', 'L2', 'Informatique', FALSE, 
 1, 8, 3, 6, 4, 'Excellent en développement mobile, autonome sur les projets.'),
 
('Petit', 'Manon', 'manon.petit@efrei.fr', '06.56.78.90.12', 'L2', 'Data Science', FALSE, 
 4, 12, 3, 7, 8, 'Compétences analytiques remarquables, projets data innovants.'),
 
('Durand', 'Antoine', 'antoine.durand@efrei.fr', '06.67.89.01.23', 'L2', 'Informatique', FALSE, 
 5, 15, 3, 8, 2, 'Très bonne progression, intérêt marqué pour le frontend.'),

-- Apprentis L3 (troisième année)
('Simon', 'Léa', 'lea.simon@efrei.fr', '06.78.90.12.34', 'L3', 'Informatique', FALSE, 
 2, 3, 3, 2, 5, 'Niveau expert, excellente candidate pour poursuite en master.'),
 
('Michel', 'Maxime', 'maxime.michel@efrei.fr', '06.89.01.23.45', 'L3', 'Cybersécurité', FALSE, 
 3, 20, 3, 10, 12, 'Spécialiste sécurité reconnu, projets à fort impact.'),
 
('Lefebvre', 'Sarah', 'sarah.lefebvre@efrei.fr', '06.90.12.34.56', 'L3', 'Data Science', FALSE, 
 4, 25, 3, 14, 9, 'Expertise machine learning, stage de fin d\'études remarquable.');

-- ====================================================================
-- DONNÉES DE TEST : ÉVALUATIONS
-- ====================================================================

-- Insertion d'évaluations d'exemple
INSERT INTO evaluation (type, theme_sujet, note_finale, date_soutenance, commentaires, apprenti_id) VALUES

-- Évaluations L1
('Rapport', 'Développement d\'une application web de gestion de tâches', 15.5, '2024-06-15', 
 'Bon travail sur les bases du développement web. Interface utilisateur soignée et fonctionnalités correctement implémentées.', 1),
 
('Soutenance', 'Présentation du projet web et retour d\'expérience', 14.0, '2024-06-20',
 'Présentation claire mais pourrait être plus dynamique. Bonnes réponses aux questions techniques.', 1),

-- Évaluations L2  
('Rapport', 'Application mobile de suivi sportif avec React Native', 16.5, '2024-06-10',
 'Excellent travail technique. Application fonctionnelle avec des features avancées. Code bien structuré.', 4),
 
('Soutenance', 'Démonstration de l\'app mobile et architecture technique', 17.0, '2024-06-18',
 'Présentation exemplaire, maîtrise technique évidente. Très bonnes réponses aux questions du jury.', 4),

-- Évaluations L3
('Mémoire', 'Intelligence Artificielle appliquée à la détection de fraudes', 18.0, '2024-06-25',
 'Mémoire de qualité professionnelle. Approche méthodologique rigoureuse et résultats probants.', 7),
 
('Soutenance', 'Soutenance de mémoire - IA et détection de fraudes', 17.5, '2024-07-02',
 'Soutenance de haut niveau. Démonstration convaincante et expertise technique remarquable.', 7),

-- Évaluations pour Lucas Dubois (ID 2)
('Rapport', 'Développement d\'une application de gestion de stock', 13.0, '2024-06-12',
 'Travail correct mais manque de finition. Les fonctionnalités de base sont présentes.', 2),
 
('Soutenance', 'Présentation du projet de gestion de stock', 12.5, '2024-06-18',
 'Présentation timide mais contenu technique satisfaisant. À développer la confiance en soi.', 2);

-- ====================================================================
-- DONNÉES DE TEST : VISITES
-- ====================================================================

-- Insertion de visites d'exemple
INSERT INTO visite (date, format, commentaires, statut, commentaire_tuteur, apprenti_id) VALUES

-- Visites réalisées
('2024-03-15', 'Présentiel', 'Première visite d\'intégration. Découverte de l\'équipe et des outils de développement.', 
 'Réalisée', 'Excellent accueil en entreprise. Emma s\'adapte bien à l\'environnement professionnel.', 1),

('2024-10-10', 'Visio', 'Point d\'avancement sur le projet mobile. Démonstration des fonctionnalités développées.',
 'Réalisée', 'Très bonne progression technique. Thomas montre une réelle autonomie sur son projet.', 4),

('2024-09-20', 'Présentiel', 'Visite de suivi en laboratoire de cybersécurité. Évaluation des compétences acquises.',
 'Réalisée', 'Léa maîtrise parfaitement son sujet. Niveau d\'expertise impressionnant pour une L3.', 7),

('2024-09-05', 'Présentiel', 'Première visite d\'évaluation. Découverte de l\'environnement de travail et des missions.',
 'Réalisée', 'Lucas s\'adapte bien à l\'environnement professionnel. Motivation visible, à encourager sur la prise d\'initiative.', 2),

-- Visites programmées
('2024-11-15', 'Présentiel', 'Visite de mi-parcours pour évaluer l\'intégration et les apprentissages techniques.',
 'Programmée', NULL, 2),

('2024-11-22', 'Visio', 'Point trimestriel sur les projets data science. Revue des modèles développés.',
 'Programmée', NULL, 5),

('2024-12-05', 'Téléphonique', 'Entretien de suivi avec le maître d\'apprentissage sur l\'évolution des compétences.',
 'Programmée', NULL, 6),

-- Visites en retard (pour tester les alertes)
('2024-10-01', 'Présentiel', 'Visite prévue mais reportée à cause de contraintes planning entreprise.',
 'Programmée', NULL, 3),

('2024-09-30', 'Visio', 'Point d\'étape sur les projets de cybersécurité. Évaluation des livrables.',
 'Programmée', NULL, 8);
