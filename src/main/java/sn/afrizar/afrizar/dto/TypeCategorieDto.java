package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeCategorieDto {
    
    private Long id;
    private String nom;
    private String description;
    private String type;
    private String imageUrl;
    private Integer ordre;
    private boolean active;
    
    // Statistiques
    private Integer nombreGenres; // Nombre de genres qui utilisent ce type
}

