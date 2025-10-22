package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoldeCommissionDto {
    
    private BigDecimal soldeTotal;
    private BigDecimal soldeDisponible;
    private BigDecimal soldeEnAttente;
    private BigDecimal soldePaye;
    private Long nombreCommissions;
}
