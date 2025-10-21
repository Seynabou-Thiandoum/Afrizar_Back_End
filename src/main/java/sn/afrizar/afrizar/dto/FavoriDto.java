package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriDto {
    private Long id;
    private Long clientId;
    private Long produitId;
    private String produitNom;
    private String produitImageUrl;
    private Double produitPrix;
    private String vendeurNom;
    private LocalDateTime dateAjout;
}



