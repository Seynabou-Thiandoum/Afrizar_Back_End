package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.afrizar.afrizar.model.FraisLivraison;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraisLivraisonDto {
    
    private Long id;
    private String nom;
    private String description;
    private String type;
    private String typeNom;
    private String typeDescription;
    private BigDecimal frais;
    private Integer delaiMinJours;
    private Integer delaiMaxJours;
    private Boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private BigDecimal poidsMin;
    private BigDecimal poidsMax;
    private String zone;
}


