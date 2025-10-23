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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.ActualiteDto;
import sn.afrizar.afrizar.dto.CreateActualiteDto;
import sn.afrizar.afrizar.service.ActualiteService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/actualites")
@RequiredArgsConstructor
@Tag(name = "Admin Actualités", description = "API d'administration pour la gestion des actualités")
@SecurityRequirement(name = "bearer-jwt")
public class AdminActualiteController {
    
    private final ActualiteService actualiteService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir toutes les actualités pour l'admin", 
               description = "Récupère toutes les actualités pour la gestion admin")
    @ApiResponse(responseCode = "200", description = "Actualités récupérées")
    @ApiResponse(responseCode = "403", description = "Accès refusé - Admin uniquement")
    public ResponseEntity<Page<ActualiteDto>> obtenirToutesLesActualites(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size) {
        
        log.info("Admin: Récupération de toutes les actualités");
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ActualiteDto> actualites = actualiteService.obtenirToutesLesActualites(pageable);
        
        return ResponseEntity.ok(actualites);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer une nouvelle actualité", 
               description = "Permet à l'admin de créer une nouvelle actualité")
    @ApiResponse(responseCode = "201", description = "Actualité créée")
    @ApiResponse(responseCode = "403", description = "Accès refusé - Admin uniquement")
    public ResponseEntity<ActualiteDto> creerActualite(@RequestBody CreateActualiteDto createActualiteDto) {
        
        log.info("Admin: Création d'une nouvelle actualité: {}", createActualiteDto.getTitre());
        
        ActualiteDto actualiteCreee = actualiteService.creerActualite(createActualiteDto);
        
        return ResponseEntity.ok(actualiteCreee);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour une actualité", 
               description = "Permet à l'admin de modifier une actualité")
    @ApiResponse(responseCode = "200", description = "Actualité mise à jour")
    @ApiResponse(responseCode = "403", description = "Accès refusé - Admin uniquement")
    @ApiResponse(responseCode = "404", description = "Actualité non trouvée")
    public ResponseEntity<ActualiteDto> mettreAJourActualite(
            @Parameter(description = "ID de l'actualité") @PathVariable Long id,
            @RequestBody CreateActualiteDto createActualiteDto) {
        
        log.info("Admin: Mise à jour de l'actualité avec ID: {}", id);
        
        ActualiteDto actualiteMiseAJour = actualiteService.mettreAJourActualite(id, createActualiteDto);
        
        return ResponseEntity.ok(actualiteMiseAJour);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une actualité", 
               description = "Permet à l'admin de supprimer une actualité")
    @ApiResponse(responseCode = "200", description = "Actualité supprimée")
    @ApiResponse(responseCode = "403", description = "Accès refusé - Admin uniquement")
    @ApiResponse(responseCode = "404", description = "Actualité non trouvée")
    public ResponseEntity<Map<String, Object>> supprimerActualite(
            @Parameter(description = "ID de l'actualité") @PathVariable Long id) {
        
        log.info("Admin: Suppression de l'actualité avec ID: {}", id);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            actualiteService.supprimerActualite(id);
            result.put("success", true);
            result.put("message", "Actualité supprimée avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de l'actualité", e);
            result.put("success", false);
            result.put("message", "Erreur lors de la suppression");
        }
        
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{id}/visibilite")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Changer la visibilité d'une actualité", 
               description = "Permet à l'admin de rendre visible ou invisible une actualité")
    @ApiResponse(responseCode = "200", description = "Visibilité modifiée")
    @ApiResponse(responseCode = "403", description = "Accès refusé - Admin uniquement")
    @ApiResponse(responseCode = "404", description = "Actualité non trouvée")
    public ResponseEntity<ActualiteDto> changerVisibiliteActualite(
            @Parameter(description = "ID de l'actualité") @PathVariable Long id,
            @Parameter(description = "Visible") @RequestParam boolean visible) {
        
        log.info("Admin: Changement de visibilité de l'actualité {} vers {}", id, visible);
        
        ActualiteDto actualite = actualiteService.changerVisibiliteActualite(id, visible);
        
        return ResponseEntity.ok(actualite);
    }
    
    @PutMapping("/{id}/tendance")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Marquer une actualité comme tendance", 
               description = "Permet à l'admin de marquer une actualité comme tendance")
    @ApiResponse(responseCode = "200", description = "Statut tendance modifié")
    @ApiResponse(responseCode = "403", description = "Accès refusé - Admin uniquement")
    @ApiResponse(responseCode = "404", description = "Actualité non trouvée")
    public ResponseEntity<ActualiteDto> marquerActualiteTendance(
            @Parameter(description = "ID de l'actualité") @PathVariable Long id,
            @Parameter(description = "Tendance") @RequestParam boolean tendance) {
        
        log.info("Admin: Marquage de l'actualité {} comme tendance: {}", id, tendance);
        
        ActualiteDto actualite = actualiteService.marquerActualiteTendance(id, tendance);
        
        return ResponseEntity.ok(actualite);
    }
    
    @PostMapping("/{id}/likes")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Incrémenter les likes d'une actualité", 
               description = "Permet à l'admin d'incrémenter manuellement les likes")
    @ApiResponse(responseCode = "200", description = "Likes incrémentés")
    @ApiResponse(responseCode = "403", description = "Accès refusé - Admin uniquement")
    public ResponseEntity<ActualiteDto> incrementerLikes(
            @Parameter(description = "ID de l'actualité") @PathVariable Long id) {
        
        log.info("Admin: Incrémentation des likes pour l'actualité {}", id);
        
        ActualiteDto actualite = actualiteService.incrementerLikes(id);
        
        return ResponseEntity.ok(actualite);
    }
    
    @PostMapping("/{id}/commentaires")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Incrémenter les commentaires d'une actualité", 
               description = "Permet à l'admin d'incrémenter manuellement les commentaires")
    @ApiResponse(responseCode = "200", description = "Commentaires incrémentés")
    @ApiResponse(responseCode = "403", description = "Accès refusé - Admin uniquement")
    public ResponseEntity<ActualiteDto> incrementerCommentaires(
            @Parameter(description = "ID de l'actualité") @PathVariable Long id) {
        
        log.info("Admin: Incrémentation des commentaires pour l'actualité {}", id);
        
        ActualiteDto actualite = actualiteService.incrementerCommentaires(id);
        
        return ResponseEntity.ok(actualite);
    }
    
    @GetMapping("/statistiques")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir les statistiques des actualités", 
               description = "Récupère les statistiques des actualités pour le dashboard admin")
    @ApiResponse(responseCode = "200", description = "Statistiques récupérées")
    @ApiResponse(responseCode = "403", description = "Accès refusé - Admin uniquement")
    public ResponseEntity<Map<String, Object>> obtenirStatistiquesActualites() {
        
        log.info("Admin: Récupération des statistiques des actualités");
        
        Map<String, Object> stats = new HashMap<>();
        
        try {
            long totalActualites = actualiteService.compterActualites();
            long actualitesVisibles = actualiteService.compterActualitesVisibles();
            long actualitesTendance = actualiteService.compterActualitesTendance();
            
            stats.put("totalActualites", totalActualites);
            stats.put("actualitesVisibles", actualitesVisibles);
            stats.put("actualitesTendance", actualitesTendance);
            stats.put("success", true);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques", e);
            stats.put("success", false);
            stats.put("message", "Erreur lors de la récupération des statistiques");
        }
        
        return ResponseEntity.ok(stats);
    }
}
