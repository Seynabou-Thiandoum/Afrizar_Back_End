package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.afrizar.afrizar.model.Paiement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaiementDto {
    
    private Long id;
    private BigDecimal montant;
    private Paiement.MethodePaiement methode;
    private Paiement.StatutPaiement statut;
    private LocalDateTime dateCreation;
    private LocalDateTime datePaiement;
    private String referenceExterne;
    private String numeroTransaction;
    private String devise;
    private BigDecimal fraisTransaction;
    private BigDecimal montantNet;
    private String detailsPaiement;
    private Long commandeId;
}

