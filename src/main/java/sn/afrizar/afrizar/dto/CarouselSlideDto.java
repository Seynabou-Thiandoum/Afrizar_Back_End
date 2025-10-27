package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarouselSlideDto {
    private Long id;
    private String titre;
    private String sousTitre;
    private String imageUrl;
    private String badge;
    private String boutonTexte;
    private String boutonLien;
    private Integer ordreAffichage;
    private Boolean actif;
}
