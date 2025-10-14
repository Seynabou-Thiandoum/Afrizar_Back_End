package sn.afrizar.afrizar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.Vendeur;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendeurRepository extends JpaRepository<Vendeur, Long> {
    
    Optional<Vendeur> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<Vendeur> findByVerifie(boolean verifie);
    
    Page<Vendeur> findByVerifie(boolean verifie, Pageable pageable);
    
    long countByVerifie(boolean verifie);
    
    List<Vendeur> findByActif(boolean actif);
    
    @Query("SELECT v FROM Vendeur v WHERE v.rating >= :rating ORDER BY v.rating DESC")
    List<Vendeur> findByRatingGreaterThanEqualOrderByRatingDesc(@Param("rating") BigDecimal rating);
    
    @Query("SELECT v FROM Vendeur v WHERE v.nomBoutique LIKE %:nom% OR v.specialites LIKE %:specialite%")
    List<Vendeur> findByNomBoutiqueContainingOrSpecialitesContaining(
        @Param("nom") String nom, 
        @Param("specialite") String specialite
    );
    
    @Query("SELECT v FROM Vendeur v WHERE v.verifie = true AND v.actif = true ORDER BY v.rating DESC, v.nombreEvaluations DESC")
    List<Vendeur> findVendeursVerifiesOrderByRating();
    
    @Query("SELECT COUNT(v) FROM Vendeur v WHERE v.verifie = true AND v.actif = true")
    Long countVendeursVerifies();
    
    List<Vendeur> findByPublieAndActifAndVerifie(boolean publie, boolean actif, boolean verifie);
    
    @Query("SELECT v FROM Vendeur v WHERE SIZE(v.produits) >= :nombreProduits")
    List<Vendeur> findVendeursAvecPlusieursProduitsOrderByNomBoutique(@Param("nombreProduits") int nombreProduits);
}
