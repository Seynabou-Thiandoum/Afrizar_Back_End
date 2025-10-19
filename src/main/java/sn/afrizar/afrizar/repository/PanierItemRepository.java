package sn.afrizar.afrizar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.PanierItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface PanierItemRepository extends JpaRepository<PanierItem, Long> {
    
    // Trouver tous les items d'un panier
    List<PanierItem> findByPanierId(Long panierId);
    
    // Trouver un item spécifique dans le panier
    @Query("SELECT pi FROM PanierItem pi WHERE pi.panier.id = :panierId AND pi.produit.id = :produitId " +
           "AND (pi.taille = :taille OR (pi.taille IS NULL AND :taille IS NULL)) " +
           "AND (pi.couleur = :couleur OR (pi.couleur IS NULL AND :couleur IS NULL))")
    Optional<PanierItem> findByPanierIdAndProduitIdAndOptions(
            @Param("panierId") Long panierId,
            @Param("produitId") Long produitId,
            @Param("taille") String taille,
            @Param("couleur") String couleur);
    
    // Supprimer tous les items d'un panier
    void deleteByPanierId(Long panierId);
}

