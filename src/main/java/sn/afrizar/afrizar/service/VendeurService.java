package sn.afrizar.afrizar.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.afrizar.afrizar.dto.VendeurDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface VendeurService {
    
    VendeurDto creerVendeur(VendeurDto vendeurDto);
    
    Optional<VendeurDto> obtenirVendeurParId(Long id);
    
    Optional<VendeurDto> obtenirVendeurParEmail(String email);
    
    List<VendeurDto> obtenirTousLesVendeurs();
    
    Page<VendeurDto> obtenirVendeursAvecPagination(Pageable pageable);
    
    VendeurDto mettreAJourVendeur(Long id, VendeurDto vendeurDto);
    
    void supprimerVendeur(Long id);
    
    void desactiverVendeur(Long id);
    
    void activerVendeurVoid(Long id);
    
    List<VendeurDto> obtenirVendeursVerifies();
    
    List<VendeurDto> obtenirVendeursActifs();
    
    List<VendeurDto> obtenirVendeursParRatingMinimum(BigDecimal rating);
    
    List<VendeurDto> rechercherVendeurs(String terme);
    
    VendeurDto verifierVendeur(Long vendeurId);
    
    VendeurDto annulerVerificationVendeur(Long vendeurId);
    
    VendeurDto definirCommissionPersonnalisee(Long vendeurId, BigDecimal tauxCommission);
    
    VendeurDto ajouterEvaluation(Long vendeurId, BigDecimal note);
    
    Long getNombreProduitsVendeur(Long vendeurId);
    
    Long getNombreVendeursVerifies();
    
    boolean verifierEmailDisponible(String email);
    
    // Méthodes d'administration
    Page<VendeurDto> obtenirTousLesVendeurs(Pageable pageable, boolean includeNonVerifies);
    
    List<VendeurDto> obtenirVendeursNonVerifies();
    
    long compterVendeurs();
    
    long compterVendeursVerifies();
    
    long compterVendeursNonVerifies();
    
    VendeurDto activerVendeur(Long id);
    
    // Méthodes de publication
    VendeurDto publierVendeur(Long vendeurId);
    
    VendeurDto depublierVendeur(Long vendeurId);
    
    List<VendeurDto> obtenirVendeursPublies();
}

