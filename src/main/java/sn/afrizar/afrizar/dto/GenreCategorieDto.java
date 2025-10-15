package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreCategorieDto {
    
    private Long id;
    private String nom;
    private String description;
    private String type;
    private String imageUrl;
    private Integer ordre;
    private boolean active;
    
    // Types associés à ce genre
    private List<TypeCategorieDto> typesAssocies;
    
    // Statistiques
    private Integer nombreTypes; // Nombre de types associés à ce genre
}
