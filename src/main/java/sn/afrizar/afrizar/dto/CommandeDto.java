package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.afrizar.afrizar.model.Commande;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandeDto {
    
    private Long id;
    private String numeroCommande;
    private LocalDateTime dateCreation;
    private Commande.StatutCommande statut;
    private Commande.TypeCommande type;
    private LocalDate dateLivraisonSouhaitee;
    private LocalDate dateLivraisonEstimee;
    private BigDecimal montantHT;
    private BigDecimal montantCommission;
    private BigDecimal fraisLivraison;
    private BigDecimal montantTotal;
    private Integer pointsFideliteUtilises;
    private BigDecimal reduction;
    private String notes;
    
    // Relations
    private Long clientId;
    private String nomClient;
    private String emailClient;
    
    private PaiementDto paiement;
    private LivraisonDto livraison;
    private List<LigneCommandeDto> lignesCommande;
}

