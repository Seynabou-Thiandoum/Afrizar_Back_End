package sn.afrizar.afrizar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"panier", "produit"})
@Table(name = "panier_items")
public class PanierItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "panier_id", nullable = false)
    private Panier panier;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;
    
    @Column(nullable = false)
    private Integer quantite;
    
    @Column(name = "prix_unitaire", precision = 10, scale = 2, nullable = false)
    private BigDecimal prixUnitaire;
    
    // Options sélectionnées (taille, couleur, etc.)
    @Column(length = 50)
    private String taille;
    
    @Column(length = 50)
    private String couleur;
    
    @Column(length = 1000)
    private String optionsPersonnalisation;
    
    @Column(name = "date_ajout")
    private LocalDateTime dateAjout = LocalDateTime.now();
    
    // Méthodes utilitaires
    
    public BigDecimal getSousTotal() {
        if (prixUnitaire == null || quantite == null) {
            return BigDecimal.ZERO;
        }
        return prixUnitaire.multiply(BigDecimal.valueOf(quantite));
    }
    
    public void incrementerQuantite(int increment) {
        this.quantite += increment;
    }
    
    public void decrementerQuantite(int decrement) {
        this.quantite -= decrement;
        if (this.quantite < 0) {
            this.quantite = 0;
        }
    }
}




