package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorieDto {
    
    private Long id;
    private String nom;
    private String description;
    private String icone;
    private Integer ordre;
    private String type;
    private String genre;
    private String imageUrl;
    private boolean active;
    
    // Relation parent/enfant
    private Long parentId;
    private String nomParent;
    private List<CategorieDto> sousCategories;
    
    // Statistiques
    private Integer nombreProduits;
}

