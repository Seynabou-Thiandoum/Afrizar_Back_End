package sn.afrizar.afrizar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité représentant un mode de paiement configurable
 * Permet à l'admin de gérer dynamiquement les modes de paiement disponibles
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "modes_paiement")
public class ModePaiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom du mode de paiement (affiché au client)
    @Column(nullable = false, length = 100)
    private String nom;

    // Code unique pour identifier le mode (STRIPE, ORANGE_MONEY, WAVE, etc.)
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    // Type de paiement (CARTE, MOBILE_MONEY, CASH, VIREMENT, etc.)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypePaiement type;

    // Description du mode de paiement
    @Column(length = 500)
    private String description;

    // Logo/icône du mode de paiement (URL ou chemin)
    @Column(length = 255)
    private String logo;

    // Actif ou non
    @Column(nullable = false)
    private Boolean actif = true;

    // Configuration JSON (API keys, merchant ID, etc.)
    // Format: {"apiKey": "...", "merchantId": "...", "webhookUrl": "..."}
    @Column(columnDefinition = "TEXT")
    private String configuration;

    // Instructions pour l'utilisateur
    @Column(length = 1000)
    private String instructions;

    // Frais de transaction (pourcentage)
    @Column(precision = 5, scale = 2)
    private java.math.BigDecimal fraisPourcentage = java.math.BigDecimal.ZERO;

    // Frais de transaction (montant fixe)
    @Column(precision = 8, scale = 2)
    private java.math.BigDecimal fraisFixe = java.math.BigDecimal.ZERO;

    // Montant minimum requis
    @Column(precision = 10, scale = 2)
    private java.math.BigDecimal montantMinimum;

    // Montant maximum autorisé
    @Column(precision = 10, scale = 2)
    private java.math.BigDecimal montantMaximum;

    // Pays supportés (format JSON array: ["SN", "CI", "ML", ...])
    @Column(length = 500)
    private String paysSupportes;

    // Délai de traitement estimé (en heures)
    private Integer delaiTraitement;

    // Ordre d'affichage
    @Column(nullable = false)
    private Integer ordre = 0;

    // URL de callback pour les webhooks
    @Column(length = 255)
    private String callbackUrl;

    // Environnement (TEST ou PRODUCTION)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Environnement environnement = Environnement.PRODUCTION;

    // Métadonnées supplémentaires
    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "modifie_par")
    private String modifiePar;

    // Enum pour les types de paiement
    public enum TypePaiement {
        CARTE_BANCAIRE,      // Carte de crédit/débit
        MOBILE_MONEY,        // Paiement mobile (Orange Money, Wave, etc.)
        VIREMENT_BANCAIRE,   // Virement bancaire
        CASH,                // Paiement à la livraison
        PORTEFEUILLE,        // Portefeuille électronique (PayPal, etc.)
        CRYPTO,              // Cryptomonnaie
        POINTS_FIDELITE      // Points de fidélité
    }

    // Enum pour l'environnement
    public enum Environnement {
        TEST,
        PRODUCTION
    }

    @PreUpdate
    public void preUpdate() {
        dateModification = LocalDateTime.now();
    }
}

