package sn.afrizar.afrizar.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.afrizar.afrizar.dto.FraisLivraisonDto;

import java.math.BigDecimal;
import java.util.List;

public interface FraisLivraisonService {
    
    // CRUD operations
    FraisLivraisonDto creerFraisLivraison(FraisLivraisonDto fraisLivraisonDto);
    FraisLivraisonDto obtenirFraisLivraison(Long id);
    Page<FraisLivraisonDto> obtenirTousLesFraisLivraison(Pageable pageable);
    FraisLivraisonDto mettreAJourFraisLivraison(Long id, FraisLivraisonDto fraisLivraisonDto);
    void supprimerFraisLivraison(Long id);
    
    // Business operations
    List<FraisLivraisonDto> obtenirFraisLivraisonActifs();
    List<FraisLivraisonDto> obtenirFraisLivraisonParType(String type);
    List<FraisLivraisonDto> obtenirFraisLivraisonApplicables(BigDecimal poids, String zone);
    FraisLivraisonDto activerFraisLivraison(Long id);
    FraisLivraisonDto desactiverFraisLivraison(Long id);
    
    // Public API for clients
    List<FraisLivraisonDto> obtenirOptionsLivraison();
    FraisLivraisonDto calculerFraisLivraison(String type, BigDecimal poids, String zone);
}


