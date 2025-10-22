-- Migration des données existantes vers la nouvelle structure
-- V5__Migrate_Existing_Categories.sql

-- 1. Sauvegarder les données existantes dans une table temporaire
CREATE TEMPORARY TABLE temp_old_categories AS
SELECT 
    id,
    nom,
    description,
    type,
    genre,
    parent_id,
    ordre,
    active,
    image_url,
    created_at
FROM categories
WHERE active = TRUE;

-- 2. Insérer les types uniques depuis les anciennes catégories
INSERT INTO types_categories (nom, description, type, ordre, active, image_url)
SELECT DISTINCT
    nom as nom,
    description,
    type,
    MIN(ordre) as ordre,
    active,
    image_url
FROM temp_old_categories
WHERE parent_id IS NOT NULL  -- Seulement les sous-catégories (types)
GROUP BY nom, description, type, active, image_url
ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    image_url = VALUES(image_url);

-- 3. Insérer les genres uniques depuis les anciennes catégories
INSERT INTO genres_categories (nom, description, type, ordre, active, image_url)
SELECT DISTINCT
    nom as nom,
    description,
    type,
    MIN(ordre) as ordre,
    active,
    image_url
FROM temp_old_categories
WHERE parent_id IS NULL  -- Seulement les catégories principales (genres)
GROUP BY nom, description, type, active, image_url
ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    image_url = VALUES(image_url);

-- 4. Créer les associations basées sur les anciennes relations parent-enfant
INSERT INTO categories_combinaisons (genre_id, type_id, ordre, active)
SELECT 
    g.id as genre_id,
    t.id as type_id,
    COALESCE(child.ordre, 0) as ordre,
    child.active
FROM temp_old_categories parent
JOIN temp_old_categories child ON parent.id = child.parent_id
JOIN genres_categories g ON parent.nom = g.nom AND parent.type = g.type
JOIN types_categories t ON child.nom = t.nom AND child.type = t.type
WHERE parent.parent_id IS NULL  -- Parent est un genre
  AND child.parent_id IS NOT NULL  -- Enfant est un type
ON DUPLICATE KEY UPDATE
    ordre = VALUES(ordre),
    active = VALUES(active);

-- 5. Gérer les cas où il n'y a pas de parent (catégories orphelines)
-- Ces catégories deviennent des types génériques
INSERT INTO types_categories (nom, description, type, ordre, active, image_url)
SELECT 
    nom,
    description,
    type,
    ordre,
    active,
    image_url
FROM temp_old_categories
WHERE parent_id IS NULL 
  AND nom NOT IN (SELECT nom FROM genres_categories)
  AND nom NOT IN (SELECT nom FROM types_categories)
ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    image_url = VALUES(image_url);

-- 6. Créer des associations pour les catégories orphelines avec tous les genres du même type
INSERT INTO categories_combinaisons (genre_id, type_id, ordre, active)
SELECT 
    g.id as genre_id,
    t.id as type_id,
    t.ordre,
    t.active
FROM types_categories t
JOIN genres_categories g ON t.type = g.type
WHERE t.nom IN (
    SELECT nom FROM temp_old_categories 
    WHERE parent_id IS NULL 
      AND nom NOT IN (SELECT nom FROM genres_categories)
)
ON DUPLICATE KEY UPDATE
    ordre = VALUES(ordre),
    active = VALUES(active);

-- 7. Mettre à jour les produits pour utiliser les nouvelles associations
-- Créer une table de mapping temporaire
CREATE TEMPORARY TABLE temp_category_mapping AS
SELECT 
    old_cat.id as old_category_id,
    COALESCE(comb.id, t.id) as new_category_id,
    CASE 
        WHEN comb.id IS NOT NULL THEN 'COMBINAISON'
        ELSE 'TYPE'
    END as category_type
FROM temp_old_categories old_cat
LEFT JOIN categories_combinaisons comb ON (
    old_cat.parent_id IS NOT NULL 
    AND EXISTS (
        SELECT 1 FROM temp_old_categories parent 
        WHERE parent.id = old_cat.parent_id
    )
    AND EXISTS (
        SELECT 1 FROM genres_categories g 
        JOIN temp_old_categories parent ON parent.id = old_cat.parent_id
        WHERE g.nom = parent.nom AND g.type = parent.type
    )
    AND EXISTS (
        SELECT 1 FROM types_categories t 
        WHERE t.nom = old_cat.nom AND t.type = old_cat.type
    )
    AND comb.genre_id = (
        SELECT g.id FROM genres_categories g 
        JOIN temp_old_categories parent ON parent.id = old_cat.parent_id
        WHERE g.nom = parent.nom AND g.type = parent.type
    )
    AND comb.type_id = (
        SELECT t.id FROM types_categories t 
        WHERE t.nom = old_cat.nom AND t.type = old_cat.type
    )
)
LEFT JOIN types_categories t ON (
    old_cat.nom = t.nom AND old_cat.type = t.type
    AND comb.id IS NULL
);

-- 8. Afficher les statistiques de migration
SELECT 'Anciennes catégories:' as info, COUNT(*) as count FROM temp_old_categories;
SELECT 'Types créés:' as info, COUNT(*) as count FROM types_categories;
SELECT 'Genres créés:' as info, COUNT(*) as count FROM genres_categories;
SELECT 'Associations créées:' as info, COUNT(*) as count FROM categories_combinaisons;
SELECT 'Mappings créés:' as info, COUNT(*) as count FROM temp_category_mapping;

-- 9. Vérifier la cohérence des données
SELECT 
    'Vérification des associations' as check_type,
    COUNT(*) as total_associations,
    COUNT(DISTINCT genre_id) as genres_uniques,
    COUNT(DISTINCT type_id) as types_uniques
FROM categories_combinaisons
WHERE active = TRUE;

-- 10. Afficher un exemple de la nouvelle structure
SELECT 
    'Structure finale:' as info,
    g.nom as genre,
    g.type as genre_type,
    t.nom as type,
    t.type as type_category,
    c.ordre
FROM categories_combinaisons c
JOIN genres_categories g ON c.genre_id = g.id
JOIN types_categories t ON c.type_id = t.id
WHERE c.active = TRUE
ORDER BY g.type, g.ordre, c.ordre
LIMIT 20;

-- 11. Nettoyer les tables temporaires
DROP TEMPORARY TABLE temp_old_categories;
DROP TEMPORARY TABLE temp_category_mapping;

-- 12. Message de fin
SELECT 'Migration terminée avec succès!' as status;

