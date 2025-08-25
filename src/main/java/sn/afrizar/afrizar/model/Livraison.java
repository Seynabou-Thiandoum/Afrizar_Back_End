package sn.afrizar.afrizar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "livraisons")
public class Livraison {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeLivraison type;
    
    @Column(nullable = false)
    private String adresseLivraison;
    
    private String ville;
    
    private String codePostal;
    
    @Column(nullable = false)
    private String pays;
    
    // Coût de la livraison calculé dynamiquement
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal cout;
    
    // Poids total des produits
    @Column(precision = 8, scale = 3)
    private BigDecimal poidsTotal;
    
    @Enumerated(EnumType.STRING)
    private StatutLivraison statut = StatutLivraison.EN_PREPARATION;
    
    private LocalDate dateExpedition;
    
    private LocalDate dateLivraisonPrevue;
    
    private LocalDate dateLivraisonEffective;
    
    // Numéro de suivi
    private String numeroSuivi;
    
    // Transporteur
    private String transporteur;
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();
    
    // Notes spéciales pour la livraison
    @Column(length = 1000)
    private String notes;
    
    public enum TypeLivraison {
        EXPRESS,     // Livraison rapide (1-3 jours)
        STANDARD,    // Livraison standard (5-7 jours)
        ECONOMIQUE   // Livraison économique (10-15 jours)
    }
    
    public enum StatutLivraison {
        EN_PREPARATION,
        EXPEDIE,
        EN_TRANSIT,
        EN_LIVRAISON,
        LIVRE,
        ECHEC_LIVRAISON,
        RETOURNE
    }
}

