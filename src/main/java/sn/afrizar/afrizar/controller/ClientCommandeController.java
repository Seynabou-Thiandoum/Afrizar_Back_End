package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.CommandeDto;
import sn.afrizar.afrizar.dto.CreateCommandeDto;
import sn.afrizar.afrizar.model.Client;
import sn.afrizar.afrizar.model.Utilisateur;
import sn.afrizar.afrizar.service.CommandeService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/client/commandes")
@RequiredArgsConstructor
@Tag(name = "Commandes Client", description = "API de gestion des commandes côté client")
@SecurityRequirement(name = "bearer-jwt")
public class ClientCommandeController {
    
    private final CommandeService commandeService;
    
    @PostMapping("/depuis-panier")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Créer une commande depuis le panier", description = "Transforme le panier en commande")
    public ResponseEntity<CommandeDto> creerCommandeDepuisPanier(
            @Valid @RequestBody CreateCommandeDto createCommandeDto,
            Authentication authentication) {
        
        Long clientId = getClientId(authentication);
        log.info("Création de commande depuis panier - Client ID: {}", clientId);
        
        CommandeDto commande = commandeService.creerCommandeDepuisPanier(clientId, createCommandeDto);
        return new ResponseEntity<>(commande, HttpStatus.CREATED);
    }
    
    @GetMapping("/mes-commandes")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Mes commandes", description = "Récupère toutes les commandes du client connecté")
    public ResponseEntity<Page<CommandeDto>> obtenirMesCommandes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        Long clientId = getClientId(authentication);
        log.info("Récupération des commandes - Client ID: {}", clientId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        Page<CommandeDto> commandes = commandeService.obtenirCommandesParClientAvecPagination(clientId, pageable);
        
        return ResponseEntity.ok(commandes);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Détails d'une commande", description = "Récupère les détails d'une commande spécifique")
    public ResponseEntity<CommandeDto> obtenirDetailsCommande(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long clientId = getClientId(authentication);
        
        return commandeService.obtenirCommandeParId(id)
                .map(commande -> {
                    // Vérifier que la commande appartient au client
                    if (!commande.getClientId().equals(clientId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<CommandeDto>build();
                    }
                    return ResponseEntity.ok(commande);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{id}/annuler")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Annuler une commande", description = "Permet au client d'annuler sa commande")
    public ResponseEntity<CommandeDto> annulerCommande(
            @PathVariable Long id,
            @RequestBody(required = false) String motif,
            Authentication authentication) {
        
        Long clientId = getClientId(authentication);
        log.info("Annulation de commande - Client: {}, Commande: {}", clientId, id);
        
        // Vérifier que la commande appartient au client
        Optional<CommandeDto> commandeOpt = commandeService.obtenirCommandeParId(id);
        if (commandeOpt.isEmpty() || !commandeOpt.get().getClientId().equals(clientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        CommandeDto commande = commandeService.annulerCommande(id, motif);
        return ResponseEntity.ok(commande);
    }
    
    // ===================== MÉTHODE UTILITAIRE =====================
    
    private Long getClientId(Authentication authentication) {
        Utilisateur utilisateur = (Utilisateur) authentication.getPrincipal();
        
        if (!(utilisateur instanceof Client)) {
            throw new RuntimeException("Seuls les clients peuvent passer des commandes");
        }
        
        return utilisateur.getId();
    }
}

