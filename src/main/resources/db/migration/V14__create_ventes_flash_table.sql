-- Migration V14: Create ventes_flash tables
CREATE TABLE IF NOT EXISTS ventes_flash (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    description TEXT,
    date_debut TIMESTAMP NOT NULL,
    date_fin TIMESTAMP NOT NULL,
    actif BOOLEAN DEFAULT true,
    pourcentage_reduction_par_defaut INTEGER, -- Si pas de prix spécifique par produit
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table de relation entre VenteFlash et Produit avec prix spécifiques
CREATE TABLE IF NOT EXISTS produit_vente_flash (
    id BIGSERIAL PRIMARY KEY,
    vente_flash_id BIGINT NOT NULL REFERENCES ventes_flash(id) ON DELETE CASCADE,
    produit_id BIGINT NOT NULL REFERENCES produits(id) ON DELETE CASCADE,
    prix_promotionnel DECIMAL(15, 2), -- Prix spécial pour cette vente
    pourcentage_reduction INTEGER, -- Par exemple: 50 pour -50%
    quantite_stock INTEGER, -- Stock disponible pour la vente flash
    image_url VARCHAR(1000), -- Image personnalisée pour la vente flash (optionnel)
    UNIQUE(vente_flash_id, produit_id)
);

-- Index pour améliorer les performances
CREATE INDEX idx_ventes_flash_actif ON ventes_flash(actif);
CREATE INDEX idx_ventes_flash_dates ON ventes_flash(date_debut, date_fin);
CREATE INDEX idx_produit_vente_flash_vente ON produit_vente_flash(vente_flash_id);
CREATE INDEX idx_produit_vente_flash_produit ON produit_vente_flash(produit_id);

-- Données de test (vente flash active pour 7 jours)
INSERT INTO ventes_flash (nom, description, date_debut, date_fin, actif, pourcentage_reduction_par_defaut)
VALUES 
    ('SOLDES D''HIVER 2025', 'MEGA PROMOTION sur tous nos produits', 
     NOW(), 
     NOW() + INTERVAL '7 days', 
     true, 
     50);
