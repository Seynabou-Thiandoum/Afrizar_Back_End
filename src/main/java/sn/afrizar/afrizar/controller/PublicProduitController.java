package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.ProduitDto;
import sn.afrizar.afrizar.model.Produit;
import sn.afrizar.afrizar.repository.ProduitRepository;
import sn.afrizar.afrizar.model.Vendeur;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/public/produits")
@RequiredArgsConstructor
@Tag(name = "Public Produits", description = "API publique des produits")
public class PublicProduitController {
    
    private final ProduitRepository produitRepository;
    
    @GetMapping
    @Operation(summary = "Obtenir tous les produits publiés", 
               description = "Récupère la liste des produits actifs avec pagination et filtres")
    public ResponseEntity<Map<String, Object>> obtenirProduitsPublies(
            @Parameter(description = "ID de la catégorie") @RequestParam(required = false) Long categorieId,
            @Parameter(description = "Type de catégorie") @RequestParam(required = false) String type,
            @Parameter(description = "Genre de catégorie") @RequestParam(required = false) String genre,
            @Parameter(description = "Prix minimum") @RequestParam(required = false) BigDecimal prixMin,
            @Parameter(description = "Prix maximum") @RequestParam(required = false) BigDecimal prixMax,
            @Parameter(description = "Recherche par nom") @RequestParam(required = false) String search,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size) {
        
        log.info("Récupération des produits publiés pour affichage public");
        
        // Construire la requête de recherche basée sur les filtres
        String searchTerm = (search != null && !search.isEmpty()) ? search : null;
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Produit> produitsPage = produitRepository.findProduitsAvecFiltres(
            searchTerm, categorieId, null, prixMin, prixMax, null, pageable);
        
        // Convertir les produits en format public
        List<Map<String, Object>> produitsPublics = produitsPage.getContent().stream()
            .filter(produit -> estPublie(produit))
            .map(this::convertirEnPublicProduit)
            .toList();
        
        // Construire la réponse avec pagination
        Map<String, Object> response = new HashMap<>();
        response.put("content", produitsPublics);
        response.put("totalElements", produitsPage.getTotalElements());
        response.put("totalPages", produitsPage.getTotalPages());
        response.put("size", produitsPage.getSize());
        response.put("number", produitsPage.getNumber());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un produit publié par ID", 
               description = "Récupère les détails d'un produit publié spécifique")
    public ResponseEntity<Map<String, Object>> obtenirProduitPublie(
            @Parameter(description = "ID du produit") @PathVariable Long id) {
        
        log.info("Récupération du produit publié avec ID: {}", id);
        
        return produitRepository.findById(id)
            .filter(this::estPublie)
            .map(produit -> ResponseEntity.ok(convertirEnPublicProduit(produit)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/categories")
    @Operation(summary = "Obtenir toutes les catégories", 
               description = "Récupère la liste des catégories publiques")
    public ResponseEntity<Map<String, Object>> obtenirCategories() {
        log.info("Récupération des catégories publiques");
        // Cette méthode sera implémentée plus tard si nécessaire
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Endpoint à implémenter");
        return ResponseEntity.ok(response);
    }
    
    private boolean estPublie(Produit produit) {
        return produit.getStatut() == Produit.StatutProduit.ACTIF 
            && produit.getVendeur() != null 
            && produit.getVendeur().isActif() 
            && produit.getVendeur().isVerifie();
    }
    
    private Map<String, Object> convertirEnPublicProduit(Produit produit) {
        Map<String, Object> publicProduit = new HashMap<>();
        
        publicProduit.put("id", produit.getId());
        publicProduit.put("nom", produit.getNom());
        publicProduit.put("description", produit.getDescription());
        publicProduit.put("prix", produit.getPrix());
        publicProduit.put("prixPromotionnel", produit.getPrixPromo());
        publicProduit.put("stock", produit.getStock());
        publicProduit.put("statut", produit.getStatut().name());
        publicProduit.put("taille", produit.getTaille());
        publicProduit.put("couleur", produit.getCouleur());
        publicProduit.put("matiere", produit.getMatiere());
        publicProduit.put("poids", produit.getPoids());
        publicProduit.put("dateCreation", produit.getDateCreation());
        
        // Image principale (première photo si disponible)
        if (produit.getPhotos() != null && !produit.getPhotos().isEmpty()) {
            publicProduit.put("imageUrl", produit.getPhotos().get(0));
        }
        
        // Informations vendeur
        if (produit.getVendeur() != null) {
            Map<String, Object> vendeur = new HashMap<>();
            vendeur.put("id", produit.getVendeur().getId());
            vendeur.put("nom", produit.getVendeur().getNom());
            vendeur.put("prenom", produit.getVendeur().getPrenom());
            vendeur.put("nomBoutique", produit.getVendeur().getNomBoutique());
            vendeur.put("photoUrl", produit.getVendeur().getPhotoUrl());
            publicProduit.put("vendeur", vendeur);
        }
        
        // Informations catégorie
        if (produit.getCategorie() != null) {
            Map<String, Object> categorie = new HashMap<>();
            categorie.put("id", produit.getCategorie().getId());
            categorie.put("nom", produit.getCategorie().getNom());
            categorie.put("type", produit.getCategorie().getType());
            categorie.put("genre", produit.getCategorie().getGenre());
            categorie.put("imageUrl", produit.getCategorie().getImageUrl());
            publicProduit.put("categorie", categorie);
        }
        
        return publicProduit;
    }
}

