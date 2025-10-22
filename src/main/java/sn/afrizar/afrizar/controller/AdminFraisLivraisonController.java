package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.FraisLivraisonDto;
import sn.afrizar.afrizar.model.FraisLivraison;
import sn.afrizar.afrizar.service.FraisLivraisonService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/frais-livraison")
@RequiredArgsConstructor
@Tag(name = "Administration - Frais de Livraison", description = "API de gestion des frais de livraison (Admin uniquement)")
public class AdminFraisLivraisonController {
    
    private final FraisLivraisonService fraisLivraisonService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un nouveau frais de livraison", description = "Crée un nouveau frais de livraison")
    @ApiResponse(responseCode = "201", description = "Frais de livraison créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<FraisLivraisonDto> creerFraisLivraison(@Valid @RequestBody FraisLivraisonDto fraisLivraisonDto) {
        log.info("Création d'un nouveau frais de livraison: {}", fraisLivraisonDto.getNom());
        
        try {
            FraisLivraisonDto fraisLivraisonCree = fraisLivraisonService.creerFraisLivraison(fraisLivraisonDto);
            return new ResponseEntity<>(fraisLivraisonCree, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la création du frais de livraison: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir tous les frais de livraison", description = "Récupère tous les frais de livraison avec pagination")
    @ApiResponse(responseCode = "200", description = "Liste des frais de livraison récupérée")
    public ResponseEntity<Page<FraisLivraisonDto>> obtenirTousLesFraisLivraison(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size) {
        
        log.info("Récupération de tous les frais de livraison - Page: {}, Taille: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("type").ascending());
        Page<FraisLivraisonDto> fraisLivraisonPage = fraisLivraisonService.obtenirTousLesFraisLivraison(pageable);
        
        return ResponseEntity.ok(fraisLivraisonPage);
    }
    
    @GetMapping("/actifs")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir les frais de livraison actifs", description = "Récupère tous les frais de livraison actifs")
    @ApiResponse(responseCode = "200", description = "Liste des frais de livraison actifs")
    public ResponseEntity<List<FraisLivraisonDto>> obtenirFraisLivraisonActifs() {
        log.info("Récupération des frais de livraison actifs");
        
        List<FraisLivraisonDto> fraisLivraisonList = fraisLivraisonService.obtenirFraisLivraisonActifs();
        return ResponseEntity.ok(fraisLivraisonList);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir un frais de livraison par ID", description = "Récupère les détails d'un frais de livraison spécifique")
    @ApiResponse(responseCode = "200", description = "Frais de livraison trouvé")
    @ApiResponse(responseCode = "404", description = "Frais de livraison non trouvé")
    public ResponseEntity<FraisLivraisonDto> obtenirFraisLivraison(
            @Parameter(description = "ID du frais de livraison") @PathVariable Long id) {
        
        return fraisLivraisonService.obtenirFraisLivraisonParId(id)
                .map(fraisLivraison -> ResponseEntity.ok(fraisLivraison))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour un frais de livraison", description = "Met à jour un frais de livraison existant")
    @ApiResponse(responseCode = "200", description = "Frais de livraison mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Frais de livraison non trouvé")
    public ResponseEntity<FraisLivraisonDto> mettreAJourFraisLivraison(
            @Parameter(description = "ID du frais de livraison") @PathVariable Long id,
            @Valid @RequestBody FraisLivraisonDto fraisLivraisonDto) {
        
        log.info("Mise à jour du frais de livraison ID: {}", id);
        
        try {
            FraisLivraisonDto fraisLivraisonModifie = fraisLivraisonService.mettreAJourFraisLivraison(id, fraisLivraisonDto);
            return ResponseEntity.ok(fraisLivraisonModifie);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour du frais de livraison {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un frais de livraison", description = "Supprime un frais de livraison")
    @ApiResponse(responseCode = "204", description = "Frais de livraison supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Frais de livraison non trouvé")
    public ResponseEntity<Void> supprimerFraisLivraison(
            @Parameter(description = "ID du frais de livraison") @PathVariable Long id) {
        
        log.info("Suppression du frais de livraison ID: {}", id);
        
        try {
            fraisLivraisonService.supprimerFraisLivraison(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression du frais de livraison {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/activer")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activer un frais de livraison", description = "Active un frais de livraison désactivé")
    @ApiResponse(responseCode = "200", description = "Frais de livraison activé avec succès")
    @ApiResponse(responseCode = "404", description = "Frais de livraison non trouvé")
    public ResponseEntity<FraisLivraisonDto> activerFraisLivraison(
            @Parameter(description = "ID du frais de livraison") @PathVariable Long id) {
        
        log.info("Activation du frais de livraison ID: {}", id);
        
        try {
            FraisLivraisonDto fraisLivraisonActive = fraisLivraisonService.activerFraisLivraison(id);
            return ResponseEntity.ok(fraisLivraisonActive);
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'activation du frais de livraison {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/desactiver")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Désactiver un frais de livraison", description = "Désactive un frais de livraison actif")
    @ApiResponse(responseCode = "200", description = "Frais de livraison désactivé avec succès")
    @ApiResponse(responseCode = "404", description = "Frais de livraison non trouvé")
    public ResponseEntity<FraisLivraisonDto> desactiverFraisLivraison(
            @Parameter(description = "ID du frais de livraison") @PathVariable Long id) {
        
        log.info("Désactivation du frais de livraison ID: {}", id);
        
        try {
            FraisLivraisonDto fraisLivraisonDesactive = fraisLivraisonService.desactiverFraisLivraison(id);
            return ResponseEntity.ok(fraisLivraisonDesactive);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la désactivation du frais de livraison {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir les frais de livraison par type", description = "Récupère les frais de livraison d'un type spécifique")
    @ApiResponse(responseCode = "200", description = "Liste des frais de livraison du type spécifié")
    public ResponseEntity<List<FraisLivraisonDto>> obtenirFraisLivraisonParType(
            @Parameter(description = "Type de livraison") @PathVariable FraisLivraison.TypeLivraison type) {
        
        log.info("Récupération des frais de livraison pour le type: {}", type);
        
        List<FraisLivraisonDto> fraisLivraisonList = fraisLivraisonService.obtenirFraisLivraisonParType(type);
        return ResponseEntity.ok(fraisLivraisonList);
    }
    
    @GetMapping("/applicables")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir les frais de livraison applicables", description = "Récupère les frais de livraison applicables selon le poids et la zone")
    @ApiResponse(responseCode = "200", description = "Liste des frais de livraison applicables")
    public ResponseEntity<List<FraisLivraisonDto>> obtenirFraisLivraisonApplicables(
            @Parameter(description = "Poids en kg") @RequestParam(required = false) Double poids,
            @Parameter(description = "Zone de livraison") @RequestParam(required = false) String zone) {
        
        log.info("Récupération des frais de livraison applicables - Poids: {} kg, Zone: {}", poids, zone);
        
        List<FraisLivraisonDto> fraisLivraisonList = fraisLivraisonService.obtenirFraisLivraisonApplicables(poids, zone);
        return ResponseEntity.ok(fraisLivraisonList);
    }
}
