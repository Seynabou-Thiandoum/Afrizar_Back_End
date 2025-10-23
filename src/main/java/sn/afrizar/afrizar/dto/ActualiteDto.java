package sn.afrizar.afrizar.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActualiteDto {
    private Long id;
    private String titre;
    private String resume;
    private String contenu;
    private String imageUrl;
    private String auteur;
    private LocalDateTime datePublication;
    private String categorie;
    private List<String> tags;
    private Long nombreLikes;
    private Long nombreCommentaires;
    private Long nombrePartages;
    private Boolean estVisible;
    private Boolean estTendance;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
}
