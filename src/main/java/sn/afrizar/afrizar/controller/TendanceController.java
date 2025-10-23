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
import sn.afrizar.afrizar.dto.ProduitDto;
import sn.afrizar.afrizar.service.ProduitService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/public/tendances")
@RequiredArgsConstructor
@Tag(name = "Tendances Publiques", description = "API publique pour les tendances et produits à la mode")
public class TendanceController {
    
    private final ProduitService produitService;
    
    @GetMapping("/produits-a-la-mode")
    @Operation(summary = "Obtenir les produits à la mode", 
               description = "Récupère les produits les plus populaires et tendance pour la page publique")
    @ApiResponse(responseCode = "200", description = "Produits à la mode récupérés")
    public ResponseEntity<Map<String, Object>> obtenirProduitsALaMode(
            @Parameter(description = "Nombre de produits par catégorie") @RequestParam(defaultValue = "6") int limit) {
        
        log.info("Récupération des produits à la mode pour la page publique");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Produits les plus vus (tendance)
            Pageable pageablePlusVus = PageRequest.of(0, limit);
            Page<ProduitDto> produitsPlusVus = produitService.obtenirProduitsPlusVus(pageablePlusVus);
            
            // Produits les mieux notés
            Pageable pageableMieuxNotes = PageRequest.of(0, limit);
            Page<ProduitDto> produitsMieuxNotes = produitService.obtenirProduitsMieuxNotes(pageableMieuxNotes);
            
            // Produits en stock avec promotion
            List<ProduitDto> produitsPromo = produitService.obtenirProduitsAvecPromotion();
            
            // Produits récents
            List<ProduitDto> produitsRecents = produitService.obtenirProduitsRecents(limit);
            
            result.put("produitsPlusVus", produitsPlusVus.getContent());
            result.put("produitsMieuxNotes", produitsMieuxNotes.getContent());
            result.put("produitsPromo", produitsPromo);
            result.put("produitsRecents", produitsRecents);
            result.put("success", true);
            
            log.info("Produits à la mode récupérés: {} plus vus, {} mieux notés, {} promo, {} récents", 
                    produitsPlusVus.getContent().size(), 
                    produitsMieuxNotes.getContent().size(), 
                    produitsPromo.size(), 
                    produitsRecents.size());
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des produits à la mode", e);
            result.put("success", false);
            result.put("message", "Erreur lors de la récupération des données");
        }
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/produits-tendance")
    @Operation(summary = "Obtenir les produits tendance", 
               description = "Récupère les produits marqués comme tendance par l'admin")
    @ApiResponse(responseCode = "200", description = "Produits tendance récupérés")
    public ResponseEntity<List<ProduitDto>> obtenirProduitsTendance(
            @Parameter(description = "Nombre de produits") @RequestParam(defaultValue = "12") int limit) {
        
        log.info("Récupération des produits tendance");
        
        try {
            List<ProduitDto> produitsTendance = produitService.obtenirProduitsTendance(limit);
            return ResponseEntity.ok(produitsTendance);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des produits tendance", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/statistiques-tendances")
    @Operation(summary = "Obtenir les statistiques des tendances", 
               description = "Récupère les statistiques générales des tendances")
    @ApiResponse(responseCode = "200", description = "Statistiques récupérées")
    public ResponseEntity<Map<String, Object>> obtenirStatistiquesTendances() {
        
        log.info("Récupération des statistiques des tendances");
        
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Statistiques générales
            long totalProduits = produitService.compterTotalProduits();
            long produitsEnStock = produitService.compterProduitsEnStock();
            long produitsPromo = produitService.compterProduitsEnPromotion();
            long produitsTendance = produitService.compterProduitsTendance();
            
            stats.put("totalProduits", totalProduits);
            stats.put("produitsEnStock", produitsEnStock);
            stats.put("produitsPromo", produitsPromo);
            stats.put("produitsTendance", produitsTendance);
            stats.put("success", true);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques", e);
            stats.put("success", false);
            stats.put("message", "Erreur lors de la récupération des statistiques");
        }
        
        return ResponseEntity.ok(stats);
    }
}
