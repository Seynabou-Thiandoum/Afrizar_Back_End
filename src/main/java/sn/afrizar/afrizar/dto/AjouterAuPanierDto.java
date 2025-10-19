package sn.afrizar.afrizar.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AjouterAuPanierDto {
    
    @NotNull(message = "L'ID du produit est obligatoire")
    private Long produitId;
    
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantite;
    
    @Size(max = 50, message = "La taille ne peut pas dépasser 50 caractères")
    private String taille;
    
    @Size(max = 50, message = "La couleur ne peut pas dépasser 50 caractères")
    private String couleur;
    
    @Size(max = 1000, message = "Les options de personnalisation ne peuvent pas dépasser 1000 caractères")
    private String optionsPersonnalisation;
}

