-- Création de la table favoris
CREATE TABLE IF NOT EXISTS favoris (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    produit_id BIGINT NOT NULL,
    date_ajout TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_favori_client FOREIGN KEY (client_id) REFERENCES utilisateur(id) ON DELETE CASCADE,
    CONSTRAINT fk_favori_produit FOREIGN KEY (produit_id) REFERENCES produit(id) ON DELETE CASCADE,
    CONSTRAINT uk_favori_client_produit UNIQUE (client_id, produit_id)
);

-- Index pour améliorer les performances
CREATE INDEX idx_favoris_client ON favoris(client_id);
CREATE INDEX idx_favoris_produit ON favoris(produit_id);
CREATE INDEX idx_favoris_date_ajout ON favoris(date_ajout DESC);




