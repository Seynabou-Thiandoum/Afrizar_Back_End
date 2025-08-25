package sn.afrizar.afrizar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.Commande;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {
    
    Optional<Commande> findByNumeroCommande(String numeroCommande);
    
    List<Commande> findByClientId(Long clientId);
    
    Page<Commande> findByClientIdOrderByDateCreationDesc(Long clientId, Pageable pageable);
    
    List<Commande> findByStatut(Commande.StatutCommande statut);
    
    List<Commande> findByType(Commande.TypeCommande type);
    
    @Query("SELECT c FROM Commande c WHERE c.dateCreation BETWEEN :debut AND :fin")
    List<Commande> findCommandesParPeriode(
        @Param("debut") LocalDateTime debut, 
        @Param("fin") LocalDateTime fin
    );
    
    @Query("SELECT c FROM Commande c JOIN c.lignesCommande lc WHERE lc.produit.vendeur.id = :vendeurId")
    List<Commande> findCommandesByVendeur(@Param("vendeurId") Long vendeurId);
    
    @Query("SELECT c FROM Commande c JOIN c.lignesCommande lc WHERE lc.produit.vendeur.id = :vendeurId ORDER BY c.dateCreation DESC")
    Page<Commande> findCommandesByVendeurOrderByDateDesc(@Param("vendeurId") Long vendeurId, Pageable pageable);
    
    // Statistiques pour dashboard
    @Query("SELECT COUNT(c) FROM Commande c WHERE c.statut = :statut")
    Long countByStatut(@Param("statut") Commande.StatutCommande statut);
    
    @Query("SELECT SUM(c.montantTotal) FROM Commande c WHERE c.statut IN ('CONFIRMEE', 'EN_PREPARATION', 'PRETE', 'EXPEDIEE', 'EN_LIVRAISON', 'LIVREE')")
    BigDecimal getTotalChiffreAffaires();
    
    @Query("SELECT SUM(c.montantTotal) FROM Commande c WHERE c.dateCreation >= :debut AND c.statut IN ('CONFIRMEE', 'EN_PREPARATION', 'PRETE', 'EXPEDIEE', 'EN_LIVRAISON', 'LIVREE')")
    BigDecimal getChiffreAffairesDepuis(@Param("debut") LocalDateTime debut);
    
    @Query("SELECT COUNT(c) FROM Commande c WHERE c.client.id = :clientId")
    Long countCommandesByClient(@Param("clientId") Long clientId);
    
    @Query("SELECT AVG(c.montantTotal) FROM Commande c WHERE c.statut IN ('CONFIRMEE', 'EN_PREPARATION', 'PRETE', 'EXPEDIEE', 'EN_LIVRAISON', 'LIVREE')")
    BigDecimal getMoyennePanier();
    
    // Commandes en retard
    @Query("SELECT c FROM Commande c WHERE c.dateLivraisonEstimee < CURRENT_DATE AND c.statut NOT IN ('LIVREE', 'ANNULEE', 'RETOURNEE')")
    List<Commande> findCommandesEnRetard();
    
    // Recherche avancée
    @Query("SELECT c FROM Commande c WHERE " +
           "(:clientId IS NULL OR c.client.id = :clientId) AND " +
           "(:statut IS NULL OR c.statut = :statut) AND " +
           "(:montantMin IS NULL OR c.montantTotal >= :montantMin) AND " +
           "(:montantMax IS NULL OR c.montantTotal <= :montantMax) AND " +
           "(:dateDebut IS NULL OR c.dateCreation >= :dateDebut) AND " +
           "(:dateFin IS NULL OR c.dateCreation <= :dateFin)")
    Page<Commande> findCommandesAvecFiltres(
        @Param("clientId") Long clientId,
        @Param("statut") Commande.StatutCommande statut,
        @Param("montantMin") BigDecimal montantMin,
        @Param("montantMax") BigDecimal montantMax,
        @Param("dateDebut") LocalDateTime dateDebut,
        @Param("dateFin") LocalDateTime dateFin,
        Pageable pageable
    );
}

