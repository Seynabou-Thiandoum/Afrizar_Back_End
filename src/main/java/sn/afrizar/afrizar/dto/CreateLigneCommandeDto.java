package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.afrizar.afrizar.model.Produit;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateLigneCommandeDto {
    
    @NotNull(message = "L'ID du produit est obligatoire")
    private Long produitId;
    
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantite;
    
    private Produit.Taille taille;
    
    @Size(max = 1000, message = "La personnalisation ne peut pas dépasser 1000 caractères")
    private String personnalisation;
    
    @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
    private String notes;
}

