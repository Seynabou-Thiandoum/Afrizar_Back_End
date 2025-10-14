package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VendeurDto extends UtilisateurDto {
    
    private String nomBoutique;
    private String description;
    private String adresseBoutique;
    private String motDePasse;
    private BigDecimal rating;
    private Integer nombreEvaluations;
    private BigDecimal tauxCommissionPersonnalise;
    private boolean verifie;
    private boolean publie;
    private String photoUrl;
    private String specialites;
}

