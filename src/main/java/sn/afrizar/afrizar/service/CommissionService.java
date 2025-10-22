package sn.afrizar.afrizar.service;

import sn.afrizar.afrizar.model.Commission;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CommissionService {
    
    Commission creerCommission(Commission commission);
    
    Optional<Commission> obtenirCommissionParId(Long id);
    
    List<Commission> obtenirToutesLesCommissionsActives();
    
    Commission mettreAJourCommission(Long id, Commission commission);
    
    void supprimerCommission(Long id);
    
    Commission activerCommission(Long id);
    
    Commission desactiverCommission(Long id);
    
    /**
     * Calcule la commission applicable pour un montant donné
     */
    BigDecimal calculerCommission(BigDecimal montant);
    
    /**
     * Trouve la tranche de commission applicable pour un montant
     */
    Optional<Commission> trouverCommissionApplicable(BigDecimal montant);
    
    /**
     * Calcule la commission avec un taux personnalisé pour un vendeur
     */
    BigDecimal calculerCommissionPersonnalisee(BigDecimal montant, BigDecimal tauxPersonnalise);
    
    /**
     * Initialise les tranches de commission par défaut selon le cahier des charges
     */
    void initialiserCommissionsParDefaut();
    
    /**
     * Simule le calcul de commission pour un montant donné
     */
    java.util.Map<String, Object> simulerCommission(BigDecimal montant);
    
    /**
     * Obtient les statistiques d'utilisation des commissions
     */
    java.util.Map<String, Object> obtenirStatistiques();
}

