package sn.afrizar.afrizar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"photos", "taillesDisponibles", "vendeur", "categorie", "lignesCommande"})
@Table(name = "produits")
public class Produit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(length = 2000)
    private String description;
    
    // Photos multiples stockées comme JSON ou liste séparée par virgules
    @ElementCollection
    @CollectionTable(name = "produit_photos", joinColumns = @JoinColumn(name = "produit_id"))
    @Column(name = "photo_url")
    private List<String> photos;
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal prix;
    
    // Stock disponible
    private Integer stock = 0;
    
    // Délai de production en jours (pour commandes différées)
    private Integer delaiProduction = 0;
    
    // Poids en kg (important pour calcul des frais de livraison)
    @Column(precision = 8, scale = 3)
    private BigDecimal poids;
    
    // Tailles disponibles
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "produit_tailles", joinColumns = @JoinColumn(name = "produit_id"))
    private List<Taille> taillesDisponibles;
    
    // Détails spécifiques du produit
    @Column(length = 50)
    private String taille;
    
    @Column(length = 50)
    private String couleur;
    
    @Column(length = 100)
    private String matiere;
    
    // Qualité du produit
    @Enumerated(EnumType.STRING)
    private Qualite qualite = Qualite.STANDARD;
    
    // Possibilité de personnalisation
    private boolean personnalisable = false;
    
    // Options de personnalisation (JSON string)
    @Column(length = 1000)
    private String optionsPersonnalisation;
    
    // Statut du produit
    @Enumerated(EnumType.STRING)
    private StatutProduit statut = StatutProduit.ACTIF;
    
    // Indication de disponibilité
    @Enumerated(EnumType.STRING)
    private Disponibilite disponibilite = Disponibilite.EN_STOCK;
    
    // Nombre de vues
    private Long nombreVues = 0L;
    
    // Note moyenne et nombre d'évaluations
    @Column(precision = 2, scale = 1)
    private BigDecimal noteMoyenne = BigDecimal.ZERO;
    
    private Integer nombreEvaluations = 0;
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendeur_id", nullable = false)
    private Vendeur vendeur;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;
    
    // Relation avec les lignes de commande (pour accéder aux commandes via LigneCommande)
    @OneToMany(mappedBy = "produit", fetch = FetchType.LAZY)
    private List<LigneCommande> lignesCommande;
    
    // Enums
    public enum Taille {
        XS, S, M, L, XL, XXL, XXXL,
        // Tailles enfants
        AGE_2_3, AGE_4_5, AGE_6_7, AGE_8_9, AGE_10_11, AGE_12_13,
        // Tailles personnalisées
        SUR_MESURE
    }
    
    public enum Qualite {
        ECONOMIQUE,
        STANDARD,
        PREMIUM,
        LUXE
    }
    
    public enum StatutProduit {
        ACTIF,
        INACTIF,
        ARCHIVE,
        EN_ATTENTE_VALIDATION
    }
    
    public enum Disponibilite {
        EN_STOCK,
        SUR_COMMANDE,
        RUPTURE_STOCK,
        BIENTOT_DISPONIBLE
    }
    
    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }
}