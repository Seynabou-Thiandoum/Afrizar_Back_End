package sn.afrizar.afrizar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import sn.afrizar.afrizar.model.Client;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClientDto extends UtilisateurDto {
    
    private String adresse;
    private String ville;
    private String codePostal;
    private String motDePasse;
    private String pays;
    private Integer pointsFidelite;
    private Client.TypeLivraison typeLivraisonPrefere;
}

