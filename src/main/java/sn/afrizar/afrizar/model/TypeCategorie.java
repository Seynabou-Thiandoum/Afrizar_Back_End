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
@Table(name = "types_categories")
public class TypeCategorie {
    
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
    
    // URL de l'image du type
    private String imageUrl;
    
    // Ordre d'affichage
    private Integer ordre = 0;
    
    // Type actif ou non
    private boolean active = true;
    
    // Relations avec les combinaisons
    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CategorieCombinaison> combinaisons;
}