package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.ActualiteDto;
import sn.afrizar.afrizar.service.ActualiteService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/public/actualites")
@RequiredArgsConstructor
@Tag(name = "Actualités Publiques", description = "API publique pour les actualités mode")
public class ActualiteController {
    
    private final ActualiteService actualiteService;
    
    @GetMapping
    @Operation(summary = "Obtenir les actualités publiques", 
               description = "Récupère les actualités visibles pour la page publique")
    @ApiResponse(responseCode = "200", description = "Actualités récupérées")
    public ResponseEntity<Page<ActualiteDto>> obtenirActualitesPubliques(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size) {
        
        log.info("Récupération des actualités publiques");
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ActualiteDto> actualites = actualiteService.obtenirActualitesPubliques(pageable);
        
        return ResponseEntity.ok(actualites);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une actualité par ID", 
               description = "Récupère une actualité spécifique")
    @ApiResponse(responseCode = "200", description = "Actualité trouvée")
    @ApiResponse(responseCode = "404", description = "Actualité non trouvée")
    public ResponseEntity<ActualiteDto> obtenirActualite(
            @Parameter(description = "ID de l'actualité") @PathVariable Long id) {
        
        log.info("Récupération de l'actualité avec ID: {}", id);
        
        return actualiteService.obtenirActualiteParId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/tendances")
    @Operation(summary = "Obtenir les actualités tendance", 
               description = "Récupère les actualités marquées comme tendance")
    @ApiResponse(responseCode = "200", description = "Actualités tendance récupérées")
    public ResponseEntity<List<ActualiteDto>> obtenirActualitesTendance() {
        
        log.info("Récupération des actualités tendance");
        
        List<ActualiteDto> actualites = actualiteService.obtenirActualitesTendance();
        
        return ResponseEntity.ok(actualites);
    }
    
    @GetMapping("/recentes")
    @Operation(summary = "Obtenir les actualités récentes", 
               description = "Récupère les actualités les plus récentes")
    @ApiResponse(responseCode = "200", description = "Actualités récentes récupérées")
    public ResponseEntity<List<ActualiteDto>> obtenirActualitesRecentes(
            @Parameter(description = "Nombre d'actualités") @RequestParam(defaultValue = "5") int limit) {
        
        log.info("Récupération des {} actualités récentes", limit);
        
        List<ActualiteDto> actualites = actualiteService.obtenirActualitesRecentes(limit);
        
        return ResponseEntity.ok(actualites);
    }
    
    @GetMapping("/categorie/{categorie}")
    @Operation(summary = "Obtenir les actualités par catégorie", 
               description = "Récupère les actualités d'une catégorie spécifique")
    @ApiResponse(responseCode = "200", description = "Actualités par catégorie récupérées")
    public ResponseEntity<Page<ActualiteDto>> obtenirActualitesParCategorie(
            @Parameter(description = "Catégorie") @PathVariable String categorie,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size) {
        
        log.info("Récupération des actualités pour la catégorie: {}", categorie);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ActualiteDto> actualites = actualiteService.obtenirActualitesParCategorie(categorie, pageable);
        
        return ResponseEntity.ok(actualites);
    }
    
    @GetMapping("/recherche")
    @Operation(summary = "Rechercher des actualités", 
               description = "Recherche des actualités par titre ou contenu")
    @ApiResponse(responseCode = "200", description = "Résultats de recherche récupérés")
    public ResponseEntity<Page<ActualiteDto>> rechercherActualites(
            @Parameter(description = "Terme de recherche") @RequestParam String searchTerm,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size) {
        
        log.info("Recherche d'actualités avec le terme: {}", searchTerm);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ActualiteDto> actualites = actualiteService.rechercherActualites(searchTerm, pageable);
        
        return ResponseEntity.ok(actualites);
    }
    
    @GetMapping("/plus-likees")
    @Operation(summary = "Obtenir les actualités les plus likées", 
               description = "Récupère les actualités avec le plus de likes")
    @ApiResponse(responseCode = "200", description = "Actualités les plus likées récupérées")
    public ResponseEntity<Page<ActualiteDto>> obtenirActualitesPlusLikees(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size) {
        
        log.info("Récupération des actualités les plus likées");
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ActualiteDto> actualites = actualiteService.obtenirActualitesPlusLikees(pageable);
        
        return ResponseEntity.ok(actualites);
    }
}
