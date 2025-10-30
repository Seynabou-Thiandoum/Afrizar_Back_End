package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.VendeurDto;
import sn.afrizar.afrizar.service.VendeurService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/public/vendeurs")
@RequiredArgsConstructor
@Tag(name = "Public Vendeurs", description = "API publique des vendeurs")
public class PublicVendeurController {
    
    private final VendeurService vendeurService;
    
    @GetMapping
    @Operation(summary = "Obtenir tous les vendeurs publiés", description = "Récupère la liste des vendeurs publiés, actifs et vérifiés")
    public ResponseEntity<List<VendeurDto>> obtenirVendeursPublies() {
        log.info("Récupération des vendeurs publiés pour affichage public");
        List<VendeurDto> vendeurs = vendeurService.obtenirVendeursPublies();
        return ResponseEntity.ok(vendeurs);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'un vendeur publié", description = "Récupère les détails d'un vendeur publié spécifique")
    public ResponseEntity<VendeurDto> obtenirVendeurPublie(@PathVariable Long id) {
        log.info("Récupération du vendeur publié avec ID: {}", id);
        return vendeurService.obtenirVendeurParId(id)
                .filter(vendeur -> vendeur.isPublie() && vendeur.isActif() && vendeur.isVerifie())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}




