package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour gérer la configuration sensible d'un mode de paiement
 * (API keys, secrets, etc.)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModePaiementConfigDto {
    
    private Long modePaiementId;
    private String configuration;  // JSON avec les clés API, etc.
    private String callbackUrl;
}


