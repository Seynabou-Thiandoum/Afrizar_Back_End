package sn.afrizar.afrizar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "commandes")
public class Commande {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Numéro de commande unique
    @Column(unique = true, nullable = false)
    private String numeroCommande;
    
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCommande statut = StatutCommande.EN_ATTENTE;
    
    // Type de commande (immédiate ou différée)
    @Enumerated(EnumType.STRING)
    private TypeCommande type = TypeCommande.IMMEDIATE;
    
    // Date de livraison souhaitée (pour commandes différées)
    private LocalDate dateLivraisonSouhaitee;
    
    // Date de livraison estimée
    private LocalDate dateLivraisonEstimee;
    
    // Montant total avant commissions et frais
    @Column(precision = 10, scale = 2)
    private BigDecimal montantHT = BigDecimal.ZERO;
    
    // Commission totale
    @Column(precision = 10, scale = 2)
    private BigDecimal montantCommission = BigDecimal.ZERO;
    
    // Frais de livraison
    @Column(precision = 10, scale = 2)
    private BigDecimal fraisLivraison = BigDecimal.ZERO;
    
    // Montant total TTC
    @Column(precision = 10, scale = 2)
    private BigDecimal montantTotal = BigDecimal.ZERO;
    
    // Points de fidélité utilisés
    private Integer pointsFideliteUtilises = 0;
    
    // Reduction appliquée
    @Column(precision = 10, scale = 2)
    private BigDecimal reduction = BigDecimal.ZERO;
    
    // Notes spéciales du client
    @Column(length = 1000)
    private String notes;
    
    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    @OneToOne(mappedBy = "commande", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Paiement paiement;
    
    @OneToOne(mappedBy = "commande", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Livraison livraison;
    
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LigneCommande> lignesCommande;
    
    // Enum pour les statuts de commande
    public enum StatutCommande {
        EN_ATTENTE,          // En attente de validation
        CONFIRMEE,           // Confirmée par le client
        EN_PREPARATION,      // En cours de préparation
        PRETE,              // Prête à expédier
        EXPEDIEE,           // Expédiée
        EN_LIVRAISON,       // En cours de livraison
        LIVREE,             // Livrée
        ANNULEE,            // Annulée
        RETOURNEE,          // Retournée
        REMBOURSEE          // Remboursée
    }
    
    public enum TypeCommande {
        IMMEDIATE,          // Commande immédiate (produits en stock)
        DIFFEREE,          // Commande différée (produits sur commande)
        MIXTE              // Mélange des deux
    }
    
    @PrePersist
    public void prePersist() {
        if (numeroCommande == null) {
            // Génération automatique du numéro de commande
            numeroCommande = "CMD-" + System.currentTimeMillis();
        }
    }
}