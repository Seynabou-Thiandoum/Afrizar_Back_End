package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.CreateProduitDto;
import sn.afrizar.afrizar.dto.ProduitDto;
import sn.afrizar.afrizar.model.Produit;
import sn.afrizar.afrizar.service.ProduitService;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
@Tag(name = "Produits", description = "API de gestion des produits")
public class ProduitController {
    
    private final ProduitService produitService;
    
    @PostMapping
    @Operation(summary = "Créer un nouveau produit", description = "Crée un nouveau produit dans le catalogue")
    @ApiResponse(responseCode = "201", description = "Produit créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<ProduitDto> creerProduit(@Valid @RequestBody CreateProduitDto createProduitDto) {
        log.info("Création d'un nouveau produit: {}", createProduitDto.getNom());
        ProduitDto produitCree = produitService.creerProduit(createProduitDto);
        return new ResponseEntity<>(produitCree, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un produit par ID", description = "Récupère les détails d'un produit spécifique")
    @ApiResponse(responseCode = "200", description = "Produit trouvé")
    @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    public ResponseEntity<ProduitDto> obtenirProduit(
            @Parameter(description = "ID du produit") @PathVariable Long id) {
        
        // Incrémenter le nombre de vues
        produitService.incrementerVues(id);
        
        return produitService.obtenirProduitParId(id)
                .map(produit -> ResponseEntity.ok(produit))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "Lister tous les produits", description = "Récupère la liste de tous les produits actifs avec pagination")
    @ApiResponse(responseCode = "200", description = "Liste des produits récupérée")
    public ResponseEntity<Page<ProduitDto>> listerProduits(
            @Parameter(description = "Numéro de page (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Critère de tri") @RequestParam(defaultValue = "dateCreation") String sortBy,
            @Parameter(description = "Direction du tri (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProduitDto> produits = produitService.obtenirProduitsAvecPagination(pageable);
        
        return ResponseEntity.ok(produits);
    }
    
    @GetMapping("/recherche")
    @Operation(summary = "Rechercher des produits avec filtres", description = "Recherche des produits selon plusieurs critères")
    @ApiResponse(responseCode = "200", description = "Résultats de recherche")
    public ResponseEntity<Page<ProduitDto>> rechercherProduits(
            @Parameter(description = "Nom du produit") @RequestParam(required = false) String nom,
            @Parameter(description = "ID de la catégorie") @RequestParam(required = false) Long categorieId,
            @Parameter(description = "ID du vendeur") @RequestParam(required = false) Long vendeurId,
            @Parameter(description = "Prix minimum") @RequestParam(required = false) BigDecimal prixMin,
            @Parameter(description = "Prix maximum") @RequestParam(required = false) BigDecimal prixMax,
            @Parameter(description = "Qualité du produit") @RequestParam(required = false) Produit.Qualite qualite,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Critère de tri") @RequestParam(defaultValue = "dateCreation") String sortBy,
            @Parameter(description = "Direction du tri") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProduitDto> resultats = produitService.rechercherProduitsAvecFiltres(
            nom, categorieId, vendeurId, prixMin, prixMax, qualite, pageable);
        
        return ResponseEntity.ok(resultats);
    }
    
    @GetMapping("/vendeur/{vendeurId}")
    @Operation(summary = "Obtenir les produits d'un vendeur", description = "Récupère tous les produits d'un vendeur spécifique")
    @ApiResponse(responseCode = "200", description = "Produits du vendeur récupérés")
    public ResponseEntity<List<ProduitDto>> obtenirProduitsParVendeur(
            @Parameter(description = "ID du vendeur") @PathVariable Long vendeurId) {
        
        List<ProduitDto> produits = produitService.obtenirProduitsParVendeur(vendeurId);
        return ResponseEntity.ok(produits);
    }
    
    @GetMapping("/categorie/{categorieId}")
    @Operation(summary = "Obtenir les produits d'une catégorie", description = "Récupère tous les produits d'une catégorie spécifique")
    @ApiResponse(responseCode = "200", description = "Produits de la catégorie récupérés")
    public ResponseEntity<List<ProduitDto>> obtenirProduitsParCategorie(
            @Parameter(description = "ID de la catégorie") @PathVariable Long categorieId) {
        
        List<ProduitDto> produits = produitService.obtenirProduitsParCategorie(categorieId);
        return ResponseEntity.ok(produits);
    }
    
    @GetMapping("/en-stock")
    @Operation(summary = "Obtenir les produits en stock", description = "Récupère tous les produits disponibles en stock")
    @ApiResponse(responseCode = "200", description = "Produits en stock récupérés")
    public ResponseEntity<List<ProduitDto>> obtenirProduitsEnStock() {
        List<ProduitDto> produits = produitService.obtenirProduitsEnStock();
        return ResponseEntity.ok(produits);
    }
    
    @GetMapping("/sur-commande")
    @Operation(summary = "Obtenir les produits sur commande", description = "Récupère tous les produits disponibles sur commande")
    @ApiResponse(responseCode = "200", description = "Produits sur commande récupérés")
    public ResponseEntity<List<ProduitDto>> obtenirProduitsSurCommande() {
        List<ProduitDto> produits = produitService.obtenirProduitsSurCommande();
        return ResponseEntity.ok(produits);
    }
    
    @GetMapping("/mieux-notes")
    @Operation(summary = "Obtenir les produits les mieux notés", description = "Récupère les produits avec les meilleures notes")
    @ApiResponse(responseCode = "200", description = "Produits les mieux notés récupérés")
    public ResponseEntity<Page<ProduitDto>> obtenirProduitsMieuxNotes(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProduitDto> produits = produitService.obtenirProduitsMieuxNotes(pageable);
        return ResponseEntity.ok(produits);
    }
    
    @GetMapping("/plus-vus")
    @Operation(summary = "Obtenir les produits les plus vus", description = "Récupère les produits les plus consultés")
    @ApiResponse(responseCode = "200", description = "Produits les plus vus récupérés")
    public ResponseEntity<Page<ProduitDto>> obtenirProduitsPlusVus(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProduitDto> produits = produitService.obtenirProduitsPlusVus(pageable);
        return ResponseEntity.ok(produits);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un produit", description = "Met à jour les informations d'un produit existant")
    @ApiResponse(responseCode = "200", description = "Produit mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    public ResponseEntity<ProduitDto> mettreAJourProduit(
            @Parameter(description = "ID du produit") @PathVariable Long id,
            @Valid @RequestBody CreateProduitDto createProduitDto) {
        
        try {
            ProduitDto produitMisAJour = produitService.mettreAJourProduit(id, createProduitDto);
            return ResponseEntity.ok(produitMisAJour);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour du produit {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/stock")
    @Operation(summary = "Mettre à jour le stock d'un produit", description = "Met à jour la quantité en stock d'un produit")
    @ApiResponse(responseCode = "200", description = "Stock mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    public ResponseEntity<ProduitDto> mettreAJourStock(
            @Parameter(description = "ID du produit") @PathVariable Long id,
            @Parameter(description = "Nouvelle quantité en stock") @RequestParam Integer stock) {
        
        try {
            ProduitDto produitMisAJour = produitService.mettreAJourStock(id, stock);
            return ResponseEntity.ok(produitMisAJour);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour du stock du produit {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/evaluation")
    @Operation(summary = "Ajouter une évaluation", description = "Ajoute une note à un produit")
    @ApiResponse(responseCode = "200", description = "Évaluation ajoutée avec succès")
    @ApiResponse(responseCode = "400", description = "Note invalide")
    public ResponseEntity<ProduitDto> ajouterEvaluation(
            @Parameter(description = "ID du produit") @PathVariable Long id,
            @Parameter(description = "Note (0-5)") @RequestParam BigDecimal note) {
        
        try {
            ProduitDto produitEvalue = produitService.ajouterEvaluation(id, note);
            return ResponseEntity.ok(produitEvalue);
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'ajout de l'évaluation au produit {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un produit", description = "Supprime définitivement un produit")
    @ApiResponse(responseCode = "204", description = "Produit supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    public ResponseEntity<Void> supprimerProduit(
            @Parameter(description = "ID du produit") @PathVariable Long id) {
        
        try {
            produitService.supprimerProduit(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression du produit {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/archiver")
    @Operation(summary = "Archiver un produit", description = "Archive un produit (le rend inactif)")
    @ApiResponse(responseCode = "204", description = "Produit archivé avec succès")
    @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    public ResponseEntity<Void> archiverProduit(
            @Parameter(description = "ID du produit") @PathVariable Long id) {
        
        try {
            produitService.archiverProduit(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'archivage du produit {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/activer")
    @Operation(summary = "Activer un produit", description = "Active un produit archivé")
    @ApiResponse(responseCode = "204", description = "Produit activé avec succès")
    @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    public ResponseEntity<Void> activerProduit(
            @Parameter(description = "ID du produit") @PathVariable Long id) {
        
        try {
            produitService.activerProduit(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'activation du produit {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/taille/{taille}")
    @Operation(summary = "Obtenir les produits par taille", description = "Récupère tous les produits disponibles dans une taille spécifique")
    @ApiResponse(responseCode = "200", description = "Produits de la taille récupérés")
    public ResponseEntity<List<ProduitDto>> obtenirProduitsParTaille(
            @Parameter(description = "Taille recherchée") @PathVariable Produit.Taille taille) {
        
        List<ProduitDto> produits = produitService.obtenirProduitsParTaille(taille);
        return ResponseEntity.ok(produits);
    }
    
    @GetMapping("/{id}/disponibilite-stock")
    @Operation(summary = "Vérifier la disponibilité du stock", description = "Vérifie si une quantité est disponible en stock")
    @ApiResponse(responseCode = "200", description = "Disponibilité vérifiée")
    public ResponseEntity<Boolean> verifierDisponibiliteStock(
            @Parameter(description = "ID du produit") @PathVariable Long id,
            @Parameter(description = "Quantité souhaitée") @RequestParam Integer quantite) {
        
        boolean disponible = produitService.verifierDisponibiliteStock(id, quantite);
        return ResponseEntity.ok(disponible);
    }
}

