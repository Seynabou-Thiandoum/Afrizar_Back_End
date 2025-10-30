package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.afrizar.afrizar.model.ModePaiement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO pour afficher les informations d'un mode de paiement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModePaiementDto {
    
    private Long id;
    private String nom;
    private String code;
    private ModePaiement.TypePaiement type;
    private String description;
    private String logo;
    private Boolean actif;
    private String instructions;
    private BigDecimal fraisPourcentage;
    private BigDecimal fraisFixe;
    private BigDecimal montantMinimum;
    private BigDecimal montantMaximum;
    private String paysSupportes;
    private Integer delaiTraitement;
    private Integer ordre;
    private ModePaiement.Environnement environnement;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    
    // Constructor pour affichage client (sans informations sensibles)
    public static ModePaiementDto fromEntityForClient(ModePaiement entity) {
        ModePaiementDto dto = new ModePaiementDto();
        dto.setId(entity.getId());
        dto.setNom(entity.getNom());
        dto.setCode(entity.getCode());
        dto.setType(entity.getType());
        dto.setDescription(entity.getDescription());
        dto.setLogo(entity.getLogo());
        dto.setActif(entity.getActif());
        dto.setInstructions(entity.getInstructions());
        dto.setFraisPourcentage(entity.getFraisPourcentage());
        dto.setFraisFixe(entity.getFraisFixe());
        dto.setMontantMinimum(entity.getMontantMinimum());
        dto.setMontantMaximum(entity.getMontantMaximum());
        dto.setDelaiTraitement(entity.getDelaiTraitement());
        dto.setOrdre(entity.getOrdre());
        return dto;
    }
    
    // Constructor pour affichage admin (toutes les infos sauf config sensible)
    public static ModePaiementDto fromEntityForAdmin(ModePaiement entity) {
        ModePaiementDto dto = fromEntityForClient(entity);
        dto.setPaysSupportes(entity.getPaysSupportes());
        dto.setEnvironnement(entity.getEnvironnement());
        dto.setDateCreation(entity.getDateCreation());
        dto.setDateModification(entity.getDateModification());
        return dto;
    }
}


