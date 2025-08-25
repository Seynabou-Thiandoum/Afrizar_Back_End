package sn.afrizar.afrizar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lignes_commande")
public class LigneCommande {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;
    
    @Column(nullable = false)
    private Integer quantite;
    
    // Prix unitaire au moment de la commande (pour historique)
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal prixUnitaire;
    
    // Taille sélectionnée
    @Enumerated(EnumType.STRING)
    private Produit.Taille taille;
    
    // Options de personnalisation sélectionnées (JSON)
    @Column(length = 1000)
    private String personnalisation;
    
    // Sous-total pour cette ligne (quantité × prix unitaire)
    @Column(precision = 10, scale = 2)
    private BigDecimal sousTotal;
    
    // Commission pour cette ligne
    @Column(precision = 10, scale = 2)
    private BigDecimal commission;
    
    // Notes spécifiques à cette ligne
    @Column(length = 500)
    private String notes;
    
    @PrePersist
    @PreUpdate
    public void calculerSousTotal() {
        if (quantite != null && prixUnitaire != null) {
            sousTotal = prixUnitaire.multiply(BigDecimal.valueOf(quantite));
        }
    }
}

