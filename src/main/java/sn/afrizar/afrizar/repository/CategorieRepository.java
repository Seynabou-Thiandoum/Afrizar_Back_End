package sn.afrizar.afrizar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.Categorie;

import java.util.List;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    
    List<Categorie> findByActiveTrue();
    
    List<Categorie> findByParentIsNullAndActiveTrueOrderByOrdre();
    
    List<Categorie> findByParentIdAndActiveTrueOrderByOrdre(Long parentId);
    
    @Query("SELECT c FROM Categorie c WHERE LOWER(c.nom) LIKE LOWER(CONCAT('%', :nom, '%')) AND c.active = true")
    List<Categorie> findByNomContainingIgnoreCaseAndActiveTrue(@Param("nom") String nom);
    
    boolean existsByNomAndActiveTrue(String nom);
    
    @Query("SELECT c FROM Categorie c WHERE c.parent IS NULL AND c.active = true ORDER BY c.ordre ASC")
    List<Categorie> findCategoriesRacines();
    
    @Query("SELECT c FROM Categorie c LEFT JOIN c.produits p WHERE c.active = true GROUP BY c ORDER BY COUNT(p) DESC")
    List<Categorie> findCategoriesOrderByNombreProduits();
    
    @Query("SELECT COUNT(p) FROM Produit p WHERE p.categorie.id = :categorieId AND p.statut = 'ACTIF'")
    Long countProduitsActifsByCategorie(@Param("categorieId") Long categorieId);
    
    // Trouver par slug
    Categorie findBySlugAndActiveTrue(String slug);
    
    // Trouver toutes les catégories avec leurs sous-catégories
    @Query("SELECT c FROM Categorie c LEFT JOIN FETCH c.sousCategories WHERE c.parent IS NULL AND c.active = true ORDER BY c.ordre ASC")
    List<Categorie> findCategoriesRacinesAvecSousCategories();
}

