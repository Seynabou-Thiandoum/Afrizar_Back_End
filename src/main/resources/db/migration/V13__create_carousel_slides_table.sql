-- Migration V13: Create carousel_slides table
CREATE TABLE IF NOT EXISTS carousel_slides (
    id BIGSERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    sous_titre VARCHAR(500),
    image_url VARCHAR(500) NOT NULL,
    badge VARCHAR(50), -- 'HOT', 'NEW', 'PROMO'
    bouton_texte VARCHAR(100),
    bouton_lien VARCHAR(500),
    ordre_affichage INTEGER DEFAULT 0,
    actif BOOLEAN DEFAULT true,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertion de données de test
INSERT INTO carousel_slides (titre, sous_titre, image_url, badge, bouton_texte, bouton_lien, ordre_affichage, actif)
VALUES 
    ('MEGA SOLDES', 'Jusqu''à -50% sur toute la collection', 'https://images.unsplash.com/photo-1583745800992-0d82e55ae8b5?w=1200&h=600&fit=crop', 'HOT', 'Découvrir', '#categories', 1, true),
    ('Nouvelle Collection', 'Découvrez les dernières tendances africaines', 'https://images.unsplash.com/photo-1469334031218-e382a71b716b?w=1200&h=600&fit=crop', 'NEW', 'Explorer', '#produits', 2, true),
    ('Livraison Gratuite', 'Pour toute commande supérieure à 50,000 FCFA', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=1200&h=600&fit=crop', 'PROMO', 'Acheter maintenant', '#catalog', 3, true);

CREATE INDEX idx_carousel_slides_actif ON carousel_slides(actif);
CREATE INDEX idx_carousel_slides_ordre ON carousel_slides(ordre_affichage);
