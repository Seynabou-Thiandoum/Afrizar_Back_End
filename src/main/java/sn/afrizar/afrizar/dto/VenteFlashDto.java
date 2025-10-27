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
public class VenteFlashDto {
    private Long id;
    private String nom;
    private String description;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Boolean actif;
    private Integer pourcentageReductionParDefaut;
    private long tempsRestantMillisecondes; // Timer en millisecondes
    private boolean estEnCours;
    private List<ProduitVenteFlashDto> produits;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProduitVenteFlashDto {
        private Long produitId;
        private String nomProduit;
        private String imageUrl;
        private BigDecimal prixOriginal;
        private BigDecimal prixPromotionnel;
        private Integer pourcentageReduction;
        private Integer quantiteStock;
        private Integer quantiteVendue;
    }
}
