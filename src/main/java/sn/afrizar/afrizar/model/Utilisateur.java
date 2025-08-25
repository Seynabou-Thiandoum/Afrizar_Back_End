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
@Inheritance(strategy = InheritanceType.JOINED)
public class Utilisateur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(nullable = false)
    private String prenom;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String motDePasse;
    
    private String telephone;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    private boolean actif = true;
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();
    
    @Column(name = "derniere_connexion")
    private LocalDateTime derniereConnexion;
    
    // Enum pour les rôles
    public enum Role {
        CLIENT,
        VENDEUR,
        ADMIN,
        SUPPORT
    }
}