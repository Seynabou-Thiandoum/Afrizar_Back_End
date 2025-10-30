package sn.afrizar.afrizar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.VenteFlashDto;
import sn.afrizar.afrizar.service.VenteFlashService;

import java.util.List;

@RestController
@RequestMapping("/api/public/ventes-flash")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PublicVenteFlashController {
    
    private final VenteFlashService venteFlashService;
    
    /**
     * Obtenir la vente flash active principale (pour afficher en haut de page)
     */
    @GetMapping("/active")
    public ResponseEntity<VenteFlashDto> obtenirVenteFlashActive() {
        return venteFlashService.obtenirVenteFlashActive()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Obtenir toutes les ventes flash actives
     */
    @GetMapping("/toutes")
    public ResponseEntity<List<VenteFlashDto>> obtenirToutesVentesFlashActives() {
        List<VenteFlashDto> ventesFlash = venteFlashService.obtenirToutesVentesFlashActives();
        return ResponseEntity.ok(ventesFlash);
    }
    
    /**
     * Obtenir une vente flash avec ses produits
     */
    @GetMapping("/{id}/produits")
    public ResponseEntity<VenteFlashDto> obtenirVenteFlashAvecProduits(@PathVariable Long id) {
        VenteFlashDto venteFlash = venteFlashService.obtenirVenteFlashAvecProduits(id);
        if (venteFlash != null) {
            return ResponseEntity.ok(venteFlash);
        }
        return ResponseEntity.notFound().build();
    }
}

