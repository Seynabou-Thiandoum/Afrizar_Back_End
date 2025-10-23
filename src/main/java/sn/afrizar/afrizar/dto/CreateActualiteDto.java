package sn.afrizar.afrizar.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
public class CreateActualiteDto {
    
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    private String titre;
    
    @Size(max = 500, message = "Le résumé ne peut pas dépasser 500 caractères")
    private String resume;
    
    @Size(max = 5000, message = "Le contenu ne peut pas dépasser 5000 caractères")
    private String contenu;
    
    private String imageUrl;
    
    @NotBlank(message = "L'auteur est obligatoire")
    @Size(max = 100, message = "Le nom de l'auteur ne peut pas dépasser 100 caractères")
    private String auteur;
    
    @Size(max = 50, message = "La catégorie ne peut pas dépasser 50 caractères")
    private String categorie;
    
    private List<String> tags;
    
    private Boolean estVisible = true;
    
    private Boolean estTendance = false;
}
