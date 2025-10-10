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
import sn.afrizar.afrizar.dto.InscriptionRequestDto;
import sn.afrizar.afrizar.dto.ProduitDto;
import sn.afrizar.afrizar.dto.UtilisateurDto;
import sn.afrizar.afrizar.dto.VendeurDto;
import sn.afrizar.afrizar.model.Produit;
import sn.afrizar.afrizar.model.Utilisateur;
import sn.afrizar.afrizar.service.AuthService;
import sn.afrizar.afrizar.service.ProduitService;
import sn.afrizar.afrizar.service.VendeurService;
import sn.afrizar.afrizar.service.ClientService;
import sn.afrizar.afrizar.repository.UtilisateurRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Administration", description = "API d'administration réservée aux admins")
@SecurityRequirement(name = "bearer-jwt")
public class AdminController {
    
    private final ProduitService produitService;
    private final VendeurService vendeurService;
    private final ClientService clientService;
    private final UtilisateurRepository utilisateurRepository;
    private final AuthService authService;
    
    // ===================== GESTION DES PRODUITS =====================
    
    @GetMapping("/produits/en-attente")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir les produits en attente de validation", 
               description = "Récupère tous les produits en attente de validation par l'admin")
    @ApiResponse(responseCode = "200", description = "Liste des produits en attente")
    @ApiResponse(responseCode = "403", description = "Accès refusé - Admin uniquement")
    public ResponseEntity<Page<ProduitDto>> obtenirProduitsEnAttente(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size) {
        
        log.info("Admin: Récupération des produits en attente de validation");
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        Page<ProduitDto> produitsEnAttente = produitService.obtenirProduitsEnAttente(pageable);
        
        return ResponseEntity.ok(produitsEnAttente);
    }
    
    @GetMapping("/produits/tous")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir tous les produits (tous statuts)", 
               description = "Récupère tous les produits quelle que soit leur statut")
    @ApiResponse(responseCode = "200", description = "Liste de tous les produits")
    public ResponseEntity<Page<ProduitDto>> obtenirTousLesProduits(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Statut du produit") @RequestParam(required = false) Produit.StatutProduit statut) {
        
        log.info("Admin: Récupération de tous les produits");
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        Page<ProduitDto> produits;
        
        if (statut != null) {
            produits = produitService.obtenirProduitsParStatut(statut, pageable);
        } else {
            produits = produitService.obtenirTousLesProduitsAdmin(pageable);
        }
        
        return ResponseEntity.ok(produits);
    }
    
    @PatchMapping("/produits/{id}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Valider et publier un produit", 
               description = "Valide un produit et le rend visible aux utilisateurs")
    @ApiResponse(responseCode = "200", description = "Produit validé et publié")
    @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    public ResponseEntity<ProduitDto> validerProduit(@PathVariable Long id) {
        log.info("Admin: Validation du produit {}", id);
        
        try {
            ProduitDto produit = produitService.validerProduit(id);
            return ResponseEntity.ok(produit);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la validation du produit {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/produits/{id}/rejeter")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Rejeter un produit", 
               description = "Rejette un produit en attente de validation")
    @ApiResponse(responseCode = "200", description = "Produit rejeté")
    @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    public ResponseEntity<Map<String, String>> rejeterProduit(
            @PathVariable Long id,
            @RequestParam(required = false) String motif) {
        
        log.info("Admin: Rejet du produit {} - Motif: {}", id, motif);
        
        try {
            produitService.rejeterProduit(id, motif);
            return ResponseEntity.ok(Map.of(
                "message", "Produit rejeté avec succès",
                "motif", motif != null ? motif : "Non spécifié"
            ));
        } catch (RuntimeException e) {
            log.error("Erreur lors du rejet du produit {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    // ===================== GESTION DES VENDEURS =====================
    
    @GetMapping("/vendeurs/tous")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir tous les vendeurs", 
               description = "Récupère la liste de tous les vendeurs inscrits")
    @ApiResponse(responseCode = "200", description = "Liste de tous les vendeurs")
    public ResponseEntity<Page<VendeurDto>> obtenirTousLesVendeurs(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Inclure les non vérifiés") @RequestParam(defaultValue = "true") boolean includeNonVerifies) {
        
        log.info("Admin: Récupération de tous les vendeurs");
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        Page<VendeurDto> vendeurs = vendeurService.obtenirTousLesVendeurs(pageable, includeNonVerifies);
        
        return ResponseEntity.ok(vendeurs);
    }
    
    @GetMapping("/vendeurs/non-verifies")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir les vendeurs non vérifiés", 
               description = "Récupère les vendeurs en attente de vérification")
    @ApiResponse(responseCode = "200", description = "Liste des vendeurs non vérifiés")
    public ResponseEntity<List<VendeurDto>> obtenirVendeursNonVerifies() {
        log.info("Admin: Récupération des vendeurs non vérifiés");
        List<VendeurDto> vendeurs = vendeurService.obtenirVendeursNonVerifies();
        return ResponseEntity.ok(vendeurs);
    }
    
    @PatchMapping("/vendeurs/{id}/verifier")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Vérifier un vendeur", 
               description = "Approuve et vérifie un compte vendeur")
    @ApiResponse(responseCode = "200", description = "Vendeur vérifié")
    @ApiResponse(responseCode = "404", description = "Vendeur non trouvé")
    public ResponseEntity<VendeurDto> verifierVendeur(@PathVariable Long id) {
        log.info("Admin: Vérification du vendeur {}", id);
        
        try {
            VendeurDto vendeur = vendeurService.verifierVendeur(id);
            return ResponseEntity.ok(vendeur);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la vérification du vendeur {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/vendeurs/{id}/desactiver")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Désactiver un vendeur", 
               description = "Désactive le compte d'un vendeur")
    @ApiResponse(responseCode = "200", description = "Vendeur désactivé")
    @ApiResponse(responseCode = "404", description = "Vendeur non trouvé")
    public ResponseEntity<Map<String, String>> desactiverVendeur(
            @PathVariable Long id,
            @RequestParam(required = false) String motif) {
        
        log.info("Admin: Désactivation du vendeur {} - Motif: {}", id, motif);
        
        try {
            vendeurService.desactiverVendeur(id);
            return ResponseEntity.ok(Map.of(
                "message", "Vendeur désactivé avec succès",
                "motif", motif != null ? motif : "Non spécifié"
            ));
        } catch (RuntimeException e) {
            log.error("Erreur lors de la désactivation du vendeur {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/vendeurs/{id}/activer")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activer un vendeur", 
               description = "Réactive le compte d'un vendeur désactivé")
    @ApiResponse(responseCode = "200", description = "Vendeur activé")
    @ApiResponse(responseCode = "404", description = "Vendeur non trouvé")
    public ResponseEntity<VendeurDto> activerVendeur(@PathVariable Long id) {
        log.info("Admin: Activation du vendeur {}", id);
        
        try {
            VendeurDto vendeur = vendeurService.activerVendeur(id);
            return ResponseEntity.ok(vendeur);
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'activation du vendeur {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    // ===================== GESTION DES UTILISATEURS =====================
    
    @PostMapping("/utilisateurs/creer")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un utilisateur (tous rôles)", 
               description = "Permet à l'admin de créer un utilisateur avec n'importe quel rôle (CLIENT, VENDEUR, ADMIN, SUPPORT)")
    @ApiResponse(responseCode = "200", description = "Utilisateur créé avec succès")
    @ApiResponse(responseCode = "400", description = "Erreur lors de la création")
    public ResponseEntity<?> creerUtilisateur(@RequestBody InscriptionRequestDto request) {
        log.info("Admin: Création d'un nouvel utilisateur - Rôle: {}", request.getRole());
        
        try {
            UtilisateurDto utilisateur = authService.creerUtilisateurParAdmin(request);
            return ResponseEntity.ok(Map.of(
                "message", "Utilisateur créé avec succès",
                "utilisateur", utilisateur
            ));
        } catch (RuntimeException e) {
            log.error("Erreur lors de la création de l'utilisateur: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Erreur lors de la création de l'utilisateur",
                "erreur", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/utilisateurs/tous")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir tous les utilisateurs", 
               description = "Récupère la liste de tous les utilisateurs du système")
    @ApiResponse(responseCode = "200", description = "Liste de tous les utilisateurs")
    public ResponseEntity<Page<UtilisateurDto>> obtenirTousLesUtilisateurs(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Rôle") @RequestParam(required = false) Utilisateur.Role role) {
        
        log.info("Admin: Récupération de tous les utilisateurs");
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        Page<Utilisateur> utilisateurs;
        
        if (role != null) {
            utilisateurs = utilisateurRepository.findByRole(role, pageable);
        } else {
            utilisateurs = utilisateurRepository.findAll(pageable);
        }
        
        Page<UtilisateurDto> utilisateurDtos = utilisateurs.map(this::convertirVersDto);
        
        return ResponseEntity.ok(utilisateurDtos);
    }
    
    @PatchMapping("/utilisateurs/{id}/desactiver")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Désactiver un utilisateur", 
               description = "Désactive un compte utilisateur")
    @ApiResponse(responseCode = "200", description = "Utilisateur désactivé")
    public ResponseEntity<Map<String, String>> desactiverUtilisateur(@PathVariable Long id) {
        log.info("Admin: Désactivation de l'utilisateur {}", id);
        
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        utilisateur.setActif(false);
        utilisateurRepository.save(utilisateur);
        
        return ResponseEntity.ok(Map.of("message", "Utilisateur désactivé avec succès"));
    }
    
    @PatchMapping("/utilisateurs/{id}/activer")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activer un utilisateur", 
               description = "Réactive un compte utilisateur désactivé")
    @ApiResponse(responseCode = "200", description = "Utilisateur activé")
    public ResponseEntity<Map<String, String>> activerUtilisateur(@PathVariable Long id) {
        log.info("Admin: Activation de l'utilisateur {}", id);
        
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        utilisateur.setActif(true);
        utilisateurRepository.save(utilisateur);
        
        return ResponseEntity.ok(Map.of("message", "Utilisateur activé avec succès"));
    }
    
    // ===================== STATISTIQUES =====================
    
    @GetMapping("/statistiques/dashboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
    @Operation(summary = "Obtenir les statistiques du dashboard", 
               description = "Récupère les statistiques générales du système")
    @ApiResponse(responseCode = "200", description = "Statistiques du dashboard")
    public ResponseEntity<Map<String, Object>> obtenirStatistiquesDashboard() {
        log.info("Admin: Récupération des statistiques du dashboard");
        
        Map<String, Object> stats = new HashMap<>();
        
        // Statistiques produits
        long totalProduits = produitService.compterProduits();
        long produitsActifs = produitService.compterProduitsActifs();
        long produitsEnAttente = produitService.compterProduitsEnAttente();
        
        // Statistiques vendeurs
        long totalVendeurs = vendeurService.compterVendeurs();
        long vendeursVerifies = vendeurService.compterVendeursVerifies();
        long vendeursNonVerifies = vendeurService.compterVendeursNonVerifies();
        
        // Statistiques clients
        long totalClients = clientService.compterClients();
        
        // Statistiques utilisateurs
        long totalUtilisateurs = utilisateurRepository.count();
        long utilisateursActifs = utilisateurRepository.countByActif(true);
        
        stats.put("produits", Map.of(
            "total", totalProduits,
            "actifs", produitsActifs,
            "enAttente", produitsEnAttente
        ));
        
        stats.put("vendeurs", Map.of(
            "total", totalVendeurs,
            "verifies", vendeursVerifies,
            "nonVerifies", vendeursNonVerifies
        ));
        
        stats.put("clients", Map.of(
            "total", totalClients
        ));
        
        stats.put("utilisateurs", Map.of(
            "total", totalUtilisateurs,
            "actifs", utilisateursActifs,
            "inactifs", totalUtilisateurs - utilisateursActifs
        ));
        
        return ResponseEntity.ok(stats);
    }
    
    // ===================== MÉTHODES UTILITAIRES =====================
    
    private UtilisateurDto convertirVersDto(Utilisateur utilisateur) {
        UtilisateurDto dto = new UtilisateurDto();
        dto.setId(utilisateur.getId());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setEmail(utilisateur.getEmail());
        dto.setTelephone(utilisateur.getTelephone());
        dto.setRole(utilisateur.getRole());
        dto.setActif(utilisateur.isActif());
        dto.setDateCreation(utilisateur.getDateCreation());
        dto.setDerniereConnexion(utilisateur.getDerniereConnexion());
        return dto;
    }
}

