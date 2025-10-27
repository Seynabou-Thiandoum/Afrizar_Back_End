package sn.afrizar.afrizar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "carousel_slides")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarouselSlide {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titre;
    
    @Column(name = "sous_titre")
    private String sousTitre;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    private String badge; // "HOT", "NEW", "PROMO"
    
    @Column(name = "bouton_texte")
    private String boutonTexte;
    
    @Column(name = "bouton_lien")
    private String boutonLien;
    
    @Column(name = "ordre_affichage")
    private Integer ordreAffichage = 0;
    
    private Boolean actif = true;
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation;
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }
}
