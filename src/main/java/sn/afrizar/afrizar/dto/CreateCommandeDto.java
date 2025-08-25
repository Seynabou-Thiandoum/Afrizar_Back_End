package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.afrizar.afrizar.model.Commande;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommandeDto {
    
    @NotNull(message = "L'ID du client est obligatoire")
    private Long clientId;
    
    private Commande.TypeCommande type = Commande.TypeCommande.IMMEDIATE;
    
    @Future(message = "La date de livraison souhaitée doit être dans le futur")
    private LocalDate dateLivraisonSouhaitee;
    
    private Integer pointsFideliteUtilises = 0;
    
    @Size(max = 1000, message = "Les notes ne peuvent pas dépasser 1000 caractères")
    private String notes;
    
    @NotEmpty(message = "Une commande doit contenir au moins un produit")
    @Valid
    private List<CreateLigneCommandeDto> lignesCommande;
    
    @Valid
    private CreateLivraisonDto livraison;
}

