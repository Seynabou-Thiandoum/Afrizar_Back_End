package sn.afrizar.afrizar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.LigneCommande;

import java.util.List;

@Repository
public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {
    
    List<LigneCommande> findByCommandeId(Long commandeId);
    
    List<LigneCommande> findByProduitId(Long produitId);
    
    @Query("SELECT lc FROM LigneCommande lc WHERE lc.produit.vendeur.id = :vendeurId")
    List<LigneCommande> findByVendeur(@Param("vendeurId") Long vendeurId);
    
    @Query("SELECT SUM(lc.quantite) FROM LigneCommande lc WHERE lc.produit.id = :produitId")
    Long getTotalQuantiteVendueParProduit(@Param("produitId") Long produitId);
    
    @Query("SELECT lc.produit.id, SUM(lc.quantite) FROM LigneCommande lc " +
           "WHERE lc.produit.vendeur.id = :vendeurId " +
           "GROUP BY lc.produit.id " +
           "ORDER BY SUM(lc.quantite) DESC")
    List<Object[]> getProduitsLesPlusVendusParVendeur(@Param("vendeurId") Long vendeurId);
}

