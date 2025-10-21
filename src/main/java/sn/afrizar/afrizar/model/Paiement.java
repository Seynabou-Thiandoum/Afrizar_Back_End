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
@Table(name = "paiements")
public class Paiement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal montant;
    
    // Ancien champ méthode (conservé pour compatibilité)
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    @Deprecated
    private MethodePaiement methode;
    
    // Nouveau: Référence vers le mode de paiement configurable
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mode_paiement_id")
    private ModePaiement modePaiement;
    
    @Enumerated(EnumType.STRING)
    private StatutPaiement statut = StatutPaiement.EN_ATTENTE;
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();
    
    @Column(name = "date_paiement")
    private LocalDateTime datePaiement;
    
    // Référence de transaction externe (PayPal, Orange Money, etc.)
    private String referenceExterne;
    
    // Numéro de transaction
    @Column(unique = true)
    private String numeroTransaction;
    
    // Devise du paiement
    private String devise = "XOF"; // Franc CFA par défaut
    
    // Frais de transaction
    @Column(precision = 8, scale = 2)
    private BigDecimal fraisTransaction = BigDecimal.ZERO;
    
    // Montant net reçu (après frais)
    @Column(precision = 10, scale = 2)
    private BigDecimal montantNet;
    
    // Informations spécifiques au mode de paiement
    @Column(length = 1000)
    private String detailsPaiement;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;
    
    // Enum pour les méthodes de paiement
    public enum MethodePaiement {
        CARTE_CREDIT("Carte de crédit"),
        CARTE_DEBIT("Carte de débit"),
        ORANGE_MONEY("Orange Money"),
        WAVE("Wave"),
        PAYPAL("PayPal"),
        VIREMENT_BANCAIRE("Virement bancaire"),
        ESPECES("Espèces"),
        POINTS_FIDELITE("Points de fidélité");
        
        private final String libelle;
        
        MethodePaiement(String libelle) {
            this.libelle = libelle;
        }
        
        public String getLibelle() {
            return libelle;
        }
    }
    
    public enum StatutPaiement {
        EN_ATTENTE,
        EN_COURS,
        REUSSI,
        ECHEC,
        ANNULE,
        REMBOURSE,
        PARTIELLEMENT_REMBOURSE
    }
    
    @PrePersist
    public void prePersist() {
        if (numeroTransaction == null) {
            numeroTransaction = "PAY-" + System.currentTimeMillis();
        }
        if (montantNet == null) {
            montantNet = montant.subtract(fraisTransaction != null ? fraisTransaction : BigDecimal.ZERO);
        }
    }
}