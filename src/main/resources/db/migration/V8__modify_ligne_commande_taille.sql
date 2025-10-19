-- ============================================
-- MIGRATION V8 : MODIFIER LE TYPE DE TAILLE DANS LIGNE_COMMANDES
-- ============================================

-- Modifier la colonne taille pour accepter des valeurs String au lieu d'un enum
ALTER TABLE ligne_commandes 
ALTER COLUMN taille TYPE VARCHAR(50);

-- Commentaire
COMMENT ON COLUMN ligne_commandes.taille IS 'Taille sélectionnée (format texte pour flexibilité)';

