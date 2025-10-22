package sn.afrizar.afrizar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "commissions_clients")
public class CommissionClient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "client_id", nullable = false)
    private Long clientId;
    
    @Column(name = "commande_id", nullable = false)
    private Long commandeId;
    
    @Column(name = "montant_commission", precision = 10, scale = 2, nullable = false)
    private BigDecimal montantCommission;
    
    @Column(name = "pourcentage_commission", precision = 5, scale = 2, nullable = false)
    private BigDecimal pourcentageCommission;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCommission statut = StatutCommission.EN_ATTENTE;
    
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;
    
    @Column(name = "date_validation")
    private LocalDateTime dateValidation;
    
    @Column(name = "date_paiement")
    private LocalDateTime datePaiement;
    
    // Relation avec la commande
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", insertable = false, updatable = false)
    private Commande commande;
    
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
    
    public enum StatutCommission {
        EN_ATTENTE("En attente"),
        VALIDEE("Validée"),
        PAYEE("Payée");
        
        private final String displayName;
        
        StatutCommission(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
