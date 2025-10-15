-- Ajouter les colonnes pour les détails spécifiques des produits
ALTER TABLE produits
ADD COLUMN taille VARCHAR(50),
ADD COLUMN couleur VARCHAR(50),
ADD COLUMN matiere VARCHAR(100);
