package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.ModePaiementConfigDto;
import sn.afrizar.afrizar.dto.ModePaiementCreateDto;
import sn.afrizar.afrizar.dto.ModePaiementDto;
import sn.afrizar.afrizar.dto.ModePaiementUpdateDto;
import sn.afrizar.afrizar.model.ModePaiement;
import sn.afrizar.afrizar.service.ModePaiementService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Modes de Paiement", description = "Gestion des modes de paiement")
public class ModePaiementController {
    
    private final ModePaiementService modePaiementService;
    
    // ========== ENDPOINTS PUBLICS ==========
    
    @GetMapping("/public/modes-paiement")
    @Operation(summary = "Récupérer tous les modes de paiement actifs (public)")
    public ResponseEntity<List<ModePaiementDto>> getActivesModesPaiement() {
        try {
            List<ModePaiementDto> modes = modePaiementService.getActivesModesPaiement();
            log.info("✅ Récupération de {} modes de paiement actifs", modes.size());
            return ResponseEntity.ok(modes);
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des modes de paiement: {}", e.getMessage(), e);
            return ResponseEntity.ok(List.of()); // Retourner une liste vide en cas d'erreur
        }
    }
    
    @GetMapping("/public/modes-paiement/code/{code}")
    @Operation(summary = "Récupérer un mode de paiement par code (public)")
    public ResponseEntity<ModePaiementDto> getModePaiementByCode(@PathVariable String code) {
        return ResponseEntity.ok(modePaiementService.getModePaiementByCode(code));
    }
    
    @GetMapping("/public/modes-paiement/type/{type}")
    @Operation(summary = "Récupérer les modes de paiement par type (public)")
    public ResponseEntity<List<ModePaiementDto>> getModesPaiementByType(
            @PathVariable ModePaiement.TypePaiement type) {
        return ResponseEntity.ok(modePaiementService.getModesPaiementByType(type));
    }
    
    // ========== ENDPOINTS ADMIN ==========
    
    @GetMapping("/admin/modes-paiement")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Récupérer tous les modes de paiement (admin)")
    public ResponseEntity<List<ModePaiementDto>> getAllModesPaiement() {
        return ResponseEntity.ok(modePaiementService.getAllModesPaiement());
    }
    
    @GetMapping("/admin/modes-paiement/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Récupérer un mode de paiement par ID (admin)")
    public ResponseEntity<ModePaiementDto> getModePaiementById(@PathVariable Long id) {
        return ResponseEntity.ok(modePaiementService.getModePaiementById(id));
    }
    
    @PostMapping("/admin/modes-paiement")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Créer un nouveau mode de paiement")
    public ResponseEntity<ModePaiementDto> createModePaiement(
            @Valid @RequestBody ModePaiementCreateDto dto,
            Authentication authentication) {
        sn.afrizar.afrizar.model.Utilisateur utilisateur = (sn.afrizar.afrizar.model.Utilisateur) authentication.getPrincipal();
        String username = utilisateur.getEmail();
        ModePaiementDto created = modePaiementService.createModePaiement(dto, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/admin/modes-paiement/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Mettre à jour un mode de paiement")
    public ResponseEntity<ModePaiementDto> updateModePaiement(
            @PathVariable Long id,
            @Valid @RequestBody ModePaiementUpdateDto dto,
            Authentication authentication) {
        sn.afrizar.afrizar.model.Utilisateur utilisateur = (sn.afrizar.afrizar.model.Utilisateur) authentication.getPrincipal();
        String username = utilisateur.getEmail();
        ModePaiementDto updated = modePaiementService.updateModePaiement(id, dto, username);
        return ResponseEntity.ok(updated);
    }
    
    @PutMapping("/admin/modes-paiement/{id}/configuration")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Mettre à jour la configuration d'un mode de paiement")
    public ResponseEntity<Void> updateConfiguration(
            @PathVariable Long id,
            @Valid @RequestBody ModePaiementConfigDto configDto,
            Authentication authentication) {
        sn.afrizar.afrizar.model.Utilisateur utilisateur = (sn.afrizar.afrizar.model.Utilisateur) authentication.getPrincipal();
        String username = utilisateur.getEmail();
        modePaiementService.updateConfiguration(id, configDto, username);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/admin/modes-paiement/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Activer/Désactiver un mode de paiement")
    public ResponseEntity<Void> toggleActif(
            @PathVariable Long id,
            @RequestParam Boolean actif,
            Authentication authentication) {
        sn.afrizar.afrizar.model.Utilisateur utilisateur = (sn.afrizar.afrizar.model.Utilisateur) authentication.getPrincipal();
        String username = utilisateur.getEmail();
        modePaiementService.toggleActif(id, actif, username);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/admin/modes-paiement/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Supprimer un mode de paiement")
    public ResponseEntity<Void> deleteModePaiement(@PathVariable Long id) {
        modePaiementService.deleteModePaiement(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/admin/modes-paiement/reorder")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Réorganiser les modes de paiement")
    public ResponseEntity<Void> reorderModesPaiement(@RequestBody List<Long> orderedIds) {
        modePaiementService.reorderModesPaiement(orderedIds);
        return ResponseEntity.ok().build();
    }
}

