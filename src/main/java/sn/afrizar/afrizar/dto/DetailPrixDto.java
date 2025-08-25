package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO pour afficher le détail transparent des calculs de prix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailPrixDto {
    
    // Prix de base du vendeur
    private BigDecimal prixVendeur;
    
    // Pourcentage de commission appliqué
    private BigDecimal pourcentageCommission;
    
    // Montant de la commission en FCFA
    private BigDecimal montantCommission;
    
    // Prix final pour le client (prix vendeur + commission)
    private BigDecimal prixFinal;
    
    // Description de la tranche de commission
    private String descriptionTranche;
    
    // Indique si c'est une commission personnalisée pour le vendeur
    private boolean commissionPersonnalisee;
    
    // Nom du vendeur (pour affichage)
    private String nomVendeur;
    
    // Exemple d'affichage transparent
    public String getAffichageTransparent() {
        if (commissionPersonnalisee) {
            return String.format(
                "Prix vendeur : %,.0f FCFA + %,.1f%% commission spéciale = %,.0f FCFA", 
                prixVendeur, 
                pourcentageCommission, 
                prixFinal
            );
        } else {
            return String.format(
                "Prix vendeur : %,.0f FCFA + %,.1f%% commission (%s) = %,.0f FCFA", 
                prixVendeur, 
                pourcentageCommission, 
                descriptionTranche,
                prixFinal
            );
        }
    }
    
    // Calcul de l'économie par rapport à la tranche la plus élevée (pour motiver l'achat)
    public String getMessageEconomie() {
        if (pourcentageCommission.compareTo(BigDecimal.valueOf(10)) < 0) {
            BigDecimal economie = prixVendeur.multiply(BigDecimal.valueOf(10 - pourcentageCommission.doubleValue()))
                    .divide(BigDecimal.valueOf(100));
            return String.format("Vous économisez %,.0f FCFA par rapport aux produits < 10 000 FCFA !", economie);
        }
        return "";
    }
}

