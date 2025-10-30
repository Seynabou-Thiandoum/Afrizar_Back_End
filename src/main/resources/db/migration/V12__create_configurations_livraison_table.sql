-- Migration pour créer la table des configurations de livraison
-- Permet aux administrateurs de configurer les tarifs et délais de livraison

CREATE TABLE configurations_livraison (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(20) NOT NULL CHECK (type IN ('EXPRESS', 'STANDARD', 'ECONOMIQUE')),
    pays VARCHAR(100) NOT NULL,
    tarif_base DECIMAL(10,2) NOT NULL,
    tarif_par_kg DECIMAL(10,2) NOT NULL,
    delai_jours INTEGER NOT NULL,
    delai_min_jours INTEGER,
    delai_max_jours INTEGER,
    minimum_facturation DECIMAL(10,2),
    reduction_gros_colis DECIMAL(5,2), -- Pourcentage de réduction pour colis > 5kg
    supplement_ville_eloignee DECIMAL(5,2), -- Pourcentage de supplément pour villes éloignées
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    description TEXT,
    notes TEXT,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modifie_par VARCHAR(255)
);

-- Index pour améliorer les performances
CREATE INDEX idx_configurations_livraison_pays_type ON configurations_livraison(pays, type);
CREATE INDEX idx_configurations_livraison_actif ON configurations_livraison(actif);
CREATE INDEX idx_configurations_livraison_type ON configurations_livraison(type);

-- Contraintes d'unicité
CREATE UNIQUE INDEX idx_configurations_livraison_unique ON configurations_livraison(pays, type) WHERE actif = TRUE;

-- Commentaires sur la table et les colonnes
COMMENT ON TABLE configurations_livraison IS 'Configurations des tarifs et délais de livraison par pays et type';
COMMENT ON COLUMN configurations_livraison.type IS 'Type de livraison: EXPRESS, STANDARD, ECONOMIQUE';
COMMENT ON COLUMN configurations_livraison.pays IS 'Pays de destination (SENEGAL, FRANCE, etc.) ou GENERAL pour les tarifs par défaut';
COMMENT ON COLUMN configurations_livraison.tarif_base IS 'Tarif de base en FCFA';
COMMENT ON COLUMN configurations_livraison.tarif_par_kg IS 'Tarif par kilogramme en FCFA';
COMMENT ON COLUMN configurations_livraison.delai_jours IS 'Délai de livraison en jours';
COMMENT ON COLUMN configurations_livraison.delai_min_jours IS 'Délai minimum de livraison en jours';
COMMENT ON COLUMN configurations_livraison.delai_max_jours IS 'Délai maximum de livraison en jours';
COMMENT ON COLUMN configurations_livraison.minimum_facturation IS 'Montant minimum de facturation en FCFA';
COMMENT ON COLUMN configurations_livraison.reduction_gros_colis IS 'Pourcentage de réduction pour les colis de plus de 5kg';
COMMENT ON COLUMN configurations_livraison.supplement_ville_eloignee IS 'Pourcentage de supplément pour les villes éloignées';
COMMENT ON COLUMN configurations_livraison.actif IS 'Indique si la configuration est active';
COMMENT ON COLUMN configurations_livraison.description IS 'Description de la configuration';
COMMENT ON COLUMN configurations_livraison.notes IS 'Notes additionnelles';
COMMENT ON COLUMN configurations_livraison.modifie_par IS 'Email de l''administrateur qui a modifié la configuration';

