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
import sn.afrizar.afrizar.dto.VendeurDto;
import sn.afrizar.afrizar.service.VendeurService;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/vendeurs")
@RequiredArgsConstructor
@Tag(name = "Vendeurs", description = "API de gestion des vendeurs")
public class VendeurController {
    
    private final VendeurService vendeurService;
    
    @PostMapping
    @Operation(summary = "Créer un nouveau vendeur", description = "Enregistre un nouveau vendeur dans le système")
    @ApiResponse(responseCode = "201", description = "Vendeur créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    public ResponseEntity<VendeurDto> creerVendeur(@Valid @RequestBody VendeurDto vendeurDto) {
        log.info("Création d'un nouveau vendeur avec email: {}", vendeurDto.getEmail());
        
        try {
            VendeurDto vendeurCree = vendeurService.creerVendeur(vendeurDto);
            return new ResponseEntity<>(vendeurCree, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la création du vendeur: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un vendeur par ID", description = "Récupère les détails d'un vendeur spécifique")
    @ApiResponse(responseCode = "200", description = "Vendeur trouvé")
    @ApiResponse(responseCode = "404", description = "Vendeur non trouvé")
    public ResponseEntity<VendeurDto> obtenirVendeur(
            @Parameter(description = "ID du vendeur") @PathVariable Long id) {
        
        return vendeurService.obtenirVendeurParId(id)
                .map(vendeur -> ResponseEntity.ok(vendeur))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "Obtenir un vendeur par email", description = "Récupère un vendeur par son adresse email")
    @ApiResponse(responseCode = "200", description = "Vendeur trouvé")
    @ApiResponse(responseCode = "404", description = "Vendeur non trouvé")
    public ResponseEntity<VendeurDto> obtenirVendeurParEmail(
            @Parameter(description = "Email du vendeur") @PathVariable String email) {
        
        return vendeurService.obtenirVendeurParEmail(email)
                .map(vendeur -> ResponseEntity.ok(vendeur))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "Lister tous les vendeurs", description = "Récupère la liste de tous les vendeurs avec pagination")
    @ApiResponse(responseCode = "200", description = "Liste des vendeurs récupérée")
    public ResponseEntity<Page<VendeurDto>> listerVendeurs(
            @Parameter(description = "Numéro de page (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Critère de tri") @RequestParam(defaultValue = "dateCreation") String sortBy,
            @Parameter(description = "Direction du tri (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<VendeurDto> vendeurs = vendeurService.obtenirVendeursAvecPagination(pageable);
        
        return ResponseEntity.ok(vendeurs);
    }
    
    @GetMapping("/verifies")
    @Operation(summary = "Obtenir les vendeurs vérifiés", description = "Récupère tous les vendeurs vérifiés classés par rating")
    @ApiResponse(responseCode = "200", description = "Vendeurs vérifiés récupérés")
    public ResponseEntity<List<VendeurDto>> obtenirVendeursVerifies() {
        List<VendeurDto> vendeurs = vendeurService.obtenirVendeursVerifies();
        return ResponseEntity.ok(vendeurs);
    }
    
    @GetMapping("/rating-minimum/{rating}")
    @Operation(summary = "Obtenir les vendeurs avec rating minimum", description = "Récupère les vendeurs ayant au moins le rating spécifié")
    @ApiResponse(responseCode = "200", description = "Vendeurs avec rating minimum récupérés")
    public ResponseEntity<List<VendeurDto>> obtenirVendeursParRatingMinimum(
            @Parameter(description = "Rating minimum") @PathVariable BigDecimal rating) {
        
        List<VendeurDto> vendeurs = vendeurService.obtenirVendeursParRatingMinimum(rating);
        return ResponseEntity.ok(vendeurs);
    }
    
    @GetMapping("/recherche")
    @Operation(summary = "Rechercher des vendeurs", description = "Recherche des vendeurs par nom de boutique ou spécialités")
    @ApiResponse(responseCode = "200", description = "Résultats de recherche")
    public ResponseEntity<List<VendeurDto>> rechercherVendeurs(
            @Parameter(description = "Terme de recherche") @RequestParam String terme) {
        
        List<VendeurDto> vendeurs = vendeurService.rechercherVendeurs(terme);
        return ResponseEntity.ok(vendeurs);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un vendeur", description = "Met à jour les informations d'un vendeur existant")
    @ApiResponse(responseCode = "200", description = "Vendeur mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Vendeur non trouvé")
    @ApiResponse(responseCode = "409", description = "Email déjà utilisé par un autre vendeur")
    public ResponseEntity<VendeurDto> mettreAJourVendeur(
            @Parameter(description = "ID du vendeur") @PathVariable Long id,
            @Valid @RequestBody VendeurDto vendeurDto) {
        
        try {
            VendeurDto vendeurMisAJour = vendeurService.mettreAJourVendeur(id, vendeurDto);
            return ResponseEntity.ok(vendeurMisAJour);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour du vendeur {}: {}", id, e.getMessage());
            if (e.getMessage().contains("email existe déjà")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/verifier")
    @Operation(summary = "Vérifier un vendeur", description = "Marque un vendeur comme vérifié")
    @ApiResponse(responseCode = "200", description = "Vendeur vérifié avec succès")
    @ApiResponse(responseCode = "404", description = "Vendeur non trouvé")
    public ResponseEntity<VendeurDto> verifierVendeur(
            @Parameter(description = "ID du vendeur") @PathVariable Long id) {
        
        try {
            VendeurDto vendeurVerifie = vendeurService.verifierVendeur(id);
            return ResponseEntity.ok(vendeurVerifie);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la vérification du vendeur {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/annuler-verification")
    @Operation(summary = "Annuler la vérification d'un vendeur", description = "Annule la vérification d'un vendeur")
    @ApiResponse(responseCode = "200", description = "Vérification annulée avec succès")
    @ApiResponse(responseCode = "404", description = "Vendeur non trouvé")
    public ResponseEntity<VendeurDto> annulerVerificationVendeur(
            @Parameter(description = "ID du vendeur") @PathVariable Long id) {
        
        try {
            VendeurDto vendeurModifie = vendeurService.annulerVerificationVendeur(id);
            return ResponseEntity.ok(vendeurModifie);
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'annulation de vérification du vendeur {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/commission")
    @Operation(summary = "Définir une commission personnalisée", description = "Définit un taux de commission personnalisé pour un vendeur")
    @ApiResponse(responseCode = "200", description = "Commission mise à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Vendeur non trouvé")
    public ResponseEntity<VendeurDto> definirCommissionPersonnalisee(
            @Parameter(description = "ID du vendeur") @PathVariable Long id,
            @Parameter(description = "Taux de commission personnalisé (%)") @RequestParam BigDecimal tauxCommission) {
        
        try {
            VendeurDto vendeurModifie = vendeurService.definirCommissionPersonnalisee(id, tauxCommission);
            return ResponseEntity.ok(vendeurModifie);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la définition de commission pour le vendeur {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/evaluation")
    @Operation(summary = "Ajouter une évaluation à un vendeur", description = "Ajoute une note à un vendeur")
    @ApiResponse(responseCode = "200", description = "Évaluation ajoutée avec succès")
    @ApiResponse(responseCode = "400", description = "Note invalide")
    @ApiResponse(responseCode = "404", description = "Vendeur non trouvé")
    public ResponseEntity<VendeurDto> ajouterEvaluation(
            @Parameter(description = "ID du vendeur") @PathVariable Long id,
            @Parameter(description = "Note (0-5)") @RequestParam BigDecimal note) {
        
        try {
            VendeurDto vendeurEvalue = vendeurService.ajouterEvaluation(id, note);
            return ResponseEntity.ok(vendeurEvalue);
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'ajout de l'évaluation au vendeur {}: {}", id, e.getMessage());
            if (e.getMessage().contains("note doit être")) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/desactiver")
    @Operation(summary = "Désactiver un vendeur", description = "Désactive le compte d'un vendeur")
    @ApiResponse(responseCode = "204", description = "Vendeur désactivé avec succès")
    @ApiResponse(responseCode = "404", description = "Vendeur non trouvé")
    public ResponseEntity<Void> desactiverVendeur(
            @Parameter(description = "ID du vendeur") @PathVariable Long id) {
        
        try {
            vendeurService.desactiverVendeur(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la désactivation du vendeur {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/activer")
    @Operation(summary = "Activer un vendeur", description = "Active le compte d'un vendeur")
    @ApiResponse(responseCode = "204", description = "Vendeur activé avec succès")
    @ApiResponse(responseCode = "404", description = "Vendeur non trouvé")
    public ResponseEntity<Void> activerVendeur(
            @Parameter(description = "ID du vendeur") @PathVariable Long id) {
        
        try {
            vendeurService.activerVendeurVoid(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'activation du vendeur {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un vendeur", description = "Supprime définitivement un vendeur")
    @ApiResponse(responseCode = "204", description = "Vendeur supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Vendeur non trouvé")
    public ResponseEntity<Void> supprimerVendeur(
            @Parameter(description = "ID du vendeur") @PathVariable Long id) {
        
        try {
            vendeurService.supprimerVendeur(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression du vendeur {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    // Endpoints de statistiques
    @GetMapping("/{id}/statistiques/produits")
    @Operation(summary = "Obtenir les statistiques des produits d'un vendeur", description = "Récupère le nombre de produits d'un vendeur")
    @ApiResponse(responseCode = "200", description = "Statistiques récupérées")
    public ResponseEntity<Long> getNombreProduitsVendeur(
            @Parameter(description = "ID du vendeur") @PathVariable Long id) {
        
        try {
            Long nombreProduits = vendeurService.getNombreProduitsVendeur(id);
            return ResponseEntity.ok(nombreProduits);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/statistiques/count/verifies")
    @Operation(summary = "Compter les vendeurs vérifiés", description = "Compte le nombre total de vendeurs vérifiés")
    @ApiResponse(responseCode = "200", description = "Nombre de vendeurs vérifiés")
    public ResponseEntity<Long> getNombreVendeursVerifies() {
        Long nombre = vendeurService.getNombreVendeursVerifies();
        return ResponseEntity.ok(nombre != null ? nombre : 0L);
    }
    
    @GetMapping("/email/{email}/disponible")
    @Operation(summary = "Vérifier la disponibilité d'un email", description = "Vérifie si une adresse email est disponible")
    @ApiResponse(responseCode = "200", description = "Disponibilité vérifiée")
    public ResponseEntity<Boolean> verifierEmailDisponible(
            @Parameter(description = "Adresse email à vérifier") @PathVariable String email) {
        
        boolean disponible = vendeurService.verifierEmailDisponible(email);
        return ResponseEntity.ok(disponible);
    }
}

