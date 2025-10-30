-- ============================================
-- MIGRATION V7 : CRÉATION DES TABLES PANIER
-- ============================================

-- Table des paniers
CREATE TABLE IF NOT EXISTS paniers (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL UNIQUE,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    
    CONSTRAINT fk_panier_client FOREIGN KEY (client_id) REFERENCES utilisateur(id) ON DELETE CASCADE
);

-- Table des items du panier
CREATE TABLE IF NOT EXISTS panier_items (
    id BIGSERIAL PRIMARY KEY,
    panier_id BIGINT NOT NULL,
    produit_id BIGINT NOT NULL,
    quantite INTEGER NOT NULL CHECK (quantite > 0),
    prix_unitaire DECIMAL(10, 2) NOT NULL,
    taille VARCHAR(50),
    couleur VARCHAR(50),
    options_personnalisation VARCHAR(1000),
    date_ajout TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_panier_item_panier FOREIGN KEY (panier_id) REFERENCES paniers(id) ON DELETE CASCADE,
    CONSTRAINT fk_panier_item_produit FOREIGN KEY (produit_id) REFERENCES produits(id) ON DELETE CASCADE
);

-- Index pour améliorer les performances
CREATE INDEX idx_panier_client ON paniers(client_id);
CREATE INDEX idx_panier_item_panier ON panier_items(panier_id);
CREATE INDEX idx_panier_item_produit ON panier_items(produit_id);
CREATE INDEX idx_panier_actif ON paniers(actif);

-- Commentaires
COMMENT ON TABLE paniers IS 'Table des paniers clients';
COMMENT ON TABLE panier_items IS 'Table des articles dans les paniers';
COMMENT ON COLUMN paniers.actif IS 'Indique si le panier est actif ou archivé';
COMMENT ON COLUMN panier_items.prix_unitaire IS 'Prix unitaire au moment de l''ajout au panier';




