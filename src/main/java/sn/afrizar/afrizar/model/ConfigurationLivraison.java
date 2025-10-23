package sn.afrizar.afrizar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "configurations_livraison")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationLivraison {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Livraison.TypeLivraison type;
    
    @Column(nullable = false)
    private String pays;
    
    @Column(name = "tarif_base", nullable = false)
    private BigDecimal tarifBase;
    
    @Column(name = "tarif_par_kg", nullable = false)
    private BigDecimal tarifParKg;
    
    @Column(name = "delai_jours", nullable = false)
    private Integer delaiJours;
    
    @Column(name = "delai_min_jours")
    private Integer delaiMinJours;
    
    @Column(name = "delai_max_jours")
    private Integer delaiMaxJours;
    
    @Column(name = "minimum_facturation")
    private BigDecimal minimumFacturation;
    
    @Column(name = "reduction_gros_colis")
    private BigDecimal reductionGrosColis; // Pourcentage de réduction pour colis > 5kg
    
    @Column(name = "supplement_ville_eloignee")
    private BigDecimal supplementVilleEloignee; // Pourcentage de supplément pour villes éloignées
    
    @Column(name = "actif", nullable = false)
    @Builder.Default
    private Boolean actif = true;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "notes")
    private String notes;
    
    @CreationTimestamp
    @Column(name = "date_creation")
    private LocalDateTime dateCreation;
    
    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    @Column(name = "modifie_par")
    private String modifiePar;
}
