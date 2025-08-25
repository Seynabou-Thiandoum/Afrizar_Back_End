package sn.afrizar.afrizar.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.afrizar.afrizar.dto.CommandeDto;
import sn.afrizar.afrizar.dto.CreateCommandeDto;
import sn.afrizar.afrizar.model.Commande;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommandeService {
    
    CommandeDto creerCommande(CreateCommandeDto createCommandeDto);
    
    Optional<CommandeDto> obtenirCommandeParId(Long id);
    
    Optional<CommandeDto> obtenirCommandeParNumero(String numeroCommande);
    
    List<CommandeDto> obtenirCommandesParClient(Long clientId);
    
    Page<CommandeDto> obtenirCommandesParClientAvecPagination(Long clientId, Pageable pageable);
    
    List<CommandeDto> obtenirCommandesParVendeur(Long vendeurId);
    
    Page<CommandeDto> obtenirCommandesParVendeurAvecPagination(Long vendeurId, Pageable pageable);
    
    List<CommandeDto> obtenirCommandesParStatut(Commande.StatutCommande statut);
    
    CommandeDto changerStatutCommande(Long commandeId, Commande.StatutCommande nouveauStatut);
    
    CommandeDto confirmerCommande(Long commandeId);
    
    CommandeDto annulerCommande(Long commandeId, String motif);
    
    CommandeDto expedierCommande(Long commandeId, String numeroSuivi, String transporteur);
    
    CommandeDto livrerCommande(Long commandeId);
    
    Page<CommandeDto> rechercherCommandesAvecFiltres(
        Long clientId,
        Commande.StatutCommande statut,
        BigDecimal montantMin,
        BigDecimal montantMax,
        LocalDateTime dateDebut,
        LocalDateTime dateFin,
        Pageable pageable
    );
    
    List<CommandeDto> obtenirCommandesEnRetard();
    
    List<CommandeDto> obtenirCommandesParPeriode(LocalDateTime debut, LocalDateTime fin);
    
    // Calculs et logique métier
    CommandeDto calculerTotauxCommande(CreateCommandeDto createCommandeDto);
    
    BigDecimal calculerCommissionTotale(Long commandeId);
    
    BigDecimal calculerFraisLivraison(Long commandeId);
    
    // Statistiques
    Long getNombreCommandesParStatut(Commande.StatutCommande statut);
    
    BigDecimal getTotalChiffreAffaires();
    
    BigDecimal getChiffreAffairesDepuis(LocalDateTime depuis);
    
    BigDecimal getMoyennePanier();
    
    Long getNombreCommandesParClient(Long clientId);
}

