-- Script d'initialisation des configurations de livraison par défaut
-- Ce script crée les configurations de base pour le système

-- Configurations pour le Sénégal
INSERT INTO configurations_livraison (type, pays, tarif_base, tarif_par_kg, delai_jours, delai_min_jours, delai_max_jours, minimum_facturation, reduction_gros_colis, supplement_ville_eloignee, actif, description, modifie_par) VALUES
('EXPRESS', 'SENEGAL', 2000.00, 1000.00, 3, 2, 4, 1000.00, 10.00, 15.00, true, 'Livraison Express au Sénégal - 3 jours', 'system'),
('STANDARD', 'SENEGAL', 1000.00, 500.00, 7, 5, 10, 1000.00, 10.00, 15.00, true, 'Livraison Standard au Sénégal - 7 jours', 'system'),
('ECONOMIQUE', 'SENEGAL', 500.00, 300.00, 15, 12, 20, 1000.00, 10.00, 15.00, true, 'Livraison Économique au Sénégal - 15 jours', 'system');

-- Configurations générales pour l'international
INSERT INTO configurations_livraison (type, pays, tarif_base, tarif_par_kg, delai_jours, delai_min_jours, delai_max_jours, minimum_facturation, reduction_gros_colis, supplement_ville_eloignee, actif, description, modifie_par) VALUES
('EXPRESS', 'GENERAL', 10000.00, 3000.00, 7, 5, 10, 5000.00, 10.00, 15.00, true, 'Livraison Express International - 7 jours', 'system'),
('STANDARD', 'GENERAL', 5000.00, 2000.00, 14, 10, 18, 5000.00, 10.00, 15.00, true, 'Livraison Standard International - 14 jours', 'system'),
('ECONOMIQUE', 'GENERAL', 3000.00, 1500.00, 21, 18, 25, 5000.00, 10.00, 15.00, true, 'Livraison Économique International - 21 jours', 'system');

-- Configurations spécifiques pour la France
INSERT INTO configurations_livraison (type, pays, tarif_base, tarif_par_kg, delai_jours, delai_min_jours, delai_max_jours, minimum_facturation, reduction_gros_colis, supplement_ville_eloignee, actif, description, modifie_par) VALUES
('EXPRESS', 'FRANCE', 12000.00, 3500.00, 5, 3, 7, 5000.00, 10.00, 15.00, true, 'Livraison Express vers la France - 5 jours', 'system'),
('STANDARD', 'FRANCE', 7000.00, 2500.00, 10, 7, 14, 5000.00, 10.00, 15.00, true, 'Livraison Standard vers la France - 10 jours', 'system'),
('ECONOMIQUE', 'FRANCE', 4000.00, 1800.00, 18, 15, 22, 5000.00, 10.00, 15.00, true, 'Livraison Économique vers la France - 18 jours', 'system');

-- Configurations spécifiques pour les USA
INSERT INTO configurations_livraison (type, pays, tarif_base, tarif_par_kg, delai_jours, delai_min_jours, delai_max_jours, minimum_facturation, reduction_gros_colis, supplement_ville_eloignee, actif, description, modifie_par) VALUES
('EXPRESS', 'USA', 15000.00, 4000.00, 8, 5, 12, 5000.00, 10.00, 15.00, true, 'Livraison Express vers les USA - 8 jours', 'system'),
('STANDARD', 'USA', 9000.00, 2800.00, 15, 12, 20, 5000.00, 10.00, 15.00, true, 'Livraison Standard vers les USA - 15 jours', 'system'),
('ECONOMIQUE', 'USA', 6000.00, 2000.00, 25, 20, 30, 5000.00, 10.00, 15.00, true, 'Livraison Économique vers les USA - 25 jours', 'system');

-- Configurations spécifiques pour le Canada
INSERT INTO configurations_livraison (type, pays, tarif_base, tarif_par_kg, delai_jours, delai_min_jours, delai_max_jours, minimum_facturation, reduction_gros_colis, supplement_ville_eloignee, actif, description, modifie_par) VALUES
('EXPRESS', 'CANADA', 16000.00, 4200.00, 9, 6, 13, 5000.00, 10.00, 15.00, true, 'Livraison Express vers le Canada - 9 jours', 'system'),
('STANDARD', 'CANADA', 10000.00, 3000.00, 16, 13, 21, 5000.00, 10.00, 15.00, true, 'Livraison Standard vers le Canada - 16 jours', 'system'),
('ECONOMIQUE', 'CANADA', 7000.00, 2200.00, 26, 22, 32, 5000.00, 10.00, 15.00, true, 'Livraison Économique vers le Canada - 26 jours', 'system');

-- Afficher un message de confirmation
SELECT 'Configurations de livraison initialisées avec succès!' as message;
