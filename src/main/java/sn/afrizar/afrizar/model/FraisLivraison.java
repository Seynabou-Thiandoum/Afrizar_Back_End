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
@Table(name = "frais_livraison")
public class FraisLivraison {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeLivraison type;
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal frais;
    
    @Column(name = "delai_min_jours")
    private Integer delaiMinJours;
    
    @Column(name = "delai_max_jours")
    private Integer delaiMaxJours;
    
    @Column(nullable = false)
    private Boolean actif = true;
    
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    @Column(name = "poids_min")
    private BigDecimal poidsMin;
    
    @Column(name = "poids_max")
    private BigDecimal poidsMax;
    
    private String zone;
    
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }
    
    public enum TypeLivraison {
        STANDARD("Standard"),
        EXPRESS("Express"),
        URGENT("Urgent");
        
        private final String displayName;
        
        TypeLivraison(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
