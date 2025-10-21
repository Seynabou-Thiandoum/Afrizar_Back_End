-- Ajouter les colonnes type, genre et imageUrl à la table categories
ALTER TABLE categories 
ADD COLUMN type VARCHAR(20) DEFAULT 'VETEMENTS',
ADD COLUMN genre VARCHAR(20) DEFAULT 'HOMME',
ADD COLUMN image_url VARCHAR(255);

-- Mettre à jour les catégories existantes avec des valeurs par défaut
UPDATE categories SET type = 'VETEMENTS', genre = 'HOMME' WHERE type IS NULL OR genre IS NULL;


