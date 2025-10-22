package sn.afrizar.afrizar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories_combinaisons", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"genre_id", "type_id"}))
public class CategorieCombinaison {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Genre associé
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private GenreCategorie genre;
    
    // Type associé
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private TypeCategorie type;
    
    // Ordre d'affichage pour ce genre
    private Integer ordre = 0;
    
    // Combinaison active ou non
    private boolean active = true;
}

