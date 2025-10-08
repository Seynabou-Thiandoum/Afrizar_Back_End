package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.AuthRequestDto;
import sn.afrizar.afrizar.dto.AuthResponseDto;
import sn.afrizar.afrizar.dto.InscriptionRequestDto;
import sn.afrizar.afrizar.service.AuthService;

import java.util.Map;

//@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "API d'authentification et d'inscription")
public class AuthController {

    @Autowired
    private AuthService authService;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/inscription")
    @Operation(summary = "Inscription d'un nouvel utilisateur", 
               description = "Permet l'inscription d'un client ou d'un vendeur")
    @ApiResponse(responseCode = "201", description = "Inscription réussie")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    public ResponseEntity<?> inscrire( @RequestBody InscriptionRequestDto request) {
        try {
            log.info("Demande d'inscription reçue pour: {} - Rôle: {}", request.getEmail(), request.getRole());
            
            AuthResponseDto response = authService.inscrireUtilisateur(request);
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
            
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'inscription: {}", e.getMessage());
            
            if (e.getMessage().contains("existe déjà")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("erreur", "Un compte avec cet email existe déjà"));
            }
            
            return ResponseEntity.badRequest()
                    .body(Map.of("erreur", e.getMessage()));
        }
    }
    
    @PostMapping("/connexion")
    @Operation(summary = "Connexion d'un utilisateur", 
               description = "Authentifie un utilisateur et retourne un token JWT")
    @ApiResponse(responseCode = "200", description = "Connexion réussie")
    @ApiResponse(responseCode = "401", description = "Identifiants invalides")
    public ResponseEntity<?> connecter(@RequestBody AuthRequestDto request) {
        try {
            log.info("Tentative de connexion pour: {}", request.getEmail());
            
            AuthResponseDto response = authService.connecterUtilisateur(request);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.error("Erreur lors de la connexion: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erreur", "Email ou mot de passe incorrect"));
        }
    }
    
    @PostMapping("/deconnexion")
    @Operation(summary = "Déconnexion d'un utilisateur", 
               description = "Invalide le token JWT de l'utilisateur")
    @ApiResponse(responseCode = "200", description = "Déconnexion réussie")
    public ResponseEntity<?> deconnecter(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                authService.deconnecterUtilisateur(token);
                
                log.info("Déconnexion réussie");
                return ResponseEntity.ok(Map.of("message", "Déconnexion réussie"));
            }
            
            return ResponseEntity.badRequest()
                    .body(Map.of("erreur", "Token manquant"));
            
        } catch (Exception e) {
            log.error("Erreur lors de la déconnexion: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("erreur", "Erreur lors de la déconnexion"));
        }
    }
    
    @GetMapping("/profil")
    @Operation(summary = "Obtenir le profil de l'utilisateur connecté", 
               description = "Retourne les informations du profil basées sur le token JWT")
    @ApiResponse(responseCode = "200", description = "Profil récupéré")
    @ApiResponse(responseCode = "401", description = "Token invalide")
    public ResponseEntity<?> obtenirProfil(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                if (authService.validerToken(token)) {
                    String email = authService.extraireEmailDuToken(token);
                    
                    // Ici, vous pourriez retourner plus d'informations du profil
                    return ResponseEntity.ok(Map.of(
                        "email", email,
                        "statut", "Connecté"
                    ));
                }
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erreur", "Token invalide"));
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du profil: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erreur", "Token invalide"));
        }
    }
    
    @GetMapping("/valider-token")
    @Operation(summary = "Valider un token JWT", 
               description = "Vérifie si un token JWT est valide")
    @ApiResponse(responseCode = "200", description = "Token validé")
    public ResponseEntity<?> validerToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                boolean valide = authService.validerToken(token);
                
                return ResponseEntity.ok(Map.of("valide", valide));
            }
            
            return ResponseEntity.ok(Map.of("valide", false));
            
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("valide", false));
        }
    }
    
    @GetMapping("/test")
    @Operation(summary = "Test de connexion au backend", 
               description = "Endpoint public pour tester la connexion au backend depuis le front")
    @ApiResponse(responseCode = "200", description = "Backend accessible")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(Map.of(
            "message", "✅ Backend Afrizar est accessible !",
            "timestamp", java.time.LocalDateTime.now().toString(),
            "version", "1.0.0",
            "status", "OK"
        ));
    }
    
    @GetMapping("/test-auth")
    @Operation(summary = "Test d'authentification", 
               description = "Endpoint protégé pour tester l'authentification depuis le front")
    @ApiResponse(responseCode = "200", description = "Authentification réussie")
    @ApiResponse(responseCode = "401", description = "Non authentifié")
    public ResponseEntity<?> testAuth(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                if (authService.validerToken(token)) {
                    String email = authService.extraireEmailDuToken(token);
                    
                    return ResponseEntity.ok(Map.of(
                        "message", "✅ Authentification réussie !",
                        "email", email,
                        "timestamp", java.time.LocalDateTime.now().toString(),
                        "authenticated", true
                    ));
                }
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                        "message", "❌ Token invalide ou manquant",
                        "authenticated", false
                    ));
            
        } catch (Exception e) {
            log.error("Erreur lors du test d'authentification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                        "message", "❌ Erreur d'authentification: " + e.getMessage(),
                        "authenticated", false
                    ));
        }
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check", 
               description = "Vérifie que le service d'authentification fonctionne")
    @ApiResponse(responseCode = "200", description = "Service disponible")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of(
            "service", "auth",
            "status", "UP",
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
}
