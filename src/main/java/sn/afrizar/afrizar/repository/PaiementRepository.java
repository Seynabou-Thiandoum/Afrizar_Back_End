package sn.afrizar.afrizar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.Paiement;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    
    Optional<Paiement> findByNumeroTransaction(String numeroTransaction);
    
    Optional<Paiement> findByReferenceExterne(String referenceExterne);
    
    Optional<Paiement> findByCommandeId(Long commandeId);
    
    List<Paiement> findByStatut(Paiement.StatutPaiement statut);
    
    List<Paiement> findByMethode(Paiement.MethodePaiement methode);
    
    @Query("SELECT p FROM Paiement p WHERE p.dateCreation BETWEEN :debut AND :fin")
    List<Paiement> findPaiementsParPeriode(
        @Param("debut") LocalDateTime debut, 
        @Param("fin") LocalDateTime fin
    );
    
    @Query("SELECT SUM(p.montantNet) FROM Paiement p WHERE p.statut = 'REUSSI' AND p.dateCreation BETWEEN :debut AND :fin")
    BigDecimal getTotalPaiementsReussis(
        @Param("debut") LocalDateTime debut, 
        @Param("fin") LocalDateTime fin
    );
    
    @Query("SELECT COUNT(p) FROM Paiement p WHERE p.statut = :statut")
    Long countByStatut(@Param("statut") Paiement.StatutPaiement statut);
    
    @Query("SELECT p.methode, COUNT(p) FROM Paiement p WHERE p.statut = 'REUSSI' GROUP BY p.methode")
    List<Object[]> getStatistiquesPaiementsParMethode();
    
    @Query("SELECT SUM(p.fraisTransaction) FROM Paiement p WHERE p.statut = 'REUSSI' AND p.dateCreation BETWEEN :debut AND :fin")
    BigDecimal getTotalFraisTransaction(
        @Param("debut") LocalDateTime debut, 
        @Param("fin") LocalDateTime fin
    );
}

