package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.afrizar.afrizar.model.Livraison;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateLivraisonDto {
    
    @NotNull(message = "Le type de livraison est obligatoire")
    private Livraison.TypeLivraison type;
    
    @NotBlank(message = "L'adresse de livraison est obligatoire")
    private String adresseLivraison;
    
    private String ville;
    
    private String codePostal;
    
    @NotBlank(message = "Le pays est obligatoire")
    private String pays;
    
    @Size(max = 1000, message = "Les notes ne peuvent pas dépasser 1000 caractères")
    private String notes;
}

