package sn.afrizar.afrizar.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import sn.afrizar.afrizar.model.Utilisateur;

@NoArgsConstructor
@AllArgsConstructor

public class InscriptionRequestDto {

    public @NotBlank(message = "Le nom est obligatoire") @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères") String getNom() {
        return nom;
    }

    public void setNom(@NotBlank(message = "Le nom est obligatoire") @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères") String nom) {
        this.nom = nom;
    }

    public @NotBlank(message = "Le prénom est obligatoire") @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères") String getPrenom() {
        return prenom;
    }

    public void setPrenom(@NotBlank(message = "Le prénom est obligatoire") @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères") String prenom) {
        this.prenom = prenom;
    }

    public @Email(message = "Format d'email invalide") @NotBlank(message = "L'email est obligatoire") String getEmail() {
        return email;
    }

    public void setEmail(@Email(message = "Format d'email invalide") @NotBlank(message = "L'email est obligatoire") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Le mot de passe est obligatoire") @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères") String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(@NotBlank(message = "Le mot de passe est obligatoire") @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères") String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public @Pattern(regexp = "^(\\+221|00221)?[0-9]{8,9}$", message = "Format de téléphone invalide") String getTelephone() {
        return telephone;
    }

    public void setTelephone(@Pattern(regexp = "^(\\+221|00221)?[0-9]{8,9}$", message = "Format de téléphone invalide") String telephone) {
        this.telephone = telephone;
    }

    public Utilisateur.Role getRole() {
        return role;
    }

    public void setRole(Utilisateur.Role role) {
        this.role = role;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getNomBoutique() {
        return nomBoutique;
    }

    public void setNomBoutique(String nomBoutique) {
        this.nomBoutique = nomBoutique;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdresseBoutique() {
        return adresseBoutique;
    }

    public void setAdresseBoutique(String adresseBoutique) {
        this.adresseBoutique = adresseBoutique;
    }

    public String getSpecialites() {
        return specialites;
    }

    public void setSpecialites(String specialites) {
        this.specialites = specialites;
    }

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String nom;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String prenom;
    
    @Email(message = "Format d'email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;
    
    @Pattern(regexp = "^(\\+221|00221)?[0-9]{8,9}$", message = "Format de téléphone invalide")
    private String telephone;
    
    private Utilisateur.Role role = Utilisateur.Role.CLIENT;
    
    // Champs spécifiques pour les clients
    private String adresse;
    private String ville;
    private String codePostal;
    private String pays;
    
    // Champs spécifiques pour les vendeurs
    private String nomBoutique;
    private String description;
    private String adresseBoutique;
    private String specialites;

}
