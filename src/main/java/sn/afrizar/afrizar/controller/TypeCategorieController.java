package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.TypeCategorieDto;
import sn.afrizar.afrizar.service.TypeCategorieService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/types-categories")
@RequiredArgsConstructor
@Tag(name = "Types de Catégories", description = "API de gestion des types de catégories (Boubous, Costumes, etc.)")
public class TypeCategorieController {
    
    private final TypeCategorieService typeCategorieService;
    
    @PostMapping
    @Operation(summary = "Créer un nouveau type", description = "Crée un nouveau type de catégorie (ex: Boubous, Costumes)")
    @ApiResponse(responseCode = "201", description = "Type créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "409", description = "Nom de type déjà utilisé")
    public ResponseEntity<TypeCategorieDto> creerType(@Valid @RequestBody TypeCategorieDto typeDto) {
        log.info("Création d'un nouveau type: {}", typeDto.getNom());
        
        try {
            TypeCategorieDto typeCree = typeCategorieService.creerType(typeDto);
            return new ResponseEntity<>(typeCree, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la création du type: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un type par ID", description = "Récupère les détails d'un type spécifique")
    @ApiResponse(responseCode = "200", description = "Type trouvé")
    @ApiResponse(responseCode = "404", description = "Type non trouvé")
    public ResponseEntity<TypeCategorieDto> obtenirType(
            @Parameter(description = "ID du type") @PathVariable Long id) {
        
        TypeCategorieDto type = typeCategorieService.obtenirTypeParId(id);
        return type != null ? ResponseEntity.ok(type) : ResponseEntity.notFound().build();
    }
    
    @GetMapping
    @Operation(summary = "Lister tous les types actifs", description = "Récupère la liste de tous les types actifs")
    @ApiResponse(responseCode = "200", description = "Liste des types récupérée")
    public ResponseEntity<List<TypeCategorieDto>> listerTypes() {
        List<TypeCategorieDto> types = typeCategorieService.obtenirTousLesTypesActifs();
        return ResponseEntity.ok(types);
    }
    
    @GetMapping("/par-type/{type}")
    @Operation(summary = "Obtenir les types par catégorie", description = "Récupère les types par type de catégorie (VETEMENTS, ACCESSOIRES)")
    @ApiResponse(responseCode = "200", description = "Types récupérés")
    public ResponseEntity<List<TypeCategorieDto>> obtenirTypesParType(
            @Parameter(description = "Type de catégorie") @PathVariable String type) {
        
        List<TypeCategorieDto> types = typeCategorieService.obtenirTypesParType(type);
        return ResponseEntity.ok(types);
    }
    
    @GetMapping("/recherche")
    @Operation(summary = "Rechercher des types", description = "Recherche des types par nom")
    @ApiResponse(responseCode = "200", description = "Résultats de recherche")
    public ResponseEntity<List<TypeCategorieDto>> rechercherTypes(
            @Parameter(description = "Terme de recherche") @RequestParam String nom) {
        
        List<TypeCategorieDto> types = typeCategorieService.rechercherTypesParNom(nom);
        return ResponseEntity.ok(types);
    }
    
    @GetMapping("/populaires")
    @Operation(summary = "Obtenir les types les plus utilisés", description = "Récupère les types triés par nombre d'utilisations")
    @ApiResponse(responseCode = "200", description = "Types populaires récupérés")
    public ResponseEntity<List<TypeCategorieDto>> obtenirTypesPopulaires() {
        List<TypeCategorieDto> types = typeCategorieService.obtenirTypesParUsage();
        return ResponseEntity.ok(types);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un type", description = "Met à jour les informations d'un type existant")
    @ApiResponse(responseCode = "200", description = "Type mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Type non trouvé")
    @ApiResponse(responseCode = "409", description = "Nom de type déjà utilisé")
    public ResponseEntity<TypeCategorieDto> mettreAJourType(
            @Parameter(description = "ID du type") @PathVariable Long id,
            @Valid @RequestBody TypeCategorieDto typeDto) {
        
        try {
            TypeCategorieDto typeMiseAJour = typeCategorieService.mettreAJourType(id, typeDto);
            return ResponseEntity.ok(typeMiseAJour);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour du type {}: {}", id, e.getMessage());
            if (e.getMessage().contains("nom existe déjà")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/activer")
    @Operation(summary = "Activer un type", description = "Active un type désactivé")
    @ApiResponse(responseCode = "204", description = "Type activé avec succès")
    @ApiResponse(responseCode = "404", description = "Type non trouvé")
    public ResponseEntity<Void> activerType(
            @Parameter(description = "ID du type") @PathVariable Long id) {
        
        try {
            typeCategorieService.activerType(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'activation du type {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/desactiver")
    @Operation(summary = "Désactiver un type", description = "Désactive un type")
    @ApiResponse(responseCode = "204", description = "Type désactivé avec succès")
    @ApiResponse(responseCode = "404", description = "Type non trouvé")
    public ResponseEntity<Void> desactiverType(
            @Parameter(description = "ID du type") @PathVariable Long id) {
        
        try {
            typeCategorieService.desactiverType(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la désactivation du type {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un type", description = "Supprime définitivement un type")
    @ApiResponse(responseCode = "204", description = "Type supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Type non trouvé")
    @ApiResponse(responseCode = "409", description = "Type utilisé dans des associations")
    public ResponseEntity<Void> supprimerType(
            @Parameter(description = "ID du type") @PathVariable Long id) {
        
        try {
            typeCategorieService.supprimerType(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression du type {}: {}", id, e.getMessage());
            if (e.getMessage().contains("utilisé")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/statistiques/genres")
    @Operation(summary = "Obtenir le nombre de genres utilisant ce type", description = "Compte le nombre de genres qui utilisent ce type")
    @ApiResponse(responseCode = "200", description = "Nombre de genres récupéré")
    public ResponseEntity<Long> getNombreGenresParType(
            @Parameter(description = "ID du type") @PathVariable Long id) {
        
        try {
            Long nombreGenres = typeCategorieService.getNombreGenresParType(id);
            return ResponseEntity.ok(nombreGenres);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/nom/{nom}/disponible")
    @Operation(summary = "Vérifier la disponibilité d'un nom", description = "Vérifie si un nom de type est disponible")
    @ApiResponse(responseCode = "200", description = "Disponibilité vérifiée")
    public ResponseEntity<Boolean> verifierNomDisponible(
            @Parameter(description = "Nom de type à vérifier") @PathVariable String nom) {
        
        boolean disponible = typeCategorieService.verifierNomDisponible(nom);
        return ResponseEntity.ok(disponible);
    }
}

