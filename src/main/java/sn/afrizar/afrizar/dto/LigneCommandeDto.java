package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.afrizar.afrizar.model.Produit;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LigneCommandeDto {
    
    private Long id;
    private Long commandeId;
    private Long produitId;
    private String nomProduit;
    private String photoProduit;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private Produit.Taille taille;
    private String personnalisation;
    private BigDecimal sousTotal;
    private BigDecimal commission;
    private String notes;
}

