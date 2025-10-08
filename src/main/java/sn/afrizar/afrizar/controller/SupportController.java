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
import sn.afrizar.afrizar.dto.ClientDto;
import sn.afrizar.afrizar.dto.CommandeDto;
import sn.afrizar.afrizar.dto.VendeurDto;
import sn.afrizar.afrizar.model.Utilisateur;
import sn.afrizar.afrizar.repository.UtilisateurRepository;
import sn.afrizar.afrizar.service.ClientService;
import sn.afrizar.afrizar.service.CommandeService;
import sn.afrizar.afrizar.service.VendeurService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
@Tag(name = "Support Client", description = "API de support client pour l'assistance utilisateurs")
@SecurityRequirement(name = "bearer-jwt")
public class SupportController {
    
    private final ClientService clientService;
    private final VendeurService vendeurService;
    private final CommandeService commandeService;
    private final UtilisateurRepository utilisateurRepository;
    
    // ===================== GESTION DES CLIENTS =====================
    
    @GetMapping("/clients")
    @PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
    @Operation(summary = "Obtenir tous les clients", 
               description = "Récupère la liste de tous les clients pour le support")
    @ApiResponse(responseCode = "200", description = "Liste des clients")
    @ApiResponse(responseCode = "403", description = "Accès refusé")
    public ResponseEntity<Page<ClientDto>> obtenirTousLesClients(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size) {
        
        log.info("Support: Récupération de tous les clients");
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        Page<ClientDto> clients = clientService.obtenirClientsAvecPagination(pageable);
        
        return ResponseEntity.ok(clients);
    }
    
