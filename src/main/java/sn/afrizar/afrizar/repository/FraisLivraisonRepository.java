package sn.afrizar.afrizar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.FraisLivraison;

import java.util.List;
import java.util.Optional;

@Repository
public interface FraisLivraisonRepository extends JpaRepository<FraisLivraison, Long> {
    
    List<FraisLivraison> findByActifTrueOrderByTypeAsc();
    
    List<FraisLivraison> findByTypeAndActifTrue(FraisLivraison.TypeLivraison type);
    
    Optional<FraisLivraison> findByNomAndActifTrue(String nom);
    
    @Query("SELECT f FROM FraisLivraison f WHERE f.actif = true AND f.zone = :zone ORDER BY f.type ASC")
    List<FraisLivraison> findByZoneAndActifTrue(String zone);
    
    @Query("SELECT f FROM FraisLivraison f WHERE f.actif = true AND " +
           "(:poids IS NULL OR (f.poidsMin IS NULL OR f.poidsMin <= :poids) AND " +
           "(f.poidsMax IS NULL OR f.poidsMax >= :poids)) " +
           "ORDER BY f.type ASC")
    List<FraisLivraison> findApplicableByPoids(java.math.BigDecimal poids);
}
