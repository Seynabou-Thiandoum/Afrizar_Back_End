package sn.afrizar.afrizar.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.afrizar.afrizar.dto.FraisLivraisonDto;
import sn.afrizar.afrizar.model.FraisLivraison;

import java.util.List;
import java.util.Optional;

public interface FraisLivraisonService {
    
    FraisLivraisonDto creerFraisLivraison(FraisLivraisonDto fraisLivraisonDto);
    
    Optional<FraisLivraisonDto> obtenirFraisLivraisonParId(Long id);
    
    Page<FraisLivraisonDto> obtenirTousLesFraisLivraison(Pageable pageable);
    
    List<FraisLivraisonDto> obtenirFraisLivraisonActifs();
    
    FraisLivraisonDto mettreAJourFraisLivraison(Long id, FraisLivraisonDto fraisLivraisonDto);
    
    void supprimerFraisLivraison(Long id);
    
    FraisLivraisonDto activerFraisLivraison(Long id);
    
    FraisLivraisonDto desactiverFraisLivraison(Long id);
    
    List<FraisLivraisonDto> obtenirFraisLivraisonParType(FraisLivraison.TypeLivraison type);
    
    List<FraisLivraisonDto> obtenirFraisLivraisonApplicables(Double poids, String zone);
}
