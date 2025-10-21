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
    
    @Column(nullable = false, unique = true)
    private String nom;
    
    @Column(length = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeLivraison type;
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal frais;
    
    @Column(nullable = false)
    private Integer delaiMinJours;
    
    @Column(nullable = false)
    private Integer delaiMaxJours;
    
    @Column(nullable = false)
    private Boolean actif = true;
    
    @Column(nullable = false)
    private LocalDateTime dateCreation;
    
    @Column
    private LocalDateTime dateModification;
    
    // Frais par tranche de poids (optionnel)
    @Column(precision = 8, scale = 3)
    private BigDecimal poidsMin;
    
    @Column(precision = 8, scale = 3)
    private BigDecimal poidsMax;
    
    // Frais par zone géographique (optionnel)
    @Column(length = 100)
    private String zone;
    
    public enum TypeLivraison {
        EXPRESS("Livraison Express", "3 à 5 jours ouvrables"),
        STANDARD("Livraison Standard", "10 à 15 jours ouvrables");
        
        private final String nom;
        private final String description;
        
        TypeLivraison(String nom, String description) {
            this.nom = nom;
            this.description = description;
        }
        
        public String getNom() { return nom; }
        public String getDescription() { return description; }
    }
}


