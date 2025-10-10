package sn.afrizar.afrizar.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.afrizar.afrizar.dto.*;
import sn.afrizar.afrizar.model.Client;
import sn.afrizar.afrizar.model.Utilisateur;
import sn.afrizar.afrizar.model.Vendeur;
import sn.afrizar.afrizar.repository.UtilisateurRepository;
import sn.afrizar.afrizar.security.PasswordPolicyValidator;
import sn.afrizar.afrizar.security.SecurityAuditLogger;
import sn.afrizar.afrizar.service.AuthService;
import sn.afrizar.afrizar.service.ClientService;
import sn.afrizar.afrizar.service.VendeurService;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    
    private final UtilisateurRepository utilisateurRepository;
    private final ClientService clientService;
    private final VendeurService vendeurService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordPolicyValidator passwordPolicyValidator;
    private final SecurityAuditLogger securityAuditLogger;
    
    @Value("${app.jwt.secret:afrizarSecretKeyForJWT2024}")
    private String jwtSecret;
    
    @Value("${app.jwt.expiration:86400000}") // 24 heures par défaut
    private Long jwtExpiration;
    
    // Set pour stocker les tokens invalidés (en production, utilisez Redis)
    private final Set<String> tokensInvalides = new HashSet<>();
    
    @Override
    public AuthResponseDto inscrireUtilisateur(InscriptionRequestDto request) {
        log.info("Inscription d'un nouvel utilisateur: {} - Rôle: {}", request.getEmail(), request.getRole());
        
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Un compte avec cet email existe déjà");
        }
        
        // Valider la politique de mot de passe
        PasswordPolicyValidator.PasswordValidationResult passwordValidation = 
            passwordPolicyValidator.validatePassword(request.getMotDePasse());
        
        if (!passwordValidation.isValid()) {
            String errorMessage = "Mot de passe invalide: " + String.join(", ", passwordValidation.getErrors());
            throw new RuntimeException(errorMessage);
        }
        
        // Avertir si le mot de passe est faible
        if (!passwordValidation.getWarnings().isEmpty()) {
            log.warn("Mot de passe faible pour l'utilisateur {}: {}", 
                    request.getEmail(), String.join(", ", passwordValidation.getWarnings()));
        }
        
        // Encoder le mot de passe
        String motDePasseEncode = passwordEncoder.encode(request.getMotDePasse());
        
        UtilisateurDto utilisateur;
        
        try {
            switch (request.getRole()) {
                case CLIENT:
                    utilisateur = creerClient(request, motDePasseEncode);
                    break;
                case VENDEUR:
                    utilisateur = creerVendeur(request, motDePasseEncode);
                    break;
                case ADMIN:
                case SUPPORT:
                    throw new RuntimeException("L'inscription publique n'est pas autorisée pour le rôle " + request.getRole() + 
                                             ". Les comptes ADMIN et SUPPORT doivent être créés par un administrateur via l'endpoint /api/admin/utilisateurs/creer");
                default:
                    throw new RuntimeException("Rôle non reconnu: " + request.getRole());
            }
            
            // Générer le token JWT
            String token = genererToken(utilisateur.getEmail());
            
            // Créer la réponse
            AuthResponseDto response = new AuthResponseDto(token, utilisateur);
            
            // Ajouter les propriétés spécifiques selon le rôle
            if (request.getRole() == Utilisateur.Role.VENDEUR) {
                response.setNomBoutique(request.getNomBoutique());
                response.setVerifie(false); // Les vendeurs doivent être vérifiés par un admin
            } else if (request.getRole() == Utilisateur.Role.CLIENT) {
                response.setPointsFidelite(0);
            }
            
            log.info("Inscription réussie pour l'utilisateur: {}", utilisateur.getEmail());
            
            // Audit log
            securityAuditLogger.logRegistrationSuccess(utilisateur.getEmail(), "unknown", request.getRole().name());
            
            return response;
            
        } catch (Exception e) {
            log.error("Erreur lors de l'inscription: {}", e.getMessage());
            
            // Audit log
            securityAuditLogger.logRegistrationFailure(request.getEmail(), "unknown", e.getMessage());
            
            throw new RuntimeException("Erreur lors de l'inscription: " + e.getMessage());
        }
    }
    
    private UtilisateurDto creerClient(InscriptionRequestDto request, String motDePasseEncode) {
        ClientDto clientDto = new ClientDto();
        clientDto.setNom(request.getNom());
        clientDto.setPrenom(request.getPrenom());
        clientDto.setEmail(request.getEmail());
        clientDto.setMotDePasse(motDePasseEncode);
        clientDto.setTelephone(request.getTelephone());
        clientDto.setAdresse(request.getAdresse());
        clientDto.setVille(request.getVille());
        clientDto.setCodePostal(request.getCodePostal());
        clientDto.setPays(request.getPays() != null ? request.getPays() : "Sénégal");
        
        return clientService.creerClient(clientDto);
    }
    
    private UtilisateurDto creerVendeur(InscriptionRequestDto request, String motDePasseEncode) {
        VendeurDto vendeurDto = new VendeurDto();
        vendeurDto.setNom(request.getNom());
        vendeurDto.setPrenom(request.getPrenom());
        vendeurDto.setEmail(request.getEmail());
        vendeurDto.setMotDePasse(motDePasseEncode);
        vendeurDto.setTelephone(request.getTelephone());
        vendeurDto.setNomBoutique(request.getNomBoutique());
        vendeurDto.setDescription(request.getDescription());
        vendeurDto.setAdresseBoutique(request.getAdresseBoutique());
        vendeurDto.setSpecialites(request.getSpecialites());
        
        return vendeurService.creerVendeur(vendeurDto);
    }
    
    @Override
    public AuthResponseDto connecterUtilisateur(AuthRequestDto request) {
        log.info("Tentative de connexion pour: {}", request.getEmail());
        
        // Rechercher l'utilisateur par email
        Utilisateur utilisateur = utilisateurRepository.findByEmailAndActif(request.getEmail(), true)
                .orElse(null);
        
        if (utilisateur == null) {
            // Audit log pour tentative de connexion avec email inexistant
            securityAuditLogger.logAuthenticationFailure(request.getEmail(), "unknown", "Email inexistant");
            throw new RuntimeException("Email ou mot de passe incorrect");
        }
        
        // Vérifier le mot de passe
        if (!passwordEncoder.matches(request.getMotDePasse(), utilisateur.getMotDePasse())) {
            // Audit log pour mot de passe incorrect
            securityAuditLogger.logAuthenticationFailure(request.getEmail(), "unknown", "Mot de passe incorrect");
            throw new RuntimeException("Email ou mot de passe incorrect");
        }
        
        // Mettre à jour la dernière connexion
        utilisateur.setDerniereConnexion(LocalDateTime.now());
        utilisateurRepository.save(utilisateur);
        
        // Générer le token JWT
        String token = genererToken(utilisateur.getEmail());
        
        // Convertir vers DTO
        UtilisateurDto utilisateurDto = convertirVersDto(utilisateur);
        
        // Créer la réponse
        AuthResponseDto response = new AuthResponseDto(token, utilisateurDto);
        
        // Ajouter les propriétés spécifiques selon le rôle
        if (utilisateur instanceof Vendeur) {
            Vendeur vendeur = (Vendeur) utilisateur;
            response.setNomBoutique(vendeur.getNomBoutique());
            response.setVerifie(vendeur.isVerifie());
        } else if (utilisateur instanceof Client) {
            Client client = (Client) utilisateur;
            response.setPointsFidelite(client.getPointsFidelite());
        }
        
        log.info("Connexion réussie pour: {}", utilisateur.getEmail());
        
        // Audit log
        securityAuditLogger.logAuthenticationSuccess(utilisateur.getEmail(), "unknown");
        
        return response;
    }
    
    private UtilisateurDto convertirVersDto(Utilisateur utilisateur) {
        UtilisateurDto dto = new UtilisateurDto();
        dto.setId(utilisateur.getId());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setEmail(utilisateur.getEmail());
        dto.setTelephone(utilisateur.getTelephone());
        dto.setRole(utilisateur.getRole());
        dto.setActif(utilisateur.isActif());
        dto.setDateCreation(utilisateur.getDateCreation());
        dto.setDerniereConnexion(utilisateur.getDerniereConnexion());
        return dto;
    }
    
    @Override
    public String genererToken(String email) {
        // Récupérer l'utilisateur pour avoir son rôle
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtExpiration);
        
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.builder()
                .setSubject(email)
                .claim("role", utilisateur.getRole().name())
                .claim("userId", utilisateur.getId())
                .claim("nom", utilisateur.getNom())
                .claim("prenom", utilisateur.getPrenom())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    @Override
    public boolean validerToken(String token) {
        try {
            if (tokensInvalides.contains(token)) {
                return false;
            }
            
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.debug("Token invalide: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public String extraireEmailDuToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return claims.getSubject();
        } catch (Exception e) {
            log.error("Erreur lors de l'extraction de l'email du token: {}", e.getMessage());
            throw new RuntimeException("Token invalide");
        }
    }
    
    @Override
    public void deconnecterUtilisateur(String token) {
        tokensInvalides.add(token);
        log.info("Token invalidé pour déconnexion");
    }
    
    @Override
    public String extraireRoleDuToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return claims.get("role", String.class);
        } catch (Exception e) {
            log.error("Erreur lors de l'extraction du rôle du token: {}", e.getMessage());
            throw new RuntimeException("Token invalide");
        }
    }
    
    @Override
    public Long extraireUserIdDuToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            log.error("Erreur lors de l'extraction de l'ID utilisateur du token: {}", e.getMessage());
            throw new RuntimeException("Token invalide");
        }
    }
    
    @Override
    public UtilisateurDto creerUtilisateurParAdmin(InscriptionRequestDto request) {
        log.info("Admin: Création d'un utilisateur avec le rôle: {}", request.getRole());
        
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Un compte avec cet email existe déjà");
        }
        
        // Valider la politique de mot de passe
        PasswordPolicyValidator.PasswordValidationResult passwordValidation = 
            passwordPolicyValidator.validatePassword(request.getMotDePasse());
        
        if (!passwordValidation.isValid()) {
            String errorMessage = "Mot de passe invalide: " + String.join(", ", passwordValidation.getErrors());
            throw new RuntimeException(errorMessage);
        }
        
        // Encoder le mot de passe
        String motDePasseEncode = passwordEncoder.encode(request.getMotDePasse());
        
        UtilisateurDto utilisateur;
        
        try {
            switch (request.getRole()) {
                case CLIENT:
                    utilisateur = creerClient(request, motDePasseEncode);
                    break;
                case VENDEUR:
                    utilisateur = creerVendeur(request, motDePasseEncode);
                    break;
                case ADMIN:
                case SUPPORT:
                    utilisateur = creerUtilisateurSimple(request, motDePasseEncode);
                    break;
                default:
                    throw new RuntimeException("Rôle non reconnu: " + request.getRole());
            }
            
            log.info("Admin: Utilisateur créé avec succès - Email: {}, Rôle: {}", 
                    utilisateur.getEmail(), utilisateur.getRole());
            
            // Audit log
            securityAuditLogger.logRegistrationSuccess(utilisateur.getEmail(), "admin", request.getRole().name());
            
            return utilisateur;
            
        } catch (Exception e) {
            log.error("Erreur lors de la création de l'utilisateur par admin: {}", e.getMessage());
            
            // Audit log
            securityAuditLogger.logRegistrationFailure(request.getEmail(), "admin", e.getMessage());
            
            throw new RuntimeException("Erreur lors de la création de l'utilisateur: " + e.getMessage());
        }
    }
    
    private UtilisateurDto creerUtilisateurSimple(InscriptionRequestDto request, String motDePasseEncode) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(motDePasseEncode);
        utilisateur.setTelephone(request.getTelephone());
        utilisateur.setRole(request.getRole());
        utilisateur.setActif(true);
        utilisateur.setDateCreation(LocalDateTime.now());
        
        utilisateur = utilisateurRepository.save(utilisateur);
        
        return convertirVersDto(utilisateur);
    }
}
