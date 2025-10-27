-- Migration V15: Add image_url column to produit_vente_flash
ALTER TABLE produit_vente_flash 
ADD COLUMN IF NOT EXISTS image_url VARCHAR(1000);

COMMENT ON COLUMN produit_vente_flash.image_url IS 'Image personnalisée pour la vente flash (optionnel). Si non fournie, l''image du produit sera utilisée.';
