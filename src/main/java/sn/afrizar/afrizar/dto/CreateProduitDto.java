package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.afrizar.afrizar.model.Produit;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProduitDto {
    
    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;
    
    @Size(max = 2000, message = "La description ne peut pas dépasser 2000 caractères")
    private String description;
    
    private List<String> photos;
    
    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être positif")
    private BigDecimal prix;
    
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    private Integer stock = 0;
    
    @Min(value = 0, message = "Le délai de production ne peut pas être négatif")
    private Integer delaiProduction = 0;
    
    @DecimalMin(value = "0.0", message = "Le poids ne peut pas être négatif")
    private BigDecimal poids;
    
    private List<Produit.Taille> taillesDisponibles;
    
    private Produit.Qualite qualite = Produit.Qualite.STANDARD;
    
    private boolean personnalisable = false;
    
    @Size(max = 1000, message = "Les options de personnalisation ne peuvent pas dépasser 1000 caractères")
    private String optionsPersonnalisation;
    
    @NotNull(message = "L'ID du vendeur est obligatoire")
    private Long vendeurId;
    
    private Long categorieId;
}

