package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.afrizar.afrizar.model.CommissionClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommissionClientDto {
    
    private Long id;
    private Long clientId;
    private Long commandeId;
    private BigDecimal montantCommission;
    private BigDecimal pourcentageCommission;
    private CommissionClient.StatutCommission statut;
    private LocalDateTime dateCreation;
    private LocalDateTime dateValidation;
    private LocalDateTime datePaiement;
    
    // Informations de la commande
    private String numeroCommande;
    private BigDecimal montantTotal;
    private LocalDateTime dateCommande;
}
