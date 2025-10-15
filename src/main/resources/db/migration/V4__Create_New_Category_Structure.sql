-- Migration pour la nouvelle structure de catégories
-- V4__Create_New_Category_Structure.sql

-- 1. Créer la table des types de catégories (Boubous, Costumes, Robes, etc.)
CREATE TABLE types_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    type VARCHAR(50) NOT NULL DEFAULT 'VETEMENTS',
    image_url VARCHAR(255),
    ordre INT DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. Créer la table des genres de catégories (Homme, Femme, Enfant)
CREATE TABLE genres_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    type VARCHAR(50) NOT NULL DEFAULT 'VETEMENTS',
    image_url VARCHAR(255),
    ordre INT DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. Créer la table des combinaisons Genre + Type
CREATE TABLE categories_combinaisons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    genre_id BIGINT NOT NULL,
    type_id BIGINT NOT NULL,
    ordre INT DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (genre_id) REFERENCES genres_categories(id) ON DELETE CASCADE,
    FOREIGN KEY (type_id) REFERENCES types_categories(id) ON DELETE CASCADE,
    
    UNIQUE KEY unique_combination (genre_id, type_id),
    INDEX idx_genre_id (genre_id),
    INDEX idx_type_id (type_id),
    INDEX idx_active (active),
    INDEX idx_ordre (ordre)
);

-- 4. Insérer les types de base pour VÊTEMENTS
INSERT INTO types_categories (nom, description, type, ordre) VALUES
('Boubous', 'Grands boubous traditionnels brodés', 'VETEMENTS', 1),
('Costumes', 'Costumes traditionnels et modernes', 'VETEMENTS', 2),
('Pantalons', 'Pantalons traditionnels et modernes', 'VETEMENTS', 3),
('Chemises', 'Chemises traditionnelles et modernes', 'VETEMENTS', 4),
('Robes', 'Robes traditionnelles et modernes', 'VETEMENTS', 5),
('Caftans', 'Caftans élégants', 'VETEMENTS', 6),
('Ensembles', 'Ensembles complets', 'VETEMENTS', 7),
('Tenues Traditionnelles', 'Tenues traditionnelles complètes', 'VETEMENTS', 8);

-- 5. Insérer les types de base pour ACCESSOIRES
INSERT INTO types_categories (nom, description, type, ordre) VALUES
('Bonnets/Chapeaux', 'Couvre-chefs traditionnels et modernes', 'ACCESSOIRES', 1),
('Chaussures', 'Babouches, sandales, chaussures artisanales', 'ACCESSOIRES', 2),
('Sacs', 'Sacs à main, besaces, maroquinerie', 'ACCESSOIRES', 3),
('Bijoux', 'Colliers, bracelets, boucles d\'oreilles', 'ACCESSOIRES', 4);

-- 6. Insérer les genres pour VÊTEMENTS
INSERT INTO genres_categories (nom, description, type, ordre) VALUES
('Homme', 'Vêtements traditionnels et modernes pour hommes', 'VETEMENTS', 1),
('Femme', 'Vêtements traditionnels et modernes pour femmes', 'VETEMENTS', 2),
('Enfant', 'Vêtements traditionnels et modernes pour enfants', 'VETEMENTS', 3);

-- 7. Insérer les genres pour ACCESSOIRES
INSERT INTO genres_categories (nom, description, type, ordre) VALUES
('Homme', 'Accessoires traditionnels et modernes pour hommes', 'ACCESSOIRES', 1),
('Femme', 'Accessoires traditionnels et modernes pour femmes', 'ACCESSOIRES', 2),
('Enfant', 'Accessoires traditionnels et modernes pour enfants', 'ACCESSOIRES', 3);

-- 8. Créer les associations VÊTEMENTS
-- Homme + Types
INSERT INTO categories_combinaisons (genre_id, type_id, ordre) VALUES
(1, 1, 1), -- Homme + Boubous
(1, 2, 2), -- Homme + Costumes
(1, 3, 3), -- Homme + Pantalons
(1, 4, 4); -- Homme + Chemises

-- Femme + Types
INSERT INTO categories_combinaisons (genre_id, type_id, ordre) VALUES
(2, 1, 1), -- Femme + Boubous (MÊME Boubous !)
(2, 5, 2), -- Femme + Robes
(2, 6, 3); -- Femme + Caftans

-- Enfant + Types
INSERT INTO categories_combinaisons (genre_id, type_id, ordre) VALUES
(3, 1, 1), -- Enfant + Boubous (MÊME Boubous !)
(3, 7, 2), -- Enfant + Ensembles
(3, 8, 3); -- Enfant + Tenues Traditionnelles

-- 9. Créer les associations ACCESSOIRES
-- Homme + Types Accessoires
INSERT INTO categories_combinaisons (genre_id, type_id, ordre) VALUES
(4, 9, 1), -- Homme + Bonnets/Chapeaux
(4, 10, 2), -- Homme + Chaussures
(4, 11, 3); -- Homme + Sacs

-- Femme + Types Accessoires
INSERT INTO categories_combinaisons (genre_id, type_id, ordre) VALUES
(5, 9, 1), -- Femme + Bonnets/Chapeaux
(5, 10, 2), -- Femme + Chaussures
(5, 11, 3), -- Femme + Sacs
(5, 12, 4); -- Femme + Bijoux

-- Enfant + Types Accessoires
INSERT INTO categories_combinaisons (genre_id, type_id, ordre) VALUES
(6, 9, 1), -- Enfant + Bonnets/Chapeaux
(6, 10, 2), -- Enfant + Chaussures
(6, 11, 3); -- Enfant + Sacs

-- 10. Créer des index pour optimiser les performances
CREATE INDEX idx_types_categories_type ON types_categories(type);
CREATE INDEX idx_types_categories_active ON types_categories(active);
CREATE INDEX idx_types_categories_ordre ON types_categories(ordre);

CREATE INDEX idx_genres_categories_type ON genres_categories(type);
CREATE INDEX idx_genres_categories_active ON genres_categories(active);
CREATE INDEX idx_genres_categories_ordre ON genres_categories(ordre);

-- 11. Ajouter des commentaires pour la documentation
ALTER TABLE types_categories COMMENT = 'Types de catégories (Boubous, Costumes, Robes, etc.)';
ALTER TABLE genres_categories COMMENT = 'Genres de catégories (Homme, Femme, Enfant)';
ALTER TABLE categories_combinaisons COMMENT = 'Associations Genre + Type pour éviter la duplication';

-- 12. Vérification des données insérées
SELECT 'Types créés:' as info, COUNT(*) as count FROM types_categories;
SELECT 'Genres créés:' as info, COUNT(*) as count FROM genres_categories;
SELECT 'Associations créées:' as info, COUNT(*) as count FROM categories_combinaisons;

-- 13. Afficher la structure finale
SELECT 
    g.nom as genre,
    g.type as genre_type,
    t.nom as type,
    t.type as type_category,
    c.ordre
FROM categories_combinaisons c
JOIN genres_categories g ON c.genre_id = g.id
JOIN types_categories t ON c.type_id = t.id
WHERE c.active = TRUE
ORDER BY g.type, g.ordre, c.ordre;
