package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.afrizar.afrizar.model.Livraison;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationLivraisonDto {
    
    private Long id;
    private Livraison.TypeLivraison type;
    private String pays;
    private BigDecimal tarifBase;
    private BigDecimal tarifParKg;
    private Integer delaiJours;
    private Integer delaiMinJours;
    private Integer delaiMaxJours;
    private BigDecimal minimumFacturation;
    private BigDecimal reductionGrosColis;
    private BigDecimal supplementVilleEloignee;
    private Boolean actif;
    private String description;
    private String notes;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private String modifiePar;
}