    @GetMapping("/clients/{id}")
    @PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
    @Operation(summary = "Obtenir un client par ID", 
               description = "Récupère les détails d'un client spécifique")
    @ApiResponse(responseCode = "200", description = "Client trouvé")
    @ApiResponse(responseCode = "404", description = "Client non trouvé")
    public ResponseEntity<ClientDto> obtenirClient(@PathVariable Long id) {
        log.info("Support: Récupération du client {}", id);
        
        return clientService.obtenirClientParId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/clients/recherche")
    @PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
    @Operation(summary = "Rechercher des clients", 
               description = "Recherche des clients par email ou par pays")
    @ApiResponse(responseCode = "200", description = "Résultats de recherche")
    public ResponseEntity<List<ClientDto>> rechercherClients(
            @Parameter(description = "Email du client") @RequestParam(required = false) String email,
            @Parameter(description = "Pays") @RequestParam(required = false) String pays) {
        
        log.info("Support: Recherche de clients - email: {}, pays: {}", email, pays);
        
        if (email != null) {
            return clientService.obtenirClientParEmail(email)
                    .map(client -> ResponseEntity.ok(List.of(client)))
                    .orElse(ResponseEntity.ok(List.of()));
        } else if (pays != null) {
            List<ClientDto> clients = clientService.obtenirClientsParPays(pays);
            return ResponseEntity.ok(clients);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // ===================== GESTION DES VENDEURS =====================
    
    @GetMapping("/vendeurs")
    @PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
    @Operation(summary = "Obtenir tous les vendeurs", 
               description = "Récupère la liste de tous les vendeurs pour le support")
    @ApiResponse(responseCode = "200", description = "Liste des vendeurs")
    public ResponseEntity<Page<VendeurDto>> obtenirTousLesVendeurs(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size) {
        
        log.info("Support: Récupération de tous les vendeurs");
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        Page<VendeurDto> vendeurs = vendeurService.obtenirVendeursAvecPagination(pageable);
        
        return ResponseEntity.ok(vendeurs);
    }
    
    @GetMapping("/vendeurs/{id}")
    @PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
    @Operation(summary = "Obtenir un vendeur par ID", 
               description = "Récupère les détails d'un vendeur spécifique")
    @ApiResponse(responseCode = "200", description = "Vendeur trouvé")
    @ApiResponse(responseCode = "404", description = "Vendeur non trouvé")
    public ResponseEntity<VendeurDto> obtenirVendeur(@PathVariable Long id) {
        log.info("Support: Récupération du vendeur {}", id);
        
        return vendeurService.obtenirVendeurParId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/vendeurs/recherche")
    @PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
    @Operation(summary = "Rechercher des vendeurs", 
               description = "Recherche des vendeurs par nom de boutique ou spécialités")
    @ApiResponse(responseCode = "200", description = "Résultats de recherche")
    public ResponseEntity<List<VendeurDto>> rechercherVendeurs(
            @Parameter(description = "Terme de recherche") @RequestParam String terme) {
        
        log.info("Support: Recherche de vendeurs - terme: {}", terme);
        
        List<VendeurDto> vendeurs = vendeurService.rechercherVendeurs(terme);
        return ResponseEntity.ok(vendeurs);
    }
    
    // ===================== GESTION DES COMMANDES =====================
    
    @GetMapping("/commandes")
    @PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
    @Operation(summary = "Obtenir toutes les commandes", 
               description = "Récupère la liste de toutes les commandes pour le support")
    @ApiResponse(responseCode = "200", description = "Liste des commandes")
    public ResponseEntity<Page<CommandeDto>> obtenirToutesLesCommandes(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size) {
        
        log.info("Support: Récupération de toutes les commandes");
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCommande").descending());
        Page<CommandeDto> commandes = commandeService.obtenirToutesLesCommandesAvecPagination(pageable);
        
        return ResponseEntity.ok(commandes);
    }
    
    @GetMapping("/commandes/{id}")
    @PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
    @Operation(summary = "Obtenir une commande par ID", 
               description = "Récupère les détails d'une commande spécifique")
    @ApiResponse(responseCode = "200", description = "Commande trouvée")
    @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    public ResponseEntity<CommandeDto> obtenirCommande(@PathVariable Long id) {
        log.info("Support: Récupération de la commande {}", id);
        
        return commandeService.obtenirCommandeParId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/commandes/client/{clientId}")
    @PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
    @Operation(summary = "Obtenir les commandes d'un client", 
               description = "Récupère toutes les commandes d'un client spécifique")
    @ApiResponse(responseCode = "200", description = "Commandes du client")
    public ResponseEntity<List<CommandeDto>> obtenirCommandesParClient(@PathVariable Long clientId) {
        log.info("Support: Récupération des commandes du client {}", clientId);
        
        List<CommandeDto> commandes = commandeService.obtenirCommandesParClient(clientId);
        return ResponseEntity.ok(commandes);
    }
    
    // ===================== STATISTIQUES =====================
    
    @GetMapping("/statistiques/clients")
    @PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
    @Operation(summary = "Obtenir les statistiques clients", 
               description = "Récupère des statistiques sur les clients")
    @ApiResponse(responseCode = "200", description = "Statistiques clients")
    public ResponseEntity<Map<String, Object>> obtenirStatistiquesClients() {
        log.info("Support: Récupération des statistiques clients");
        
        Map<String, Object> stats = new HashMap<>();
        
        long totalClients = clientService.compterClients();
        Double moyennePointsFidelite = clientService.getMoyennePointsFidelite();
        
        stats.put("totalClients", totalClients);
        stats.put("moyennePointsFidelite", moyennePointsFidelite);
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/statistiques/vendeurs")
    @PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
    @Operation(summary = "Obtenir les statistiques vendeurs", 
               description = "Récupère des statistiques sur les vendeurs")
    @ApiResponse(responseCode = "200", description = "Statistiques vendeurs")
    public ResponseEntity<Map<String, Object>> obtenirStatistiquesVendeurs() {
        log.info("Support: Récupération des statistiques vendeurs");
        
        Map<String, Object> stats = new HashMap<>();
        
        long totalVendeurs = vendeurService.compterVendeurs();
        long vendeursVerifies = vendeurService.compterVendeursVerifies();
        long vendeursNonVerifies = vendeurService.compterVendeursNonVerifies();
        
        stats.put("totalVendeurs", totalVendeurs);
        stats.put("vendeursVerifies", vendeursVerifies);
        stats.put("vendeursNonVerifies", vendeursNonVerifies);
        
        return ResponseEntity.ok(stats);
    }
    
    // ===================== RECHERCHE GLOBALE =====================
    
    @GetMapping("/recherche/utilisateur")
    @PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
    @Operation(summary = "Rechercher un utilisateur par email", 
               description = "Recherche un utilisateur (client ou vendeur) par son email")
    @ApiResponse(responseCode = "200", description = "Utilisateur trouvé")
    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    public ResponseEntity<Map<String, Object>> rechercherUtilisateurParEmail(
            @Parameter(description = "Email de l'utilisateur") @RequestParam String email) {
        
        log.info("Support: Recherche d'utilisateur par email: {}", email);
        
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElse(null);
        
        if (utilisateur == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", utilisateur.getId());
        response.put("nom", utilisateur.getNom());
        response.put("prenom", utilisateur.getPrenom());
        response.put("email", utilisateur.getEmail());
        response.put("telephone", utilisateur.getTelephone());
        response.put("role", utilisateur.getRole());
        response.put("actif", utilisateur.isActif());
        response.put("dateCreation", utilisateur.getDateCreation());
        response.put("derniereConnexion", utilisateur.getDerniereConnexion());
        
        return ResponseEntity.ok(response);
    }
    
    // ===================== ACTIONS SUPPORT =====================
    
    @PatchMapping("/clients/{id}/reinitialiser-points")
    @PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
    @Operation(summary = "Réinitialiser les points de fidélité", 
               description = "Réinitialise les points de fidélité d'un client à zéro")
    @ApiResponse(responseCode = "200", description = "Points réinitialisés")
    @ApiResponse(responseCode = "404", description = "Client non trouvé")
    public ResponseEntity<Map<String, String>> reinitialiserPointsFidelite(@PathVariable Long id) {
        log.info("Support: Réinitialisation des points de fidélité du client {}", id);
        
        try {
            // Utiliser les points pour les ramener à 0
            ClientDto client = clientService.obtenirClientParId(id)
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));
            
            if (client.getPointsFidelite() > 0) {
                clientService.utiliserPointsFidelite(id, client.getPointsFidelite());
            }
            
            return ResponseEntity.ok(Map.of("message", "Points de fidélité réinitialisés avec succès"));
        } catch (RuntimeException e) {
            log.error("Erreur lors de la réinitialisation des points: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/clients/{id}/ajouter-points")
    @PreAuthorize("hasRole('SUPPORT') or hasRole('ADMIN')")
    @Operation(summary = "Ajouter des points de fidélité", 
               description = "Ajoute des points de fidélité à un client (compensation)")
    @ApiResponse(responseCode = "200", description = "Points ajoutés")
    @ApiResponse(responseCode = "404", description = "Client non trouvé")
    public ResponseEntity<ClientDto> ajouterPointsFidelite(
            @PathVariable Long id,
            @RequestParam Integer points) {
        
        log.info("Support: Ajout de {} points au client {}", points, id);
        
        try {
            ClientDto client = clientService.ajouterPointsFidelite(id, points);
            return ResponseEntity.ok(client);
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'ajout des points: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
