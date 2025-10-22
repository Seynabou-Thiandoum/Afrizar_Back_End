package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorieCombinaisonDto {
    
    private Long id;
    private Long genreId;
    private String nomGenre;
    private Long typeId;
    private String nomType;
    private Integer ordre;
    private boolean active;
    
    // Pour l'affichage
    private String affichage; // "Homme - Boubous"
}

