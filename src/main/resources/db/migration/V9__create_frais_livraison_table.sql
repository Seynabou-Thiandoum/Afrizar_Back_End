-- Création de la table frais_livraison
CREATE TABLE frais_livraison (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    type VARCHAR(50) NOT NULL,
    frais NUMERIC(10, 2) NOT NULL,
    delai_min_jours INTEGER NOT NULL,
    delai_max_jours INTEGER NOT NULL,
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP WITHOUT TIME ZONE,
    poids_min NUMERIC(8, 3),
    poids_max NUMERIC(8, 3),
    zone VARCHAR(100)
);

-- Insertion des frais de livraison par défaut
INSERT INTO frais_livraison (nom, description, type, frais, delai_min_jours, delai_max_jours, actif, zone) VALUES
('Express Dakar', 'Livraison express dans Dakar et banlieue', 'EXPRESS', 2000.00, 3, 5, TRUE, 'DAKAR'),
('Express Autres villes', 'Livraison express dans les autres villes', 'EXPRESS', 3000.00, 3, 5, TRUE, 'AUTRES'),
('Standard Dakar', 'Livraison standard dans Dakar et banlieue', 'STANDARD', 1000.00, 10, 15, TRUE, 'DAKAR'),
('Standard Autres villes', 'Livraison standard dans les autres villes', 'STANDARD', 1500.00, 10, 15, TRUE, 'AUTRES'),
('Express International', 'Livraison express vers l''international', 'EXPRESS', 15000.00, 3, 5, TRUE, 'INTERNATIONAL'),
('Standard International', 'Livraison standard vers l''international', 'STANDARD', 8000.00, 10, 15, TRUE, 'INTERNATIONAL');

-- Création d'index pour optimiser les requêtes
CREATE INDEX idx_frais_livraison_type ON frais_livraison(type);
CREATE INDEX idx_frais_livraison_actif ON frais_livraison(actif);
CREATE INDEX idx_frais_livraison_zone ON frais_livraison(zone);
CREATE INDEX idx_frais_livraison_poids ON frais_livraison(poids_min, poids_max);


