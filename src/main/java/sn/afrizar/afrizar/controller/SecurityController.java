package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.security.PasswordPolicyValidator;
import sn.afrizar.afrizar.security.SecurityAuditLogger;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
@Tag(name = "Sécurité", description = "API de gestion de la sécurité")
public class SecurityController {

    private final PasswordPolicyValidator passwordPolicyValidator;
    private final SecurityAuditLogger securityAuditLogger;

    @PostMapping("/validate-password")
    @Operation(summary = "Valider un mot de passe", 
               description = "Vérifie si un mot de passe respecte la politique de sécurité")
    @ApiResponse(responseCode = "200", description = "Validation effectuée")
    public ResponseEntity<Map<String, Object>> validatePassword(@RequestBody Map<String, String> request) {
        String password = request.get("password");
        
        if (password == null || password.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Mot de passe requis"));
        }
        
        PasswordPolicyValidator.PasswordValidationResult result = 
            passwordPolicyValidator.validatePassword(password);
        
        return ResponseEntity.ok(Map.of(
            "valid", result.isValid(),
            "strength", result.getStrength(),
            "strengthDescription", result.getStrengthDescription(),
            "errors", result.getErrors(),
            "warnings", result.getWarnings()
        ));
    }

    @GetMapping("/security-info")
    @Operation(summary = "Informations de sécurité", 
               description = "Retourne des informations sur la configuration de sécurité")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponse(responseCode = "200", description = "Informations récupérées")
    public ResponseEntity<Map<String, Object>> getSecurityInfo() {
        return ResponseEntity.ok(Map.of(
            "passwordPolicy", Map.of(
                "minLength", 8,
                "maxLength", 128,
                "requireUppercase", true,
                "requireLowercase", true,
                "requireDigit", true,
                "requireSpecialChar", true
            ),
            "rateLimiting", Map.of(
                "authRequestsPerMinute", 5,
                "generalRequestsPerMinute", 100,
                "apiRequestsPerMinute", 200
            ),
            "jwt", Map.of(
                "expirationHours", 24,
                "refreshExpirationDays", 7
            )
        ));
    }

    @PostMapping("/test-audit")
    @Operation(summary = "Test d'audit", 
               description = "Génère un événement d'audit de test (admin uniquement)")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponse(responseCode = "200", description = "Test d'audit effectué")
    public ResponseEntity<Map<String, String>> testAudit(@RequestParam String message) {
        securityAuditLogger.logSecurityEvent("TEST_AUDIT", message, "127.0.0.1");
        
        return ResponseEntity.ok(Map.of(
            "message", "Événement d'audit de test généré",
            "details", message
        ));
    }
}


