package sn.afrizar.afrizar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Client extends Utilisateur {
    
    @Column(nullable = false)
    private String adresse;
    
    private String ville;
    
    private String codePostal;
    
    @Column(nullable = false)
    private String pays;
    
    // Points de fidélité pour la gamification
    private Integer pointsFidelite = 0;
    
    // Préférences de livraison
    @Enumerated(EnumType.STRING)
    private TypeLivraison typeLivraisonPrefere = TypeLivraison.STANDARD;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Commande> commandes;
    
    public enum TypeLivraison {
        EXPRESS,
        STANDARD,
        ECONOMIQUE
    }
}