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
import sn.afrizar.afrizar.dto.ProduitDto;
import sn.afrizar.afrizar.service.ProduitService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/tendances")
@RequiredArgsConstructor
@Tag(name = "Admin Tendances", description = "API d'administration pour la gestion des tendances")
@SecurityRequirement(name = "bearer-jwt")
public class AdminTendanceController {
    
    private final ProduitService produitService;
    
    @GetMapping("/produits-a-la-mode")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir tous les produits à la mode pour l'admin", 
               description = "Récupère tous les produits avec leurs statuts de tendance pour la gestion admin")
    @ApiResponse(responseCode = "200", description = "Produits à la mode récupérés")
    @ApiResponse(responseCode = "403", description = "Accès refusé - Admin uniquement")
    public ResponseEntity<Map<String, Object>> obtenirProduitsALaModeAdmin(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size) {
        
        log.info("Admin: Récupération des produits à la mode pour gestion");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
            
            // Produits les plus vus
            Page<ProduitDto> produitsPlusVus = produitService.obtenirProduitsPlusVus(pageable);
            
            // Produits les mieux notés
            Page<ProduitDto> produitsMieuxNotes = produitService.obtenirProduitsMieuxNotes(pageable);
            
            // Produits en promotion
            List<ProduitDto> produitsPromo = produitService.obtenirProduitsAvecPromotion();
            
            // Produits marqués comme tendance
            List<ProduitDto> produitsTendance = produitService.obtenirProduitsTendance(size);
            
            result.put("produitsPlusVus", produitsPlusVus);
            result.put("produitsMieuxNotes", produitsMieuxNotes);
            result.put("produitsPromo", produitsPromo);
            result.put("produitsTendance", produitsTendance);
            result.put("success", true);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des produits à la mode pour l'admin", e);
            result.put("success", false);
            result.put("message", "Erreur lors de la récupération des données");
        }
        
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/produits/{id}/marquer-tendance")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Marquer un produit comme tendance", 
               description = "Permet à l'admin de marquer un produit comme tendance")
    @ApiResponse(responseCode = "200", description = "Produit marqué comme tendance")
    @ApiResponse(responseCode = "403", description = "Accès refusé - Admin uniquement")
    @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    public ResponseEntity<Map<String, Object>> marquerProduitTendance(
            @Parameter(description = "ID du produit") @PathVariable Long id,
            @Parameter(description = "Marquer comme tendance") @RequestParam boolean tendance) {
        
        log.info("Admin: Marquage du produit {} comme tendance: {}", id, tendance);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = produitService.marquerProduitTendance(id, tendance);
            
            if (success) {
                result.put("success", true);
                result.put("message", tendance ? "Produit marqué comme tendance" : "Produit retiré des tendances");
            } else {
                result.put("success", false);
                result.put("message", "Produit non trouvé");
            }
            
        } catch (Exception e) {
            log.error("Erreur lors du marquage du produit comme tendance", e);
            result.put("success", false);
            result.put("message", "Erreur lors de la mise à jour");
        }
        
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/produits/{id}/mettre-en-promotion")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre un produit en promotion", 
               description = "Permet à l'admin de mettre un produit en promotion")
    @ApiResponse(responseCode = "200", description = "Produit mis en promotion")
    @ApiResponse(responseCode = "403", description = "Accès refusé - Admin uniquement")
    @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    public ResponseEntity<Map<String, Object>> mettreProduitEnPromotion(
            @Parameter(description = "ID du produit") @PathVariable Long id,
            @Parameter(description = "Prix promotionnel") @RequestParam double prixPromo) {
        
        log.info("Admin: Mise en promotion du produit {} avec prix {}", id, prixPromo);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = produitService.mettreProduitEnPromotion(id, prixPromo);
            
            if (success) {
                result.put("success", true);
                result.put("message", "Produit mis en promotion");
            } else {
                result.put("success", false);
                result.put("message", "Produit non trouvé");
            }
            
        } catch (Exception e) {
            log.error("Erreur lors de la mise en promotion du produit", e);
            result.put("success", false);
            result.put("message", "Erreur lors de la mise à jour");
        }
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/statistiques-tendances")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir les statistiques des tendances pour l'admin", 
               description = "Récupère les statistiques détaillées des tendances pour le dashboard admin")
    @ApiResponse(responseCode = "200", description = "Statistiques récupérées")
    @ApiResponse(responseCode = "403", description = "Accès refusé - Admin uniquement")
    public ResponseEntity<Map<String, Object>> obtenirStatistiquesTendancesAdmin() {
        
        log.info("Admin: Récupération des statistiques des tendances");
        
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Statistiques générales
            long totalProduits = produitService.compterTotalProduits();
            long produitsEnStock = produitService.compterProduitsEnStock();
            long produitsPromo = produitService.compterProduitsEnPromotion();
            long produitsTendance = produitService.compterProduitsTendance();
            long produitsPlusVus = produitService.compterProduitsPlusVus();
            long produitsMieuxNotes = produitService.compterProduitsMieuxNotes();
            
            // Statistiques par catégorie
            Map<String, Long> statsParCategorie = produitService.obtenirStatistiquesParCategorie();
            
            stats.put("totalProduits", totalProduits);
            stats.put("produitsEnStock", produitsEnStock);
            stats.put("produitsPromo", produitsPromo);
            stats.put("produitsTendance", produitsTendance);
            stats.put("produitsPlusVus", produitsPlusVus);
            stats.put("produitsMieuxNotes", produitsMieuxNotes);
            stats.put("statsParCategorie", statsParCategorie);
            stats.put("success", true);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques pour l'admin", e);
            stats.put("success", false);
            stats.put("message", "Erreur lors de la récupération des statistiques");
        }
        
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/produits/{id}/incrementer-vues")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Incrémenter les vues d'un produit", 
               description = "Permet à l'admin d'incrémenter manuellement les vues d'un produit")
    @ApiResponse(responseCode = "200", description = "Vues incrémentées")
    @ApiResponse(responseCode = "403", description = "Accès refusé - Admin uniquement")
    public ResponseEntity<Map<String, Object>> incrementerVuesProduit(
            @Parameter(description = "ID du produit") @PathVariable Long id) {
        
        log.info("Admin: Incrémentation des vues du produit {}", id);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            produitService.incrementerVues(id);
            result.put("success", true);
            result.put("message", "Vues incrémentées");
            
        } catch (Exception e) {
            log.error("Erreur lors de l'incrémentation des vues", e);
            result.put("success", false);
            result.put("message", "Erreur lors de l'incrémentation");
        }
        
        return ResponseEntity.ok(result);
    }
}
