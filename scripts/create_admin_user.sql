-- Script pour vérifier et créer un utilisateur ADMIN

-- 1. Vérifier les utilisateurs existants
SELECT id, nom, prenom, email, role, actif 
FROM utilisateur;

-- 2. Vérifier si un admin existe déjà
SELECT * FROM utilisateur WHERE role = 'ADMIN';

-- 3. Si pas d'admin, créer un admin de test
-- IMPORTANT : Changez l'email et le mot de passe !
-- Le mot de passe doit être haché (utilisez BCrypt)
-- Mot de passe par défaut : admin123

INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, telephone, role, actif, date_creation)
VALUES (
  'Admin',
  'Afrizar',
  'admin@afrizar.sn',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- BCrypt pour "admin123"
  '+221700000000',
  'ADMIN',
  true,
  NOW()
);

-- 4. OU, changer le rôle d'un utilisateur existant en ADMIN
-- Remplacez 'votre-email@example.com' par votre email
UPDATE utilisateur 
SET role = 'ADMIN' 
WHERE email = 'votre-email@example.com';

-- 5. Vérifier que l'admin a été créé
SELECT id, nom, prenom, email, role, actif 
FROM utilisateur 
WHERE role = 'ADMIN';


