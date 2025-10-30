-- Migration pour ajouter les champs publie et photoUrl à la table vendeur
-- Date: 2025-10-13

-- Ajouter le champ publie (par défaut false)
ALTER TABLE utilisateur ADD COLUMN IF NOT EXISTS publie BOOLEAN DEFAULT FALSE;

-- Ajouter le champ photoUrl (optionnel)
ALTER TABLE utilisateur ADD COLUMN IF NOT EXISTS photo_url VARCHAR(500);

-- Mettre à jour les commentaires
COMMENT ON COLUMN utilisateur.publie IS 'Indique si le vendeur est publié sur la page publique';
COMMENT ON COLUMN utilisateur.photo_url IS 'URL de la photo de profil du vendeur (optionnel)';




