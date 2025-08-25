package sn.afrizar.afrizar.service;

import sn.afrizar.afrizar.dto.CreateLivraisonDto;
import sn.afrizar.afrizar.dto.LivraisonDto;
import sn.afrizar.afrizar.model.Livraison;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LivraisonService {
    
    LivraisonDto creerLivraison(CreateLivraisonDto createLivraisonDto, Long commandeId);
    
    Optional<LivraisonDto> obtenirLivraisonParId(Long id);
    
    Optional<LivraisonDto> obtenirLivraisonParCommande(Long commandeId);
    
    Optional<LivraisonDto> obtenirLivraisonParNumeroSuivi(String numeroSuivi);
    
    LivraisonDto mettreAJourLivraison(Long id, CreateLivraisonDto createLivraisonDto);
    
    LivraisonDto changerStatutLivraison(Long id, Livraison.StatutLivraison nouveauStatut);
    
    LivraisonDto expedierCommande(Long livraisonId, String numeroSuivi, String transporteur);
    
    LivraisonDto livrerCommande(Long livraisonId);
    
    List<LivraisonDto> obtenirLivraisonsParStatut(Livraison.StatutLivraison statut);
    
    List<LivraisonDto> obtenirLivraisonsParPays(String pays);
    
    List<LivraisonDto> obtenirExpeditionsParDate(LocalDate date);
    
    List<LivraisonDto> obtenirLivraisonsPrevuesParDate(LocalDate date);
    
    List<LivraisonDto> obtenirLivraisonsEnRetard();
    
    /**
     * Calcule le coût de livraison en fonction du poids, destination et type
     */
    BigDecimal calculerCoutLivraison(BigDecimal poids, String pays, String ville, Livraison.TypeLivraison type);
    
    /**
     * Calcule la date de livraison estimée
     */
    LocalDate calculerDateLivraisonEstimee(String pays, Livraison.TypeLivraison type);
    
    /**
     * Génère un numéro de suivi unique
     */
    String genererNumeroSuivi();
    
    // Statistiques
    Long getNombreLivraisonsParStatut(Livraison.StatutLivraison statut);
    
    List<Object[]> getStatistiquesLivraisonsParPays();
    
    List<Object[]> getDelaiMoyenLivraisonParType();
}

