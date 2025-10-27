package sn.afrizar.afrizar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.ProduitVenteFlash;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitVenteFlashRepository extends JpaRepository<ProduitVenteFlash, Long> {
    
    List<ProduitVenteFlash> findByVenteFlashId(Long venteFlashId);
    
    @Query("SELECT pvf FROM ProduitVenteFlash pvf WHERE pvf.venteFlash.id = :venteFlashId AND pvf.produit.id = :produitId")
    Optional<ProduitVenteFlash> findByVenteFlashIdAndProduitId(@Param("venteFlashId") Long venteFlashId, @Param("produitId") Long produitId);
    
    void deleteByVenteFlashId(Long venteFlashId);
    
    long countByVenteFlashId(Long venteFlashId);
}
