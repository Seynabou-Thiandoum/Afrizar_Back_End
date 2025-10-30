-- Migration pour créer la table des modes de paiement configurables
CREATE TABLE modes_paiement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    logo VARCHAR(255),
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    configuration TEXT,
    instructions VARCHAR(1000),
    frais_pourcentage DECIMAL(5, 2) DEFAULT 0.00,
    frais_fixe DECIMAL(8, 2) DEFAULT 0.00,
    montant_minimum DECIMAL(10, 2),
    montant_maximum DECIMAL(10, 2),
    pays_supportes VARCHAR(500),
    delai_traitement INT,
    ordre INT NOT NULL DEFAULT 0,
    callback_url VARCHAR(255),
    environnement VARCHAR(20) NOT NULL DEFAULT 'PRODUCTION',
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME,
    modifie_par VARCHAR(100),
    INDEX idx_actif (actif),
    INDEX idx_type (type),
    INDEX idx_ordre (ordre)
);

-- Insertion des modes de paiement par défaut
INSERT INTO modes_paiement (nom, code, type, description, actif, ordre, instructions) VALUES
('Carte Bancaire', 'CARTE_BANCAIRE', 'CARTE_BANCAIRE', 'Paiement sécurisé par carte bancaire Visa, Mastercard', TRUE, 1, 'Entrez vos informations de carte bancaire de manière sécurisée'),
('Orange Money', 'ORANGE_MONEY', 'MOBILE_MONEY', 'Paiement mobile via Orange Money', TRUE, 2, 'Composez #144# pour valider le paiement depuis votre téléphone'),
('Wave', 'WAVE', 'MOBILE_MONEY', 'Paiement mobile via Wave', TRUE, 3, 'Validez le paiement dans votre application Wave'),
('Paiement à la livraison', 'CASH_ON_DELIVERY', 'CASH', 'Payez en espèces lors de la réception', TRUE, 4, 'Préparez le montant exact à remettre au livreur'),
('Virement Bancaire', 'VIREMENT', 'VIREMENT_BANCAIRE', 'Virement bancaire direct', FALSE, 5, 'Effectuez un virement vers notre compte bancaire'),
('PayPal', 'PAYPAL', 'PORTEFEUILLE', 'Paiement via PayPal', FALSE, 6, 'Connectez-vous à votre compte PayPal pour finaliser le paiement');

-- Ajouter une colonne à la table paiements pour référencer le mode de paiement configuré
ALTER TABLE paiements ADD COLUMN mode_paiement_id BIGINT;
ALTER TABLE paiements ADD CONSTRAINT fk_paiement_mode FOREIGN KEY (mode_paiement_id) REFERENCES modes_paiement(id);
ALTER TABLE paiements ADD INDEX idx_mode_paiement (mode_paiement_id);

-- Mise à jour des paiements existants pour mapper les anciennes valeurs enum
UPDATE paiements p 
SET p.mode_paiement_id = (SELECT id FROM modes_paiement WHERE code = 'CARTE_BANCAIRE' LIMIT 1)
WHERE p.methode = 'CARTE_CREDIT' OR p.methode = 'CARTE_DEBIT';

UPDATE paiements p 
SET p.mode_paiement_id = (SELECT id FROM modes_paiement WHERE code = 'ORANGE_MONEY' LIMIT 1)
WHERE p.methode = 'ORANGE_MONEY';

UPDATE paiements p 
SET p.mode_paiement_id = (SELECT id FROM modes_paiement WHERE code = 'WAVE' LIMIT 1)
WHERE p.methode = 'WAVE';

UPDATE paiements p 
SET p.mode_paiement_id = (SELECT id FROM modes_paiement WHERE code = 'CASH_ON_DELIVERY' LIMIT 1)
WHERE p.methode = 'ESPECES';

UPDATE paiements p 
SET p.mode_paiement_id = (SELECT id FROM modes_paiement WHERE code = 'PAYPAL' LIMIT 1)
WHERE p.methode = 'PAYPAL';

UPDATE paiements p 
SET p.mode_paiement_id = (SELECT id FROM modes_paiement WHERE code = 'VIREMENT' LIMIT 1)
WHERE p.methode = 'VIREMENT_BANCAIRE';


