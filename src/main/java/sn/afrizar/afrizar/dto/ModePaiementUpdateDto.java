package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.afrizar.afrizar.model.ModePaiement;

import java.math.BigDecimal;

/**
 * DTO pour mettre à jour un mode de paiement existant
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModePaiementUpdateDto {
    
    private String nom;
    private String code;
    private ModePaiement.TypePaiement type;
    private String description;
    private String logo;
    private Boolean actif;
    private String configuration;
    private String instructions;
    private BigDecimal fraisPourcentage;
    private BigDecimal fraisFixe;
    private BigDecimal montantMinimum;
    private BigDecimal montantMaximum;
    private String paysSupportes;
    private Integer delaiTraitement;
    private Integer ordre;
    private String callbackUrl;
    private ModePaiement.Environnement environnement;
}

