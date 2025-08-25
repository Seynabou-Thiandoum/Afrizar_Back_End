package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.afrizar.afrizar.model.Produit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProduitDto {
    
    private Long id;
    private String nom;
    private String description;
    private List<String> photos;
    private BigDecimal prix;
    private Integer stock;
    private Integer delaiProduction;
    private BigDecimal poids;
    private List<Produit.Taille> taillesDisponibles;
    private Produit.Qualite qualite;
    private boolean personnalisable;
    private String optionsPersonnalisation;
    private Produit.StatutProduit statut;
    private Produit.Disponibilite disponibilite;
    private Long nombreVues;
    private BigDecimal noteMoyenne;
    private Integer nombreEvaluations;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    
    // Relations
    private Long vendeurId;
    private String nomVendeur;
    private String nomBoutique;
    private Long categorieId;
    private String nomCategorie;
}

