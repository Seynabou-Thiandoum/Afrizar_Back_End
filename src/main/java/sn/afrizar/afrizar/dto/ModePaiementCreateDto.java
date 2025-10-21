package sn.afrizar.afrizar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.afrizar.afrizar.model.ModePaiement;

import java.math.BigDecimal;

/**
 * DTO pour créer un nouveau mode de paiement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModePaiementCreateDto {
    
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    
    @NotBlank(message = "Le code est obligatoire")
    private String code;
    
    @NotNull(message = "Le type est obligatoire")
    private ModePaiement.TypePaiement type;
    
    private String description;
    private String logo;
    
    @NotNull(message = "Le statut actif est obligatoire")
    private Boolean actif = true;
    
    private String configuration;
    private String instructions;
    private BigDecimal fraisPourcentage;
    private BigDecimal fraisFixe;
    private BigDecimal montantMinimum;
    private BigDecimal montantMaximum;
    private String paysSupportes;
    private Integer delaiTraitement;
    
    @NotNull(message = "L'ordre est obligatoire")
    private Integer ordre = 0;
    
    private String callbackUrl;
    
    @NotNull(message = "L'environnement est obligatoire")
    private ModePaiement.Environnement environnement = ModePaiement.Environnement.PRODUCTION;
}

