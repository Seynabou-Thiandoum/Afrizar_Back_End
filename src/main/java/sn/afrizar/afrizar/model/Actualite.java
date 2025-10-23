package sn.afrizar.afrizar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "actualites")
public class Actualite {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String titre;
    
    @Column(length = 500)
    private String resume;
    
    @Column(length = 5000)
    private String contenu;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(nullable = false, length = 100)
    private String auteur;
    
    @Column(name = "date_publication")
    private LocalDateTime datePublication;
    
    @Column(length = 50)
    private String categorie;
    
    @ElementCollection
    @CollectionTable(name = "actualite_tags", joinColumns = @JoinColumn(name = "actualite_id"))
    @Column(name = "tag")
    private java.util.List<String> tags;
    
    @Column(name = "nombre_likes")
    private Long nombreLikes = 0L;
    
    @Column(name = "nombre_commentaires")
    private Long nombreCommentaires = 0L;
    
    @Column(name = "nombre_partages")
    private Long nombrePartages = 0L;
    
    @Column(name = "est_visible")
    private Boolean estVisible = true;
    
    @Column(name = "est_tendance")
    private Boolean estTendance = false;
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }
}
