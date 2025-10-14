package sn.afrizar.afrizar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories")
public class Categorie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nom;
    
    @Column(length = 500)
    private String description;
    
    // Icône ou image pour la catégorie
    private String icone;
    
    // Ordre d'affichage
    private Integer ordre = 0;
    
    // Type de catégorie (VETEMENTS, ACCESSOIRES)
    @Enumerated(EnumType.STRING)
    private TypeCategorie type = TypeCategorie.VETEMENTS;
    
    // Genre ciblé (HOMME, FEMME, ENFANT)
    @Enumerated(EnumType.STRING)
    private GenreCategorie genre = GenreCategorie.HOMME;
    
    // URL de l'image de la catégorie
    private String imageUrl;
    
    // Catégorie active ou non
    private boolean active = true;
    
    // Relation avec les produits
    @OneToMany(mappedBy = "categorie", fetch = FetchType.LAZY)
    private List<Produit> produits;
    
    // Sous-catégories (auto-référence)
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Categorie parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Categorie> sousCategories;
}

