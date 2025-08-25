package sn.afrizar.afrizar.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDto {
    
    @Email(message = "Format d'email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    public @NotBlank(message = "Le mot de passe est obligatoire") String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(@NotBlank(message = "Le mot de passe est obligatoire") String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public @Email(message = "Format d'email invalide") @NotBlank(message = "L'email est obligatoire") String getEmail() {
        return email;
    }

    public void setEmail(@Email(message = "Format d'email invalide") @NotBlank(message = "L'email est obligatoire") String email) {
        this.email = email;
    }

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;
}
