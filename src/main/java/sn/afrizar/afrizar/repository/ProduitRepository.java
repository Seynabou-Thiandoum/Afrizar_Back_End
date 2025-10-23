package sn.afrizar.afrizar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.Produit;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    
    // Recherche par vendeur
    List<Produit> findByVendeurId(Long vendeurId);
    
    // Recherche par catégorie
    List<Produit> findByCategorieId(Long categorieId);
    
    // Recherche par statut
    List<Produit> findByStatut(Produit.StatutProduit statut);
    
    Page<Produit> findByStatut(Produit.StatutProduit statut, Pageable pageable);
    
    long countByStatut(Produit.StatutProduit statut);
    
    // Recherche par disponibilité
    List<Produit> findByDisponibilite(Produit.Disponibilite disponibilite);
    
    // Recherche par nom (insensible à la casse)
    @Query("SELECT p FROM Produit p WHERE LOWER(p.nom) LIKE LOWER(CONCAT('%', :nom, '%'))")
    List<Produit> findByNomContainingIgnoreCase(@Param("nom") String nom);
    
    // Recherche avec pagination et filtres
    @Query("SELECT p FROM Produit p WHERE " +
           "(:nom IS NULL OR LOWER(p.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
           "(:categorieId IS NULL OR p.categorie.id = :categorieId) AND " +
           "(:vendeurId IS NULL OR p.vendeur.id = :vendeurId) AND " +
           "(:prixMin IS NULL OR p.prix >= :prixMin) AND " +
           "(:prixMax IS NULL OR p.prix <= :prixMax) AND " +
           "(:qualite IS NULL OR p.qualite = :qualite) AND " +
           "p.statut = 'ACTIF'")
    Page<Produit> findProduitsAvecFiltres(
        @Param("nom") String nom,
        @Param("categorieId") Long categorieId,
        @Param("vendeurId") Long vendeurId,
        @Param("prixMin") BigDecimal prixMin,
        @Param("prixMax") BigDecimal prixMax,
        @Param("qualite") Produit.Qualite qualite,
        Pageable pageable
    );
    
    // Produits en stock
    @Query("SELECT p FROM Produit p WHERE p.stock > 0 AND p.statut = 'ACTIF'")
    List<Produit> findProduitsEnStock();
    
    // Produits sur commande
    @Query("SELECT p FROM Produit p WHERE p.disponibilite = 'SUR_COMMANDE' AND p.statut = 'ACTIF'")
    List<Produit> findProduitsSurCommande();
    
    // Produits les mieux notés
    @Query("SELECT p FROM Produit p WHERE p.statut = 'ACTIF' ORDER BY p.noteMoyenne DESC, p.nombreEvaluations DESC")
    Page<Produit> findProduitsMieuxNotes(Pageable pageable);
    
    // Produits les plus vus
    @Query("SELECT p FROM Produit p WHERE p.statut = 'ACTIF' ORDER BY p.nombreVues DESC")
    Page<Produit> findProduitsPlusVus(Pageable pageable);
    
    // Recherche par taille
    @Query("SELECT p FROM Produit p JOIN p.taillesDisponibles t WHERE t = :taille AND p.statut = 'ACTIF'")
    List<Produit> findByTailleDisponible(@Param("taille") Produit.Taille taille);
    
    // Statistiques
    @Query("SELECT COUNT(p) FROM Produit p WHERE p.vendeur.id = :vendeurId AND p.statut = 'ACTIF'")
    Long countProduitsByVendeur(@Param("vendeurId") Long vendeurId);
    
    @Query("SELECT AVG(p.prix) FROM Produit p WHERE p.statut = 'ACTIF'")
    BigDecimal getMoyennePrix();
    
    @Query("SELECT SUM(p.stock) FROM Produit p WHERE p.vendeur.id = :vendeurId AND p.statut = 'ACTIF'")
    Long getTotalStockByVendeur(@Param("vendeurId") Long vendeurId);
    
    // Méthodes pour la gestion des tendances
    List<Produit> findByPrixPromoIsNotNullAndStatut(Produit.StatutProduit statut);
    
    @Query("SELECT p FROM Produit p WHERE p.statut = :statut ORDER BY p.dateCreation DESC")
    List<Produit> findTopByStatutOrderByDateCreationDesc(@Param("statut") Produit.StatutProduit statut, Pageable pageable);
    
    @Query("SELECT p FROM Produit p WHERE p.statut = :statut ORDER BY p.nombreVues DESC")
    List<Produit> findTopByStatutOrderByNombreVuesDesc(@Param("statut") Produit.StatutProduit statut, Pageable pageable);
    
    long countByDisponibilite(Produit.Disponibilite disponibilite);
    
    long countByPrixPromoIsNotNullAndStatut(Produit.StatutProduit statut);
    
    long countByNombreVuesGreaterThanAndStatut(Long nombreVues, Produit.StatutProduit statut);
    
    long countByNoteMoyenneGreaterThanAndStatut(BigDecimal noteMoyenne, Produit.StatutProduit statut);
    
    @Query("SELECT c.nom, COUNT(p) FROM Produit p LEFT JOIN p.categorie c WHERE p.statut = 'ACTIF' GROUP BY c.nom")
    List<Object[]> countProduitsByCategorie();
}

