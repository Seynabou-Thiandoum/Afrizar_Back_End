package sn.afrizar.afrizar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "produit_vente_flash", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"vente_flash_id", "produit_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProduitVenteFlash {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vente_flash_id")
    private VenteFlash venteFlash;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id")
    private Produit produit;
    
    @Column(name = "prix_promotionnel")
    private BigDecimal prixPromotionnel;
    
    @Column(name = "pourcentage_reduction")
    private Integer pourcentageReduction;
    
    @Column(name = "quantite_stock")
    private Integer quantiteStock;
    
    @Column(name = "image_url", length = 1000)
    private String imageUrl; // Image personnalisée pour la vente flash (optionnel)
}
