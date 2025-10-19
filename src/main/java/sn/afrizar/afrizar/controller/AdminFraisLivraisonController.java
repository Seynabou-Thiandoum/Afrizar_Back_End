package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.FraisLivraisonDto;
import sn.afrizar.afrizar.service.FraisLivraisonService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/frais-livraison")
@RequiredArgsConstructor
@Tag(name = "Administration - Frais de Livraison", description = "API d'administration des frais de livraison")
@SecurityRequirement(name = "bearer-jwt")
public class AdminFraisLivraisonController {
    
    private final FraisLivraisonService fraisLivraisonService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir tous les frais de livraison", description = "Récupère tous les frais de livraison avec pagination")
    @ApiResponse(responseCode = "200", description = "Liste des frais de livraison")
    public ResponseEntity<Page<FraisLivraisonDto>> obtenirTousLesFraisLivraison(
        @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Admin: Récupération de tous les frais de livraison");
        Pageable pageable = PageRequest.of(page, size, Sort.by("type", "nom").ascending());
        Page<FraisLivraisonDto> fraisLivraison = fraisLivraisonService.obtenirTousLesFraisLivraison(pageable);
        return ResponseEntity.ok(fraisLivraison);
    }
    
    @GetMapping("/actifs")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir les frais de livraison actifs", description = "Récupère seulement les frais de livraison actifs")
    @ApiResponse(responseCode = "200", description = "Liste des frais de livraison actifs")
    public ResponseEntity<List<FraisLivraisonDto>> obtenirFraisLivraisonActifs() {
        log.info("Admin: Récupération des frais de livraison actifs");
        List<FraisLivraisonDto> fraisLivraison = fraisLivraisonService.obtenirFraisLivraisonActifs();
        return ResponseEntity.ok(fraisLivraison);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir un frais de livraison", description = "Récupère un frais de livraison par son ID")
    @ApiResponse(responseCode = "200", description = "Frais de livraison trouvé")
    @ApiResponse(responseCode = "404", description = "Frais de livraison non trouvé")
    public ResponseEntity<FraisLivraisonDto> obtenirFraisLivraison(@PathVariable Long id) {
        log.info("Admin: Récupération du frais de livraison ID: {}", id);
        try {
            FraisLivraisonDto fraisLivraison = fraisLivraisonService.obtenirFraisLivraison(id);
            return ResponseEntity.ok(fraisLivraison);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la récupération du frais de livraison {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer des frais de livraison", description = "Crée de nouveaux frais de livraison")
    @ApiResponse(responseCode = "200", description = "Frais de livraison créés")
    public ResponseEntity<FraisLivraisonDto> creerFraisLivraison(@RequestBody FraisLivraisonDto fraisLivraisonDto) {
        log.info("Admin: Création des frais de livraison: {}", fraisLivraisonDto.getNom());
        try {
            FraisLivraisonDto fraisLivraisonCree = fraisLivraisonService.creerFraisLivraison(fraisLivraisonDto);
            return ResponseEntity.ok(fraisLivraisonCree);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la création des frais de livraison: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour des frais de livraison", description = "Met à jour des frais de livraison existants")
    @ApiResponse(responseCode = "200", description = "Frais de livraison mis à jour")
    @ApiResponse(responseCode = "404", description = "Frais de livraison non trouvé")
    public ResponseEntity<FraisLivraisonDto> mettreAJourFraisLivraison(
        @PathVariable Long id,
        @RequestBody FraisLivraisonDto fraisLivraisonDto
    ) {
        log.info("Admin: Mise à jour des frais de livraison ID: {}", id);
        try {
            FraisLivraisonDto fraisLivraisonMisAJour = fraisLivraisonService.mettreAJourFraisLivraison(id, fraisLivraisonDto);
            return ResponseEntity.ok(fraisLivraisonMisAJour);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour des frais de livraison {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer des frais de livraison", description = "Supprime des frais de livraison")
    @ApiResponse(responseCode = "200", description = "Frais de livraison supprimés")
    @ApiResponse(responseCode = "404", description = "Frais de livraison non trouvé")
    public ResponseEntity<Map<String, String>> supprimerFraisLivraison(@PathVariable Long id) {
        log.info("Admin: Suppression des frais de livraison ID: {}", id);
        try {
            fraisLivraisonService.supprimerFraisLivraison(id);
            return ResponseEntity.ok(Map.of("message", "Frais de livraison supprimés avec succès"));
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression des frais de livraison {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/activer")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activer des frais de livraison", description = "Active des frais de livraison")
    @ApiResponse(responseCode = "200", description = "Frais de livraison activés")
    @ApiResponse(responseCode = "404", description = "Frais de livraison non trouvé")
    public ResponseEntity<FraisLivraisonDto> activerFraisLivraison(@PathVariable Long id) {
        log.info("Admin: Activation des frais de livraison ID: {}", id);
        try {
            FraisLivraisonDto fraisLivraison = fraisLivraisonService.activerFraisLivraison(id);
            return ResponseEntity.ok(fraisLivraison);
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'activation des frais de livraison {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/desactiver")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Désactiver des frais de livraison", description = "Désactive des frais de livraison")
    @ApiResponse(responseCode = "200", description = "Frais de livraison désactivés")
    @ApiResponse(responseCode = "404", description = "Frais de livraison non trouvé")
    public ResponseEntity<FraisLivraisonDto> desactiverFraisLivraison(@PathVariable Long id) {
        log.info("Admin: Désactivation des frais de livraison ID: {}", id);
        try {
            FraisLivraisonDto fraisLivraison = fraisLivraisonService.desactiverFraisLivraison(id);
            return ResponseEntity.ok(fraisLivraison);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la désactivation des frais de livraison {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/statistiques")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir les statistiques des frais de livraison", description = "Récupère les statistiques des frais de livraison")
    @ApiResponse(responseCode = "200", description = "Statistiques des frais de livraison")
    public ResponseEntity<Map<String, Object>> obtenirStatistiques() {
        log.info("Admin: Récupération des statistiques des frais de livraison");
        
        List<FraisLivraisonDto> fraisLivraisonActifs = fraisLivraisonService.obtenirFraisLivraisonActifs();
        List<FraisLivraisonDto> fraisExpress = fraisLivraisonService.obtenirFraisLivraisonParType("EXPRESS");
        List<FraisLivraisonDto> fraisStandard = fraisLivraisonService.obtenirFraisLivraisonParType("STANDARD");
        
        Map<String, Object> statistiques = new HashMap<>();
        statistiques.put("total", fraisLivraisonActifs.size());
        statistiques.put("express", fraisExpress.size());
        statistiques.put("standard", fraisStandard.size());
        statistiques.put("actifs", fraisLivraisonActifs.size());
        
        return ResponseEntity.ok(statistiques);
    }
}
