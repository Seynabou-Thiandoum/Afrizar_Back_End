package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.FraisLivraisonDto;
import sn.afrizar.afrizar.service.FraisLivraisonService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/public/frais-livraison")
@RequiredArgsConstructor
@Tag(name = "Public - Frais de Livraison", description = "API publique des frais de livraison actifs")
public class PublicFraisLivraisonController {
    
    private final FraisLivraisonService fraisLivraisonService;
    
    @GetMapping("/actifs")
    @Operation(summary = "Obtenir les frais de livraison actifs", 
               description = "Récupère tous les frais de livraison actifs configurés par l'admin (endpoint public)")
    public ResponseEntity<List<FraisLivraisonDto>> obtenirFraisLivraisonActifs() {
        log.info("Récupération des frais de livraison actifs (public)");
        
        List<FraisLivraisonDto> fraisLivraisonList = fraisLivraisonService.obtenirFraisLivraisonActifs();
        return ResponseEntity.ok(fraisLivraisonList);
    }
    
    @GetMapping("/applicables")
    @Operation(summary = "Obtenir les frais de livraison applicables", 
               description = "Récupère les frais de livraison actifs applicables selon le poids et la zone (endpoint public)")
    public ResponseEntity<List<FraisLivraisonDto>> obtenirFraisLivraisonApplicables(
            @RequestParam(required = false) Double poids,
            @RequestParam(required = false) String zone) {
        
        log.info("Récupération des frais de livraison applicables (public) - Poids: {} kg, Zone: {}", poids, zone);
        
        List<FraisLivraisonDto> fraisLivraisonList = fraisLivraisonService.obtenirFraisLivraisonApplicables(poids, zone);
        return ResponseEntity.ok(fraisLivraisonList);
    }
}

