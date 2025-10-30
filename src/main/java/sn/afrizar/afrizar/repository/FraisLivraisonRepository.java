package sn.afrizar.afrizar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.FraisLivraison;

import java.util.List;

@Repository
public interface FraisLivraisonRepository extends JpaRepository<FraisLivraison, Long> {
    
    List<FraisLivraison> findByActifTrueOrderByType();
    
    Page<FraisLivraison> findByActifTrueOrderByType(Pageable pageable);
    
    List<FraisLivraison> findByTypeAndActifTrue(FraisLivraison.TypeLivraison type);
    
    @Query("SELECT f FROM FraisLivraison f WHERE f.actif = true AND " +
           "(:poids IS NULL OR (f.poidsMin IS NULL OR f.poidsMin <= :poids) AND " +
           "(f.poidsMax IS NULL OR f.poidsMax >= :poids)) AND " +
           "(:zone IS NULL OR f.zone IS NULL OR f.zone = :zone)")
    List<FraisLivraison> findApplicableFraisLivraison(@Param("poids") Double poids, @Param("zone") String zone);
    
    long countByActifTrue();
}
