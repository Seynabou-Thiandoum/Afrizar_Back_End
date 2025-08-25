package sn.afrizar.afrizar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.afrizar.afrizar.dto.DetailPrixDto;
import sn.afrizar.afrizar.model.Commission;
import sn.afrizar.afrizar.model.Vendeur;
import sn.afrizar.afrizar.repository.VendeurRepository;
import sn.afrizar.afrizar.service.CalculPrixService;
import sn.afrizar.afrizar.service.CommissionService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalculPrixServiceImpl implements CalculPrixService {
    
    private final CommissionService commissionService;
    private final VendeurRepository vendeurRepository;
    
    @Override
    public DetailPrixDto calculerPrixFinal(BigDecimal prixVendeur, Long vendeurId) {
        log.debug("Calcul du prix final pour vendeur {} avec prix de base {}", vendeurId, prixVendeur);
        
        DetailPrixDto detail = new DetailPrixDto();
        detail.setPrixVendeur(prixVendeur);
        
        // Récupérer les infos du vendeur
        Optional<Vendeur> vendeurOpt = vendeurRepository.findById(vendeurId);
        if (vendeurOpt.isPresent()) {
            Vendeur vendeur = vendeurOpt.get();
            detail.setNomVendeur(vendeur.getNomBoutique());
            
            // Vérifier si le vendeur a une commission personnalisée
            if (vendeur.getTauxCommissionPersonnalise() != null && 
                vendeur.getTauxCommissionPersonnalise().compareTo(BigDecimal.ZERO) > 0) {
                
                // Commission personnalisée
                detail.setPourcentageCommission(vendeur.getTauxCommissionPersonnalise());
                detail.setMontantCommission(
                    prixVendeur.multiply(vendeur.getTauxCommissionPersonnalise())
                              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                );
                detail.setCommissionPersonnalisee(true);
                detail.setDescriptionTranche("Commission négociée");
                
                log.debug("Commission personnalisée appliquée: {}%", detail.getPourcentageCommission());
            } else {
                // Commission standard
                calculerCommissionStandard(prixVendeur, detail);
            }
        } else {
            // Vendeur non trouvé, utiliser commission standard
            calculerCommissionStandard(prixVendeur, detail);
        }
        
        // Calculer le prix final
        detail.setPrixFinal(detail.getPrixVendeur().add(detail.getMontantCommission()));
        
        log.debug("Prix final calculé: {} FCFA (prix: {} + commission: {})", 
                 detail.getPrixFinal(), detail.getPrixVendeur(), detail.getMontantCommission());
        
        return detail;
    }
    
    @Override
    public DetailPrixDto calculerPrixFinal(BigDecimal prixVendeur) {
        log.debug("Calcul du prix final sans vendeur spécifique pour prix de base {}", prixVendeur);
        
        DetailPrixDto detail = new DetailPrixDto();
        detail.setPrixVendeur(prixVendeur);
        
        calculerCommissionStandard(prixVendeur, detail);
        
        // Calculer le prix final
        detail.setPrixFinal(detail.getPrixVendeur().add(detail.getMontantCommission()));
        
        return detail;
    }
    
    private void calculerCommissionStandard(BigDecimal prixVendeur, DetailPrixDto detail) {
        Optional<Commission> commissionOpt = commissionService.trouverCommissionApplicable(prixVendeur);
        
        if (commissionOpt.isPresent()) {
            Commission commission = commissionOpt.get();
            detail.setPourcentageCommission(commission.getPourcentage());
            detail.setMontantCommission(commission.calculerCommission(prixVendeur));
            detail.setDescriptionTranche(commission.getDescription());
            detail.setCommissionPersonnalisee(false);
            
            log.debug("Commission standard appliquée: {}% ({})", 
                     commission.getPourcentage(), commission.getDescription());
        } else {
            // Aucune commission trouvée, utiliser 0%
            detail.setPourcentageCommission(BigDecimal.ZERO);
            detail.setMontantCommission(BigDecimal.ZERO);
            detail.setDescriptionTranche("Aucune commission");
            detail.setCommissionPersonnalisee(false);
            
            log.warn("Aucune tranche de commission trouvée pour le prix: {}", prixVendeur);
        }
    }
    
    @Override
    public String genererExemplesCalculs() {
        StringBuilder exemples = new StringBuilder();
        exemples.append("EXEMPLES DE CALCULS DE COMMISSION :\n\n");
        
        // Exemples selon vos spécifications
        BigDecimal[] prixExemples = {
            BigDecimal.valueOf(9000),   // < 10 000
            BigDecimal.valueOf(25000),  // 10 000 - 30 000
            BigDecimal.valueOf(45000),  // 30 001 - 50 000
            BigDecimal.valueOf(75000)   // > 50 000
        };
        
        for (BigDecimal prix : prixExemples) {
            DetailPrixDto detail = calculerPrixFinal(prix);
            exemples.append(String.format(
                "• %,.0f FCFA → %,.0f FCFA (%s)\n",
                prix,
                detail.getPrixFinal(),
                detail.getDescriptionTranche()
            ));
        }
        
        exemples.append("\nNote: Les vendeurs partenaires peuvent bénéficier de commissions négociées.");
        
        return exemples.toString();
    }
    
    @Override
    public boolean aCommissionPersonnalisee(Long vendeurId) {
        return vendeurRepository.findById(vendeurId)
                .map(vendeur -> vendeur.getTauxCommissionPersonnalise() != null && 
                               vendeur.getTauxCommissionPersonnalise().compareTo(BigDecimal.ZERO) > 0)
                .orElse(false);
    }
}

