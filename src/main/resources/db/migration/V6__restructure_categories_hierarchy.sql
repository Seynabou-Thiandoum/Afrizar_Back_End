-- Restructuration complète des catégories avec hiérarchie
-- Supprimer les contraintes existantes
ALTER TABLE categories DROP CONSTRAINT IF EXISTS fk_categorie_parent;

-- Ajouter les nouvelles colonnes
ALTER TABLE categories 
ADD COLUMN IF NOT EXISTS slug VARCHAR(255) UNIQUE,
ADD COLUMN IF NOT EXISTS parent_id BIGINT;

-- Ajouter la contrainte de clé étrangère
ALTER TABLE categories 
ADD CONSTRAINT fk_categorie_parent 
    FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL;

-- Insérer les catégories racines
INSERT INTO categories (nom, slug, description, image_url, parent_id, type, genre, ordre, active) VALUES
('Homme', 'homme', 'Vêtements pour hommes', NULL, NULL, 'VETEMENTS', 'HOMME', 1, true),
('Femme', 'femme', 'Vêtements pour femmes', NULL, NULL, 'VETEMENTS', 'FEMME', 2, true),
('Enfant', 'enfant', 'Vêtements pour enfants', NULL, NULL, 'VETEMENTS', 'ENFANT', 3, true);

-- Insérer les sous-catégories pour Homme
INSERT INTO categories (nom, slug, description, image_url, parent_id, type, genre, ordre, active) VALUES
('Boubous', 'boubous-homme', 'Boubous traditionnels pour hommes', NULL, (SELECT id FROM categories WHERE slug = 'homme'), 'VETEMENTS', 'HOMME', 1, true),
('Costumes', 'costumes-homme', 'Costumes africains pour hommes', NULL, (SELECT id FROM categories WHERE slug = 'homme'), 'VETEMENTS', 'HOMME', 2, true),
('Pantalons', 'pantalons-homme', 'Pantalons pour hommes', NULL, (SELECT id FROM categories WHERE slug = 'homme'), 'VETEMENTS', 'HOMME', 3, true),
('Chemises', 'chemises-homme', 'Chemises pour hommes', NULL, (SELECT id FROM categories WHERE slug = 'homme'), 'VETEMENTS', 'HOMME', 4, true);

-- Insérer les sous-catégories pour Femme
INSERT INTO categories (nom, slug, description, image_url, parent_id, type, genre, ordre, active) VALUES
('Robes', 'robes-femme', 'Robes pour femmes', NULL, (SELECT id FROM categories WHERE slug = 'femme'), 'VETEMENTS', 'FEMME', 1, true),
('Boubous', 'boubous-femme', 'Boubous traditionnels pour femmes', NULL, (SELECT id FROM categories WHERE slug = 'femme'), 'VETEMENTS', 'FEMME', 2, true),
('Ensembles', 'ensembles-femme', 'Ensembles pour femmes', NULL, (SELECT id FROM categories WHERE slug = 'femme'), 'VETEMENTS', 'FEMME', 3, true);

-- Insérer les sous-catégories pour Enfant
INSERT INTO categories (nom, slug, description, image_url, parent_id, type, genre, ordre, active) VALUES
('Garçons', 'garcons', 'Vêtements pour garçons', NULL, (SELECT id FROM categories WHERE slug = 'enfant'), 'VETEMENTS', 'ENFANT', 1, true),
('Filles', 'filles', 'Vêtements pour filles', NULL, (SELECT id FROM categories WHERE slug = 'enfant'), 'VETEMENTS', 'ENFANT', 2, true);


