-- Ajouter le support des sous-catégories
ALTER TABLE categories
ADD COLUMN parent_id BIGINT,
ADD CONSTRAINT fk_categorie_parent 
    FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL;


