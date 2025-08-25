package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.afrizar.afrizar.model.Utilisateur;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    
    private String token;
    private String tokenType = "Bearer";
    private Long utilisateurId;
    private String email;
    private String nom;
    private String prenom;
    private Utilisateur.Role role;
    private boolean actif;
    
    // Propriétés spécifiques selon le rôle
    private String nomBoutique; // Pour les vendeurs
    private boolean verifie; // Pour les vendeurs
    private Integer pointsFidelite; // Pour les clients
    
    public AuthResponseDto(String token, UtilisateurDto utilisateur) {
        this.token = token;
        this.utilisateurId = utilisateur.getId();
        this.email = utilisateur.getEmail();
        this.nom = utilisateur.getNom();
        this.prenom = utilisateur.getPrenom();
        this.role = utilisateur.getRole();
        this.actif = utilisateur.isActif();
    }
}
