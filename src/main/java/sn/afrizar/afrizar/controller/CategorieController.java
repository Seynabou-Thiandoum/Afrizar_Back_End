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
import sn.afrizar.afrizar.dto.CategorieDto;
import sn.afrizar.afrizar.service.CategorieService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Catégories", description = "API de gestion des catégories de produits")
public class CategorieController {
    
    private final CategorieService categorieService;
    
    @PostMapping
    @Operation(summary = "Créer une nouvelle catégorie", description = "Crée une nouvelle catégorie de produits")
    @ApiResponse(responseCode = "201", description = "Catégorie créée avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "409", description = "Nom de catégorie déjà utilisé")
    public ResponseEntity<CategorieDto> creerCategorie(@Valid @RequestBody CategorieDto categorieDto) {
        log.info("Création d'une nouvelle catégorie: {}", categorieDto.getNom());
        
        try {
            CategorieDto categorieCree = categorieService.creerCategorie(categorieDto);
            return new ResponseEntity<>(categorieCree, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la création de la catégorie: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une catégorie par ID", description = "Récupère les détails d'une catégorie spécifique")
    @ApiResponse(responseCode = "200", description = "Catégorie trouvée")
    @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    public ResponseEntity<CategorieDto> obtenirCategorie(
            @Parameter(description = "ID de la catégorie") @PathVariable Long id) {
        
        return categorieService.obtenirCategorieParId(id)
                .map(categorie -> ResponseEntity.ok(categorie))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "Lister toutes les catégories actives", description = "Récupère la liste de toutes les catégories actives")
    @ApiResponse(responseCode = "200", description = "Liste des catégories récupérée")
    public ResponseEntity<List<CategorieDto>> listerCategories() {
        List<CategorieDto> categories = categorieService.obtenirToutesLesCategoriesActives();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/racines")
    @Operation(summary = "Obtenir les catégories racines", description = "Récupère toutes les catégories de niveau supérieur (sans parent)")
    @ApiResponse(responseCode = "200", description = "Catégories racines récupérées")
    public ResponseEntity<List<CategorieDto>> obtenirCategoriesRacines() {
        List<CategorieDto> categories = categorieService.obtenirCategoriesRacines();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/{id}/sous-categories")
    @Operation(summary = "Obtenir les sous-catégories", description = "Récupère toutes les sous-catégories d'une catégorie parent")
    @ApiResponse(responseCode = "200", description = "Sous-catégories récupérées")
    public ResponseEntity<List<CategorieDto>> obtenirSousCategories(
            @Parameter(description = "ID de la catégorie parent") @PathVariable Long id) {
        
        List<CategorieDto> sousCategories = categorieService.obtenirSousCategories(id);
        return ResponseEntity.ok(sousCategories);
    }
    
    @GetMapping("/recherche")
    @Operation(summary = "Rechercher des catégories", description = "Recherche des catégories par nom")
    @ApiResponse(responseCode = "200", description = "Résultats de recherche")
    public ResponseEntity<List<CategorieDto>> rechercherCategories(
            @Parameter(description = "Terme de recherche") @RequestParam String nom) {
        
        List<CategorieDto> categories = categorieService.rechercherCategoriesParNom(nom);
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/populaires")
    @Operation(summary = "Obtenir les catégories les plus populaires", description = "Récupère les catégories triées par nombre de produits")
    @ApiResponse(responseCode = "200", description = "Catégories populaires récupérées")
    public ResponseEntity<List<CategorieDto>> obtenirCategoriesPopulaires() {
        List<CategorieDto> categories = categorieService.obtenirCategoriesParPopularite();
        return ResponseEntity.ok(categories);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une catégorie", description = "Met à jour les informations d'une catégorie existante")
    @ApiResponse(responseCode = "200", description = "Catégorie mise à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    @ApiResponse(responseCode = "409", description = "Nom de catégorie déjà utilisé")
    public ResponseEntity<CategorieDto> mettreAJourCategorie(
            @Parameter(description = "ID de la catégorie") @PathVariable Long id,
            @Valid @RequestBody CategorieDto categorieDto) {
        
        try {
            CategorieDto categorieMiseAJour = categorieService.mettreAJourCategorie(id, categorieDto);
            return ResponseEntity.ok(categorieMiseAJour);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour de la catégorie {}: {}", id, e.getMessage());
            if (e.getMessage().contains("nom existe déjà")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/activer")
    @Operation(summary = "Activer une catégorie", description = "Active une catégorie désactivée")
    @ApiResponse(responseCode = "204", description = "Catégorie activée avec succès")
    @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    public ResponseEntity<Void> activerCategorie(
            @Parameter(description = "ID de la catégorie") @PathVariable Long id) {
        
        try {
            categorieService.activerCategorie(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'activation de la catégorie {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/desactiver")
    @Operation(summary = "Désactiver une catégorie", description = "Désactive une catégorie")
    @ApiResponse(responseCode = "204", description = "Catégorie désactivée avec succès")
    @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    public ResponseEntity<Void> desactiverCategorie(
            @Parameter(description = "ID de la catégorie") @PathVariable Long id) {
        
        try {
            categorieService.desactiverCategorie(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la désactivation de la catégorie {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une catégorie", description = "Supprime définitivement une catégorie")
    @ApiResponse(responseCode = "204", description = "Catégorie supprimée avec succès")
    @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    @ApiResponse(responseCode = "409", description = "Catégorie contient des produits ou des sous-catégories")
    public ResponseEntity<Void> supprimerCategorie(
            @Parameter(description = "ID de la catégorie") @PathVariable Long id) {
        
        try {
            categorieService.supprimerCategorie(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression de la catégorie {}: {}", id, e.getMessage());
            if (e.getMessage().contains("contient")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/statistiques/produits")
    @Operation(summary = "Obtenir le nombre de produits dans une catégorie", description = "Compte le nombre de produits actifs dans une catégorie")
    @ApiResponse(responseCode = "200", description = "Nombre de produits récupéré")
    public ResponseEntity<Long> getNombreProduitsCategorie(
            @Parameter(description = "ID de la catégorie") @PathVariable Long id) {
        
        try {
            Long nombreProduits = categorieService.getNombreProduitsParCategorie(id);
            return ResponseEntity.ok(nombreProduits);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/nom/{nom}/disponible")
    @Operation(summary = "Vérifier la disponibilité d'un nom", description = "Vérifie si un nom de catégorie est disponible")
    @ApiResponse(responseCode = "200", description = "Disponibilité vérifiée")
    public ResponseEntity<Boolean> verifierNomDisponible(
            @Parameter(description = "Nom de catégorie à vérifier") @PathVariable String nom) {
        
        boolean disponible = categorieService.verifierNomDisponible(nom);
        return ResponseEntity.ok(disponible);
    }
    
    @GetMapping("/slug/{slug}")
    @Operation(summary = "Obtenir une catégorie par slug", description = "Récupère une catégorie par son slug")
    @ApiResponse(responseCode = "200", description = "Catégorie trouvée")
    @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    public ResponseEntity<CategorieDto> obtenirCategorieParSlug(
            @Parameter(description = "Slug de la catégorie") @PathVariable String slug) {
        
        return categorieService.obtenirCategorieParSlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/hierarchie")
    @Operation(summary = "Obtenir la hiérarchie complète", description = "Récupère toutes les catégories avec leurs sous-catégories")
    @ApiResponse(responseCode = "200", description = "Hiérarchie récupérée")
    public ResponseEntity<List<CategorieDto>> obtenirHierarchieComplete() {
        List<CategorieDto> hierarchie = categorieService.obtenirHierarchieComplete();
        return ResponseEntity.ok(hierarchie);
    }
}

