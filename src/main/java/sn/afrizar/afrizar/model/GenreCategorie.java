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
@Table(name = "genres_categories")
public class GenreCategorie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nom;
    
    @Column(length = 500)
    private String description;
    
    // Type de catégorie (VETEMENTS, ACCESSOIRES)
    @Enumerated(EnumType.STRING)
    private TypeCategorieEnum type = TypeCategorieEnum.VETEMENTS;
    
    // URL de l'image du genre
    private String imageUrl;
    
    // Ordre d'affichage
    private Integer ordre = 0;
    
    // Genre actif ou non
    private boolean active = true;
    
    // Relations avec les combinaisons
    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CategorieCombinaison> combinaisons;
}