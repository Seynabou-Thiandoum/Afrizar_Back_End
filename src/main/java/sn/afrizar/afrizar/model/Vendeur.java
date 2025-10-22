package sn.afrizar.afrizar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "produits")
public class Vendeur extends Utilisateur {
    
    @Column(nullable = false)
    private String nomBoutique;
    
    @Column(length = 500)
    private String description;
    
    private String adresseBoutique;
    
    // Rating de 0 à 5
    @Column(precision = 2, scale = 1)
    private BigDecimal rating = BigDecimal.ZERO;
    
    private Integer nombreEvaluations = 0;
    
    // Commission personnalisée pour ce vendeur (optionnel)
    @Column(precision = 5, scale = 2)
    private BigDecimal tauxCommissionPersonnalise;
    
    // Statut de vérification du vendeur
    private boolean verifie = false;
    
    // Statut de publication (visible sur la page publique)
    private boolean publie = false;
    
    // Photo de profil du vendeur
    private String photoUrl;
    
    // Spécialités du vendeur
    private String specialites;
    
    @OneToMany(mappedBy = "vendeur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Produit> produits;
}