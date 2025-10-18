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
import sn.afrizar.afrizar.dto.CommandeDto;
import sn.afrizar.afrizar.dto.CreateCommandeDto;
import sn.afrizar.afrizar.model.Commande;
import sn.afrizar.afrizar.service.CommandeService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/commandes")
@RequiredArgsConstructor
@Tag(name = "Commandes", description = "API de gestion des commandes")
public class CommandeController {
    
    private final CommandeService commandeService;
    
    @PostMapping
    @Operation(summary = "Créer une nouvelle commande", description = "Crée une nouvelle commande avec calcul automatique des totaux")
    @ApiResponse(responseCode = "201", description = "Commande créée avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<CommandeDto> creerCommande(@Valid @RequestBody CreateCommandeDto createCommandeDto) {
        log.info("Création d'une nouvelle commande pour le client ID: {}", createCommandeDto.getClientId());
        
        try {
            CommandeDto commandeCreee = commandeService.creerCommande(createCommandeDto);
            return new ResponseEntity<>(commandeCreee, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la création de la commande: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une commande par ID", description = "Récupère les détails d'une commande spécifique")
    @ApiResponse(responseCode = "200", description = "Commande trouvée")
    @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    public ResponseEntity<CommandeDto> obtenirCommande(
            @Parameter(description = "ID de la commande") @PathVariable Long id) {
        
        return commandeService.obtenirCommandeParId(id)
                .map(commande -> ResponseEntity.ok(commande))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/numero/{numeroCommande}")
    @Operation(summary = "Obtenir une commande par numéro", description = "Récupère une commande par son numéro unique")
    @ApiResponse(responseCode = "200", description = "Commande trouvée")
    @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    public ResponseEntity<CommandeDto> obtenirCommandeParNumero(
            @Parameter(description = "Numéro de la commande") @PathVariable String numeroCommande) {
        
        return commandeService.obtenirCommandeParNumero(numeroCommande)
                .map(commande -> ResponseEntity.ok(commande))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/client/{clientId}")
    @Operation(summary = "Obtenir les commandes d'un client", description = "Récupère toutes les commandes d'un client avec pagination")
    @ApiResponse(responseCode = "200", description = "Commandes du client récupérées")
    public ResponseEntity<Page<CommandeDto>> obtenirCommandesParClient(
            @Parameter(description = "ID du client") @PathVariable Long clientId,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        Page<CommandeDto> commandes = commandeService.obtenirCommandesParClientAvecPagination(clientId, pageable);
        
        return ResponseEntity.ok(commandes);
    }
    
    @GetMapping("/vendeur/{vendeurId}")
    @Operation(summary = "Obtenir les commandes d'un vendeur", description = "Récupère toutes les commandes contenant des produits d'un vendeur")
    @ApiResponse(responseCode = "200", description = "Commandes du vendeur récupérées")
    public ResponseEntity<Page<CommandeDto>> obtenirCommandesParVendeur(
            @Parameter(description = "ID du vendeur") @PathVariable Long vendeurId,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        Page<CommandeDto> commandes = commandeService.obtenirCommandesParVendeurAvecPagination(vendeurId, pageable);
        
        return ResponseEntity.ok(commandes);
    }
    
    @GetMapping("/statut/{statut}")
    @Operation(summary = "Obtenir les commandes par statut", description = "Récupère toutes les commandes ayant un statut spécifique")
    @ApiResponse(responseCode = "200", description = "Commandes du statut récupérées")
    public ResponseEntity<List<CommandeDto>> obtenirCommandesParStatut(
            @Parameter(description = "Statut des commandes") @PathVariable Commande.StatutCommande statut) {
        
        List<CommandeDto> commandes = commandeService.obtenirCommandesParStatut(statut);
        return ResponseEntity.ok(commandes);
    }
    
    @PatchMapping("/{id}/statut")
    @Operation(summary = "Changer le statut d'une commande", description = "Met à jour le statut d'une commande")
    @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    public ResponseEntity<CommandeDto> changerStatutCommande(
            @Parameter(description = "ID de la commande") @PathVariable Long id,
            @Parameter(description = "Nouveau statut") @RequestParam Commande.StatutCommande statut) {
        
        try {
            CommandeDto commandeMiseAJour = commandeService.changerStatutCommande(id, statut);
            return ResponseEntity.ok(commandeMiseAJour);
        } catch (RuntimeException e) {
            log.error("Erreur lors du changement de statut de la commande {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/confirmer")
    @Operation(summary = "Confirmer une commande", description = "Confirme une commande en attente")
    @ApiResponse(responseCode = "200", description = "Commande confirmée avec succès")
    @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    public ResponseEntity<CommandeDto> confirmerCommande(
            @Parameter(description = "ID de la commande") @PathVariable Long id) {
        
        try {
            CommandeDto commandeConfirmee = commandeService.confirmerCommande(id);
            return ResponseEntity.ok(commandeConfirmee);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la confirmation de la commande {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/annuler")
    @Operation(summary = "Annuler une commande", description = "Annule une commande avec un motif")
    @ApiResponse(responseCode = "200", description = "Commande annulée avec succès")
    @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    public ResponseEntity<CommandeDto> annulerCommande(
            @Parameter(description = "ID de la commande") @PathVariable Long id,
            @Parameter(description = "Motif d'annulation") @RequestParam(required = false) String motif) {
        
        try {
            CommandeDto commandeAnnulee = commandeService.annulerCommande(id, motif);
            return ResponseEntity.ok(commandeAnnulee);
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'annulation de la commande {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/expedier")
    @Operation(summary = "Expédier une commande", description = "Marque une commande comme expédiée avec numéro de suivi")
    @ApiResponse(responseCode = "200", description = "Commande expédiée avec succès")
    @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    public ResponseEntity<CommandeDto> expedierCommande(
            @Parameter(description = "ID de la commande") @PathVariable Long id,
            @Parameter(description = "Numéro de suivi") @RequestParam String numeroSuivi,
            @Parameter(description = "Transporteur") @RequestParam String transporteur) {
        
        try {
            CommandeDto commandeExpediee = commandeService.expedierCommande(id, numeroSuivi, transporteur);
            return ResponseEntity.ok(commandeExpediee);
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'expédition de la commande {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/livrer")
    @Operation(summary = "Marquer une commande comme livrée", description = "Marque une commande comme livrée")
    @ApiResponse(responseCode = "200", description = "Commande marquée comme livrée")
    @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    public ResponseEntity<CommandeDto> livrerCommande(
            @Parameter(description = "ID de la commande") @PathVariable Long id) {
        
        try {
            CommandeDto commandeLivree = commandeService.livrerCommande(id);
            return ResponseEntity.ok(commandeLivree);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la livraison de la commande {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/en-retard")
    @Operation(summary = "Obtenir les commandes en retard", description = "Récupère toutes les commandes en retard de livraison")
    @ApiResponse(responseCode = "200", description = "Commandes en retard récupérées")
    public ResponseEntity<List<CommandeDto>> obtenirCommandesEnRetard() {
        List<CommandeDto> commandesEnRetard = commandeService.obtenirCommandesEnRetard();
        return ResponseEntity.ok(commandesEnRetard);
    }
    
    @GetMapping("/periode")
    @Operation(summary = "Obtenir les commandes d'une période", description = "Récupère les commandes créées dans une période donnée")
    @ApiResponse(responseCode = "200", description = "Commandes de la période récupérées")
    public ResponseEntity<List<CommandeDto>> obtenirCommandesParPeriode(
            @Parameter(description = "Date de début (ISO format)") @RequestParam LocalDateTime debut,
            @Parameter(description = "Date de fin (ISO format)") @RequestParam LocalDateTime fin) {
        
        List<CommandeDto> commandes = commandeService.obtenirCommandesParPeriode(debut, fin);
        return ResponseEntity.ok(commandes);
    }
    
    @PostMapping("/calculer-totaux")
    @Operation(summary = "Calculer les totaux d'une commande", description = "Calcule les totaux d'une commande avant création")
    @ApiResponse(responseCode = "200", description = "Totaux calculés")
    public ResponseEntity<CommandeDto> calculerTotauxCommande(@Valid @RequestBody CreateCommandeDto createCommandeDto) {
        try {
            CommandeDto commandeAvecTotaux = commandeService.calculerTotauxCommande(createCommandeDto);
            return ResponseEntity.ok(commandeAvecTotaux);
        } catch (RuntimeException e) {
            log.error("Erreur lors du calcul des totaux: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Endpoints de statistiques
    @GetMapping("/statistiques/chiffre-affaires")
    @Operation(summary = "Obtenir le chiffre d'affaires total", description = "Récupère le chiffre d'affaires total")
    @ApiResponse(responseCode = "200", description = "Chiffre d'affaires récupéré")
    public ResponseEntity<BigDecimal> getTotalChiffreAffaires() {
        BigDecimal total = commandeService.getTotalChiffreAffaires();
        return ResponseEntity.ok(total != null ? total : BigDecimal.ZERO);
    }
    
    @GetMapping("/statistiques/chiffre-affaires/depuis")
    @Operation(summary = "Obtenir le chiffre d'affaires depuis une date", description = "Récupère le chiffre d'affaires depuis une date donnée")
    @ApiResponse(responseCode = "200", description = "Chiffre d'affaires récupéré")
    public ResponseEntity<BigDecimal> getChiffreAffairesDepuis(
            @Parameter(description = "Date de début") @RequestParam LocalDateTime depuis) {
        
        BigDecimal total = commandeService.getChiffreAffairesDepuis(depuis);
        return ResponseEntity.ok(total != null ? total : BigDecimal.ZERO);
    }
    
    @GetMapping("/statistiques/panier-moyen")
    @Operation(summary = "Obtenir le panier moyen", description = "Récupère la valeur moyenne des commandes")
    @ApiResponse(responseCode = "200", description = "Panier moyen récupéré")
    public ResponseEntity<BigDecimal> getMoyennePanier() {
        BigDecimal moyenne = commandeService.getMoyennePanier();
        return ResponseEntity.ok(moyenne != null ? moyenne : BigDecimal.ZERO);
    }
    
    @GetMapping("/statistiques/count/{statut}")
    @Operation(summary = "Compter les commandes par statut", description = "Compte le nombre de commandes ayant un statut spécifique")
    @ApiResponse(responseCode = "200", description = "Nombre de commandes récupéré")
    public ResponseEntity<Long> getNombreCommandesParStatut(
            @Parameter(description = "Statut des commandes") @PathVariable Commande.StatutCommande statut) {
        
        Long nombre = commandeService.getNombreCommandesParStatut(statut);
        return ResponseEntity.ok(nombre != null ? nombre : 0L);
    }
}

