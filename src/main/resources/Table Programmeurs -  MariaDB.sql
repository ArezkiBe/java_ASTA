-- Création de la base
CREATE DATABASE IF NOT EXISTS Base_ALTN72
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE Base_ALTN72;

-- Création de la table Programmeur
DROP TABLE IF EXISTS Programmeur;

CREATE TABLE Programmeur (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             nom VARCHAR(100) NOT NULL,
                             prenom VARCHAR(100) NOT NULL,
                             adresse VARCHAR(255),
                             langage VARCHAR(100),
                             livre VARCHAR(150),
                             salaire DOUBLE
);

-- Insertion de 10 enregistrements
INSERT INTO Programmeur (nom, prenom, adresse, langage, livre, salaire) VALUES
                                                                            ('Nguyen', 'Thierry', '12 rue de la Paix, Paris', 'Java', 'Clean Code', 45000.00),
                                                                            ('Fernandez', 'Lucia', '34 avenue des Lilas, Lyon', 'Python', 'Fluent Python', 42000.00),
                                                                            ('O''Connor', 'Sean', '78 boulevard Victor Hugo, Marseille', 'C++', 'Effective C++', 48000.00),
                                                                            ('Kowalski', 'Marek', '5 place Bellecour, Lyon', 'C#', 'CLR via C#', 46000.00),
                                                                            ('Haddad', 'Amina', '22 rue Nationale, Lille', 'JavaScript', 'You Don’t Know JS', 43000.00),
                                                                            ('Okafor', 'Chinedu', '19 rue des Frères Lumière, Toulouse', 'Go', 'The Go Programming Language', 47000.00),
                                                                            ('Tanaka', 'Yuki', '55 avenue Montaigne, Paris', 'Ruby', 'Programming Ruby', 41000.00),
                                                                            ('Dubois', 'Claire', '8 rue de Bretagne, Nantes', 'Kotlin', 'Kotlin in Action', 44000.00),
                                                                            ('Singh', 'Arjun', '66 rue Voltaire, Bordeaux', 'Scala', 'Programming in Scala', 49000.00),
                                                                            ('Smith', 'Emily', '101 avenue Foch, Strasbourg', 'PHP', 'Modern PHP', 40000.00);