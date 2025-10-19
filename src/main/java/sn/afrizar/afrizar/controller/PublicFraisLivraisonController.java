package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.FraisLivraisonDto;
import sn.afrizar.afrizar.service.FraisLivraisonService;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/public/frais-livraison")
@RequiredArgsConstructor
@Tag(name = "Public - Frais de Livraison", description = "API publique pour les frais de livraison")
public class PublicFraisLivraisonController {
    
    private final FraisLivraisonService fraisLivraisonService;
    
    @GetMapping
    @Operation(summary = "Obtenir les options de livraison", description = "Récupère toutes les options de livraison disponibles pour les clients")
    @ApiResponse(responseCode = "200", description = "Liste des options de livraison")
    public ResponseEntity<List<FraisLivraisonDto>> obtenirOptionsLivraison() {
        log.info("Public: Récupération des options de livraison");
        List<FraisLivraisonDto> optionsLivraison = fraisLivraisonService.obtenirOptionsLivraison();
        return ResponseEntity.ok(optionsLivraison);
    }
    
    @GetMapping("/calculer")
    @Operation(summary = "Calculer les frais de livraison", description = "Calcule les frais de livraison selon le type, poids et zone")
    @ApiResponse(responseCode = "200", description = "Frais de livraison calculés")
    public ResponseEntity<FraisLivraisonDto> calculerFraisLivraison(
        @Parameter(description = "Type de livraison (EXPRESS ou STANDARD)") @RequestParam String type,
        @Parameter(description = "Poids du colis en kg") @RequestParam(required = false) BigDecimal poids,
        @Parameter(description = "Zone de livraison") @RequestParam(required = false) String zone
    ) {
        log.info("Public: Calcul des frais de livraison - Type: {}, Poids: {}, Zone: {}", type, poids, zone);
        try {
            FraisLivraisonDto fraisLivraison = fraisLivraisonService.calculerFraisLivraison(type, poids, zone);
            return ResponseEntity.ok(fraisLivraison);
        } catch (RuntimeException e) {
            log.error("Erreur lors du calcul des frais de livraison: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/types")
    @Operation(summary = "Obtenir les types de livraison", description = "Récupère les types de livraison disponibles")
    @ApiResponse(responseCode = "200", description = "Liste des types de livraison")
    public ResponseEntity<List<FraisLivraisonDto>> obtenirTypesLivraison() {
        log.info("Public: Récupération des types de livraison");
        List<FraisLivraisonDto> typesLivraison = fraisLivraisonService.obtenirFraisLivraisonActifs();
        return ResponseEntity.ok(typesLivraison);
    }
}
