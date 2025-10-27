package sn.afrizar.afrizar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventes_flash")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenteFlash {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nom;
    private String description;
    
    @Column(name = "date_debut")
    private LocalDateTime dateDebut;
    
    @Column(name = "date_fin")
    private LocalDateTime dateFin;
    
    private Boolean actif = true;
    
    @Column(name = "pourcentage_reduction_par_defaut")
    private Integer pourcentageReductionParDefaut;
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation;
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    @OneToMany(mappedBy = "venteFlash", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProduitVenteFlash> produitsVenteFlash = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }
    
    public boolean estEnCours() {
        LocalDateTime maintenant = LocalDateTime.now();
        return actif && maintenant.isAfter(dateDebut) && maintenant.isBefore(dateFin);
    }
    
    public long getTempsRestantMillisecondes() {
        if (!estEnCours()) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), dateFin).toMillis();
    }
}
