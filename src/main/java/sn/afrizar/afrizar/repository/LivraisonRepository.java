package sn.afrizar.afrizar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.Livraison;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LivraisonRepository extends JpaRepository<Livraison, Long> {
    
    Optional<Livraison> findByCommandeId(Long commandeId);
    
    Optional<Livraison> findByNumeroSuivi(String numeroSuivi);
    
    List<Livraison> findByStatut(Livraison.StatutLivraison statut);
    
    List<Livraison> findByType(Livraison.TypeLivraison type);
    
    List<Livraison> findByPays(String pays);
    
    List<Livraison> findByTransporteur(String transporteur);
    
    @Query("SELECT l FROM Livraison l WHERE l.dateExpedition = :date")
    List<Livraison> findExpeditionsParDate(@Param("date") LocalDate date);
    
    @Query("SELECT l FROM Livraison l WHERE l.dateLivraisonPrevue = :date")
    List<Livraison> findLivraisonsPrevuesParDate(@Param("date") LocalDate date);
    
    @Query("SELECT l FROM Livraison l WHERE l.dateLivraisonPrevue < :date AND l.statut NOT IN ('LIVRE', 'ECHEC_LIVRAISON', 'RETOURNE')")
    List<Livraison> findLivraisonsEnRetard(@Param("date") LocalDate date);
    
    @Query("SELECT COUNT(l) FROM Livraison l WHERE l.statut = :statut")
    Long countByStatut(@Param("statut") Livraison.StatutLivraison statut);
    
    @Query("SELECT l.pays, COUNT(l) FROM Livraison l GROUP BY l.pays ORDER BY COUNT(l) DESC")
    List<Object[]> getStatistiquesLivraisonsParPays();
    
    @Query("SELECT l.type, AVG(DATEDIFF(DAY, l.dateExpedition, l.dateLivraisonEffective)) FROM Livraison l WHERE l.dateLivraisonEffective IS NOT NULL GROUP BY l.type")
    List<Object[]> getDelaiMoyenLivraisonParType();
}

