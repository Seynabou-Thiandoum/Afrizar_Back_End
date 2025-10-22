package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.CategorieCombinaisonDto;
import sn.afrizar.afrizar.dto.GenreCategorieDto;
import sn.afrizar.afrizar.dto.TypeCategorieDto;
import sn.afrizar.afrizar.service.CategorieCombinaisonService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/categories-combinaisons")
@RequiredArgsConstructor
@Tag(name = "Combinaisons de Catégories", description = "API de gestion des associations Genre + Type")
public class CategorieCombinaisonController {
    
    private final CategorieCombinaisonService combinaisonService;
    
    @PostMapping
    @Operation(summary = "Créer une nouvelle association", description = "Associe un genre à un type (ex: Homme + Boubous)")
    @ApiResponse(responseCode = "201", description = "Association créée avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "409", description = "Association déjà existante")
    public ResponseEntity<CategorieCombinaisonDto> creerAssociation(
            @Parameter(description = "ID du genre") @RequestParam Long genreId,
            @Parameter(description = "ID du type") @RequestParam Long typeId) {
        
        log.info("Création d'une association Genre {} + Type {}", genreId, typeId);
        
        try {
            CategorieCombinaisonDto association = combinaisonService.creerAssociation(genreId, typeId);
            return new ResponseEntity<>(association, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la création de l'association: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    @GetMapping
    @Operation(summary = "Lister toutes les associations actives", description = "Récupère toutes les associations Genre + Type")
    @ApiResponse(responseCode = "200", description = "Liste des associations récupérée")
    public ResponseEntity<List<CategorieCombinaisonDto>> listerAssociations() {
        List<CategorieCombinaisonDto> associations = combinaisonService.obtenirToutesLesAssociationsActives();
        return ResponseEntity.ok(associations);
    }
    
    @GetMapping("/genres/{genreId}/types")
    @Operation(summary = "Obtenir les types d'un genre", description = "Récupère tous les types associés à un genre")
    @ApiResponse(responseCode = "200", description = "Types du genre récupérés")
    public ResponseEntity<List<TypeCategorieDto>> obtenirTypesParGenre(
            @Parameter(description = "ID du genre") @PathVariable Long genreId) {
        
        List<TypeCategorieDto> types = combinaisonService.obtenirTypesParGenre(genreId);
        return ResponseEntity.ok(types);
    }
    
    @GetMapping("/types/{typeId}/genres")
    @Operation(summary = "Obtenir les genres d'un type", description = "Récupère tous les genres associés à un type")
    @ApiResponse(responseCode = "200", description = "Genres du type récupérés")
    public ResponseEntity<List<GenreCategorieDto>> obtenirGenresParType(
            @Parameter(description = "ID du type") @PathVariable Long typeId) {
        
        List<GenreCategorieDto> genres = combinaisonService.obtenirGenresParType(typeId);
        return ResponseEntity.ok(genres);
    }
    
    @GetMapping("/par-type/{type}")
    @Operation(summary = "Obtenir les associations par type de catégorie", description = "Récupère les associations par type (VETEMENTS, ACCESSOIRES)")
    @ApiResponse(responseCode = "200", description = "Associations récupérées")
    public ResponseEntity<List<CategorieCombinaisonDto>> obtenirAssociationsParType(
            @Parameter(description = "Type de catégorie") @PathVariable String type) {
        
        List<CategorieCombinaisonDto> associations = combinaisonService.obtenirAssociationsParType(type);
        return ResponseEntity.ok(associations);
    }
    
    @GetMapping("/verifier")
    @Operation(summary = "Vérifier si une association existe", description = "Vérifie si une association Genre + Type existe")
    @ApiResponse(responseCode = "200", description = "Existence vérifiée")
    public ResponseEntity<Boolean> verifierAssociation(
            @Parameter(description = "ID du genre") @RequestParam Long genreId,
            @Parameter(description = "ID du type") @RequestParam Long typeId) {
        
        boolean existe = combinaisonService.verifierAssociationExiste(genreId, typeId);
        return ResponseEntity.ok(existe);
    }
    
    @PutMapping("/{id}/ordre")
    @Operation(summary = "Mettre à jour l'ordre d'une association", description = "Change l'ordre d'affichage d'une association")
    @ApiResponse(responseCode = "200", description = "Ordre mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Association non trouvée")
    public ResponseEntity<CategorieCombinaisonDto> mettreAJourOrdre(
            @Parameter(description = "ID de l'association") @PathVariable Long id,
            @Parameter(description = "Nouvel ordre") @RequestParam Integer ordre) {
        
        try {
            CategorieCombinaisonDto association = combinaisonService.mettreAJourOrdre(id, ordre);
            return ResponseEntity.ok(association);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour de l'ordre de l'association {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/activer")
    @Operation(summary = "Activer une association", description = "Active une association désactivée")
    @ApiResponse(responseCode = "204", description = "Association activée avec succès")
    @ApiResponse(responseCode = "404", description = "Association non trouvée")
    public ResponseEntity<Void> activerAssociation(
            @Parameter(description = "ID de l'association") @PathVariable Long id) {
        
        try {
            combinaisonService.activerAssociation(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'activation de l'association {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/desactiver")
    @Operation(summary = "Désactiver une association", description = "Désactive une association")
    @ApiResponse(responseCode = "204", description = "Association désactivée avec succès")
    @ApiResponse(responseCode = "404", description = "Association non trouvée")
    public ResponseEntity<Void> desactiverAssociation(
            @Parameter(description = "ID de l'association") @PathVariable Long id) {
        
        try {
            combinaisonService.desactiverAssociation(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la désactivation de l'association {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une association", description = "Supprime définitivement une association")
    @ApiResponse(responseCode = "204", description = "Association supprimée avec succès")
    @ApiResponse(responseCode = "404", description = "Association non trouvée")
    public ResponseEntity<Void> supprimerAssociation(
            @Parameter(description = "ID de l'association") @PathVariable Long id) {
        
        try {
            combinaisonService.supprimerAssociation(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression de l'association {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/genres/{genreId}")
    @Operation(summary = "Supprimer toutes les associations d'un genre", description = "Supprime toutes les associations d'un genre")
    @ApiResponse(responseCode = "204", description = "Associations supprimées avec succès")
    public ResponseEntity<Void> supprimerAssociationsParGenre(
            @Parameter(description = "ID du genre") @PathVariable Long genreId) {
        
        try {
            combinaisonService.supprimerAssociationsParGenre(genreId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression des associations du genre {}: {}", genreId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/types/{typeId}")
    @Operation(summary = "Supprimer toutes les associations d'un type", description = "Supprime toutes les associations d'un type")
    @ApiResponse(responseCode = "204", description = "Associations supprimées avec succès")
    public ResponseEntity<Void> supprimerAssociationsParType(
            @Parameter(description = "ID du type") @PathVariable Long typeId) {
        
        try {
            combinaisonService.supprimerAssociationsParType(typeId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression des associations du type {}: {}", typeId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/en-lot")
    @Operation(summary = "Créer des associations en lot", description = "Crée plusieurs associations Genre + Type en une seule opération")
    @ApiResponse(responseCode = "201", description = "Associations créées avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<List<CategorieCombinaisonDto>> creerAssociationsEnLot(
            @Parameter(description = "IDs des genres") @RequestParam List<Long> genreIds,
            @Parameter(description = "IDs des types") @RequestParam List<Long> typeIds) {
        
        log.info("Création d'associations en lot pour {} genres et {} types", genreIds.size(), typeIds.size());
        
        try {
            List<CategorieCombinaisonDto> associations = combinaisonService.creerAssociationsEnLot(genreIds, typeIds);
            return new ResponseEntity<>(associations, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la création des associations en lot: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}

