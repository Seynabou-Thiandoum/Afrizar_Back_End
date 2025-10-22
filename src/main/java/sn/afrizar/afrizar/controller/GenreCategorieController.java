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
import sn.afrizar.afrizar.dto.GenreCategorieDto;
import sn.afrizar.afrizar.service.GenreCategorieService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/genres-categories")
@RequiredArgsConstructor
@Tag(name = "Genres de Catégories", description = "API de gestion des genres de catégories (Homme, Femme, Enfant)")
public class GenreCategorieController {
    
    private final GenreCategorieService genreCategorieService;
    
    @PostMapping
    @Operation(summary = "Créer un nouveau genre", description = "Crée un nouveau genre de catégorie (ex: Homme, Femme, Enfant)")
    @ApiResponse(responseCode = "201", description = "Genre créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "409", description = "Nom de genre déjà utilisé")
    public ResponseEntity<GenreCategorieDto> creerGenre(@Valid @RequestBody GenreCategorieDto genreDto) {
        log.info("Création d'un nouveau genre: {}", genreDto.getNom());
        
        try {
            GenreCategorieDto genreCree = genreCategorieService.creerGenre(genreDto);
            return new ResponseEntity<>(genreCree, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la création du genre: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un genre par ID", description = "Récupère les détails d'un genre spécifique")
    @ApiResponse(responseCode = "200", description = "Genre trouvé")
    @ApiResponse(responseCode = "404", description = "Genre non trouvé")
    public ResponseEntity<GenreCategorieDto> obtenirGenre(
            @Parameter(description = "ID du genre") @PathVariable Long id) {
        
        GenreCategorieDto genre = genreCategorieService.obtenirGenreParId(id);
        return genre != null ? ResponseEntity.ok(genre) : ResponseEntity.notFound().build();
    }
    
    @GetMapping
    @Operation(summary = "Lister tous les genres actifs", description = "Récupère la liste de tous les genres actifs")
    @ApiResponse(responseCode = "200", description = "Liste des genres récupérée")
    public ResponseEntity<List<GenreCategorieDto>> listerGenres() {
        List<GenreCategorieDto> genres = genreCategorieService.obtenirTousLesGenresActifs();
        return ResponseEntity.ok(genres);
    }
    
    @GetMapping("/par-type/{type}")
    @Operation(summary = "Obtenir les genres par catégorie", description = "Récupère les genres par type de catégorie (VETEMENTS, ACCESSOIRES)")
    @ApiResponse(responseCode = "200", description = "Genres récupérés")
    public ResponseEntity<List<GenreCategorieDto>> obtenirGenresParType(
            @Parameter(description = "Type de catégorie") @PathVariable String type) {
        
        List<GenreCategorieDto> genres = genreCategorieService.obtenirGenresParType(type);
        return ResponseEntity.ok(genres);
    }
    
    @GetMapping("/recherche")
    @Operation(summary = "Rechercher des genres", description = "Recherche des genres par nom")
    @ApiResponse(responseCode = "200", description = "Résultats de recherche")
    public ResponseEntity<List<GenreCategorieDto>> rechercherGenres(
            @Parameter(description = "Terme de recherche") @RequestParam String nom) {
        
        List<GenreCategorieDto> genres = genreCategorieService.rechercherGenresParNom(nom);
        return ResponseEntity.ok(genres);
    }
    
    @GetMapping("/populaires")
    @Operation(summary = "Obtenir les genres les plus utilisés", description = "Récupère les genres triés par nombre d'utilisations")
    @ApiResponse(responseCode = "200", description = "Genres populaires récupérés")
    public ResponseEntity<List<GenreCategorieDto>> obtenirGenresPopulaires() {
        List<GenreCategorieDto> genres = genreCategorieService.obtenirGenresParUsage();
        return ResponseEntity.ok(genres);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un genre", description = "Met à jour les informations d'un genre existant")
    @ApiResponse(responseCode = "200", description = "Genre mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Genre non trouvé")
    @ApiResponse(responseCode = "409", description = "Nom de genre déjà utilisé")
    public ResponseEntity<GenreCategorieDto> mettreAJourGenre(
            @Parameter(description = "ID du genre") @PathVariable Long id,
            @Valid @RequestBody GenreCategorieDto genreDto) {
        
        try {
            GenreCategorieDto genreMiseAJour = genreCategorieService.mettreAJourGenre(id, genreDto);
            return ResponseEntity.ok(genreMiseAJour);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour du genre {}: {}", id, e.getMessage());
            if (e.getMessage().contains("nom existe déjà")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/activer")
    @Operation(summary = "Activer un genre", description = "Active un genre désactivé")
    @ApiResponse(responseCode = "204", description = "Genre activé avec succès")
    @ApiResponse(responseCode = "404", description = "Genre non trouvé")
    public ResponseEntity<Void> activerGenre(
            @Parameter(description = "ID du genre") @PathVariable Long id) {
        
        try {
            genreCategorieService.activerGenre(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'activation du genre {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/desactiver")
    @Operation(summary = "Désactiver un genre", description = "Désactive un genre")
    @ApiResponse(responseCode = "204", description = "Genre désactivé avec succès")
    @ApiResponse(responseCode = "404", description = "Genre non trouvé")
    public ResponseEntity<Void> desactiverGenre(
            @Parameter(description = "ID du genre") @PathVariable Long id) {
        
        try {
            genreCategorieService.desactiverGenre(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la désactivation du genre {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un genre", description = "Supprime définitivement un genre")
    @ApiResponse(responseCode = "204", description = "Genre supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Genre non trouvé")
    @ApiResponse(responseCode = "409", description = "Genre utilisé dans des associations")
    public ResponseEntity<Void> supprimerGenre(
            @Parameter(description = "ID du genre") @PathVariable Long id) {
        
        try {
            genreCategorieService.supprimerGenre(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression du genre {}: {}", id, e.getMessage());
            if (e.getMessage().contains("utilisé")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/statistiques/types")
    @Operation(summary = "Obtenir le nombre de types associés à ce genre", description = "Compte le nombre de types associés à ce genre")
    @ApiResponse(responseCode = "200", description = "Nombre de types récupéré")
    public ResponseEntity<Long> getNombreTypesParGenre(
            @Parameter(description = "ID du genre") @PathVariable Long id) {
        
        try {
            Long nombreTypes = genreCategorieService.getNombreTypesParGenre(id);
            return ResponseEntity.ok(nombreTypes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/nom/{nom}/disponible")
    @Operation(summary = "Vérifier la disponibilité d'un nom", description = "Vérifie si un nom de genre est disponible")
    @ApiResponse(responseCode = "200", description = "Disponibilité vérifiée")
    public ResponseEntity<Boolean> verifierNomDisponible(
            @Parameter(description = "Nom de genre à vérifier") @PathVariable String nom) {
        
        boolean disponible = genreCategorieService.verifierNomDisponible(nom);
        return ResponseEntity.ok(disponible);
    }
}

