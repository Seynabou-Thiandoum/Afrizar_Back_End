package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.AjouterAuPanierDto;
import sn.afrizar.afrizar.dto.PanierDto;
import sn.afrizar.afrizar.model.Client;
import sn.afrizar.afrizar.model.Utilisateur;
import sn.afrizar.afrizar.service.PanierService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/panier")
@RequiredArgsConstructor
@Tag(name = "Panier", description = "API de gestion du panier")
@SecurityRequirement(name = "bearer-jwt")
public class PanierController {
    
    private final PanierService panierService;
    
    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Obtenir mon panier", description = "Récupère le panier du client connecté")
    public ResponseEntity<PanierDto> obtenirMonPanier(Authentication authentication) {
        Long clientId = getClientId(authentication);
        log.info("Récupération du panier pour le client ID: {}", clientId);
        
        PanierDto panier = panierService.obtenirPanierClient(clientId);
        return ResponseEntity.ok(panier);
    }
    
    @PostMapping("/ajouter")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Ajouter au panier", description = "Ajoute un produit au panier")
    public ResponseEntity<PanierDto> ajouterAuPanier(
            @Valid @RequestBody AjouterAuPanierDto dto,
            Authentication authentication) {
        
        Long clientId = getClientId(authentication);
        log.info("Ajout au panier - Client: {}, Produit: {}", clientId, dto.getProduitId());
        
        PanierDto panier = panierService.ajouterAuPanier(clientId, dto);
        return ResponseEntity.ok(panier);
    }
    
    @PutMapping("/item/{itemId}")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Modifier la quantité", description = "Modifie la quantité d'un article dans le panier")
    public ResponseEntity<PanierDto> modifierQuantite(
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> body,
            Authentication authentication) {
        
        Long clientId = getClientId(authentication);
        Integer nouvelleQuantite = body.get("quantite");
        
        log.info("Modification quantité - Client: {}, Item: {}, Quantité: {}", 
                clientId, itemId, nouvelleQuantite);
        
        PanierDto panier = panierService.modifierQuantite(clientId, itemId, nouvelleQuantite);
        return ResponseEntity.ok(panier);
    }
    
    @DeleteMapping("/item/{itemId}")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Retirer du panier", description = "Retire un article du panier")
    public ResponseEntity<PanierDto> retirerDuPanier(
            @PathVariable Long itemId,
            Authentication authentication) {
        
        Long clientId = getClientId(authentication);
        log.info("Retrait du panier - Client: {}, Item: {}", clientId, itemId);
        
        PanierDto panier = panierService.retirerDuPanier(clientId, itemId);
        return ResponseEntity.ok(panier);
    }
    
    @DeleteMapping("/vider")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Vider le panier", description = "Vide complètement le panier")
    public ResponseEntity<Map<String, String>> viderPanier(Authentication authentication) {
        Long clientId = getClientId(authentication);
        log.info("Vidage du panier - Client: {}", clientId);
        
        panierService.viderPanier(clientId);
        return ResponseEntity.ok(Map.of("message", "Panier vidé avec succès"));
    }
    
    @PostMapping("/synchroniser")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Synchroniser le panier", description = "Synchronise le panier (prix, stock)")
    public ResponseEntity<PanierDto> synchroniserPanier(Authentication authentication) {
        Long clientId = getClientId(authentication);
        log.info("Synchronisation du panier - Client: {}", clientId);
        
        PanierDto panier = panierService.synchroniserPanier(clientId);
        return ResponseEntity.ok(panier);
    }
    
    @GetMapping("/nombre-articles")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Nombre d'articles", description = "Récupère le nombre total d'articles dans le panier")
    public ResponseEntity<Map<String, Integer>> obtenirNombreArticles(Authentication authentication) {
        Long clientId = getClientId(authentication);
        
        int nombre = panierService.obtenirNombreArticles(clientId);
        return ResponseEntity.ok(Map.of("nombreArticles", nombre));
    }
    
    // ===================== MÉTHODE UTILITAIRE =====================
    
    private Long getClientId(Authentication authentication) {
        Utilisateur utilisateur = (Utilisateur) authentication.getPrincipal();
        
        if (!(utilisateur instanceof Client)) {
            throw new RuntimeException("Seuls les clients peuvent accéder au panier");
        }
        
        return utilisateur.getId();
    }
}




