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
    
    // Informations de livraison (optionnelles pour création depuis panier)
    private String adresseLivraison;
    private String ville;
    private String pays;
    private String codePostal;
    
    // Lignes de commande (optionnel si création depuis panier)
    @Valid
    private List<CreateLigneCommandeDto> lignesCommande;
    
    @Valid
    private CreateLivraisonDto livraison;
}

