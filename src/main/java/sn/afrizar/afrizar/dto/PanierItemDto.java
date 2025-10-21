package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanierItemDto {
    
    private Long id;
    private Long produitId;
    private String produitNom;
    private String produitDescription;
    private List<String> produitPhotos;
    private BigDecimal prixUnitaire;
    private Integer quantite;
    private String taille;
    private String couleur;
    private String optionsPersonnalisation;
    private BigDecimal sousTotal;
    private LocalDateTime dateAjout;
    private Integer stockDisponible;
    private String vendeurNom;
    private String nomBoutique;
}



