package sn.afrizar.afrizar.service;

import sn.afrizar.afrizar.dto.DetailPrixDto;

import java.math.BigDecimal;

/**
 * Service pour le calcul transparent des prix avec commissions
 */
public interface CalculPrixService {
    
    /**
     * Calcule le prix final avec commission et détail transparent
     * @param prixVendeur Prix de base du vendeur
     * @param vendeurId ID du vendeur (pour commission personnalisée éventuelle)
     * @return Détail complet du calcul de prix
     */
    DetailPrixDto calculerPrixFinal(BigDecimal prixVendeur, Long vendeurId);
    
    /**
     * Calcule le prix final avec commission standard (sans vendeur spécifique)
     * @param prixVendeur Prix de base
     * @return Détail complet du calcul de prix
     */
    DetailPrixDto calculerPrixFinal(BigDecimal prixVendeur);
    
    /**
     * Simule le calcul de prix pour plusieurs tranches (pour affichage)
     * @return Exemples de calculs par tranche
     */
    String genererExemplesCalculs();
    
    /**
     * Vérifie si un vendeur a une commission personnalisée
     * @param vendeurId ID du vendeur
     * @return true si commission personnalisée, false sinon
     */
    boolean aCommissionPersonnalisee(Long vendeurId);
}

