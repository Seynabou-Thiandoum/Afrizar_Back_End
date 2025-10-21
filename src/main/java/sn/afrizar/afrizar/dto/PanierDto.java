package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanierDto {
    
    private Long id;
    private Long clientId;
    private String clientNom;
    private List<PanierItemDto> items = new ArrayList<>();
    private BigDecimal montantTotal;
    private int nombreTotalArticles;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private boolean actif;
}



