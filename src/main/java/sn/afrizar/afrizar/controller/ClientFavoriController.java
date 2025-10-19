package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.FavoriDto;
import sn.afrizar.afrizar.model.Utilisateur;
import sn.afrizar.afrizar.service.FavoriService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/client/favoris")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Client - Favoris", description = "Gestion des produits favoris du client")
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasRole('CLIENT')")
public class ClientFavoriController {

    private final FavoriService favoriService;

    @GetMapping
    @Operation(summary = "Obtenir mes favoris", description = "Récupère la liste des produits favoris du client connecté")
    public ResponseEntity<List<FavoriDto>> getMesFavoris(Authentication authentication) {
        log.info("Client: Récupération des favoris");
        
        Utilisateur user = (Utilisateur) authentication.getPrincipal();
        List<FavoriDto> favoris = favoriService.getMesFavoris(user.getId());
        
        return ResponseEntity.ok(favoris);
    }

    @PostMapping
    @Operation(summary = "Ajouter un produit aux favoris", description = "Ajoute un produit à la liste des favoris")
    public ResponseEntity<FavoriDto> ajouterFavori(
            @RequestBody Map<String, Long> request,
            Authentication authentication) {
        log.info("Client: Ajout d'un produit aux favoris - Produit ID: {}", request.get("produitId"));
        
        Utilisateur user = (Utilisateur) authentication.getPrincipal();
        Long produitId = request.get("produitId");
        
        if (produitId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        FavoriDto favori = favoriService.ajouterFavori(user.getId(), produitId);
        return ResponseEntity.ok(favori);
    }

    @DeleteMapping("/{favoriId}")
    @Operation(summary = "Retirer un produit des favoris", description = "Retire un produit de la liste des favoris")
    public ResponseEntity<Void> retirerFavori(
            @PathVariable Long favoriId,
            Authentication authentication) {
        log.info("Client: Retrait du favori {}", favoriId);
        
        Utilisateur user = (Utilisateur) authentication.getPrincipal();
        favoriService.retirerFavori(user.getId(), favoriId);
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verifier/{produitId}")
    @Operation(summary = "Vérifier si un produit est en favori", description = "Vérifie si un produit est dans les favoris du client")
    public ResponseEntity<Map<String, Boolean>> verifierFavori(
            @PathVariable Long produitId,
            Authentication authentication) {
        Utilisateur user = (Utilisateur) authentication.getPrincipal();
        boolean estFavori = favoriService.estFavori(user.getId(), produitId);
        
        return ResponseEntity.ok(Map.of("estFavori", estFavori));
    }

    @GetMapping("/count")
    @Operation(summary = "Compter les favoris", description = "Retourne le nombre de produits dans les favoris")
    public ResponseEntity<Map<String, Long>> compterFavoris(Authentication authentication) {
        Utilisateur user = (Utilisateur) authentication.getPrincipal();
        long count = favoriService.compterFavoris(user.getId());
        
        return ResponseEntity.ok(Map.of("count", count));
    }
}

