package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.afrizar.afrizar.model.Livraison;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LivraisonDto {
    
    private Long id;
    private Long commandeId;
    private Livraison.TypeLivraison type;
    private String adresseLivraison;
    private String ville;
    private String codePostal;
    private String pays;
    private BigDecimal cout;
    private BigDecimal poidsTotal;
    private Livraison.StatutLivraison statut;
    private LocalDate dateExpedition;
    private LocalDate dateLivraisonPrevue;
    private LocalDate dateLivraisonEffective;
    private String numeroSuivi;
    private String transporteur;
    private LocalDateTime dateCreation;
    private String notes;
}

