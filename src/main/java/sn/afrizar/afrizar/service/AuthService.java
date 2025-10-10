package sn.afrizar.afrizar.service;

import sn.afrizar.afrizar.dto.AuthRequestDto;
import sn.afrizar.afrizar.dto.AuthResponseDto;
import sn.afrizar.afrizar.dto.InscriptionRequestDto;
import sn.afrizar.afrizar.dto.UtilisateurDto;

public interface AuthService {
    
    /**
     * Inscription d'un nouvel utilisateur
     */
    AuthResponseDto inscrireUtilisateur(InscriptionRequestDto request);
    
    /**
     * Connexion d'un utilisateur
     */
    AuthResponseDto connecterUtilisateur(AuthRequestDto request);
    
    /**
     * Vérification de la validité d'un token
     */
    boolean validerToken(String token);
    
    /**
     * Extraction de l'email depuis un token
     */
    String extraireEmailDuToken(String token);
    
    /**
     * Génération d'un token JWT
     */
    String genererToken(String email);
    
    /**
     * Déconnexion (invalidation du token)
     */
    void deconnecterUtilisateur(String token);
    
    /**
     * Extraction du rôle depuis un token
     */
    String extraireRoleDuToken(String token);
    
    /**
     * Extraction de l'ID utilisateur depuis un token
     */
    Long extraireUserIdDuToken(String token);
    
    /**
     * Création d'un utilisateur par l'admin (tous les rôles)
     */
    UtilisateurDto creerUtilisateurParAdmin(InscriptionRequestDto request);
}
