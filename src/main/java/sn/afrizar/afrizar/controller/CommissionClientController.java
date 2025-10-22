package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.CommissionClientDto;
import sn.afrizar.afrizar.dto.SoldeCommissionDto;
import sn.afrizar.afrizar.service.CommissionClientService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/commissions-clients")
@RequiredArgsConstructor
@Tag(name = "Commissions Clients", description = "API de gestion des commissions des clients")
public class CommissionClientController {
    
    private final CommissionClientService commissionClientService;
    
    @GetMapping("/client")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Obtenir les commissions du client connecté", description = "Récupère toutes les commissions du client connecté")
    @ApiResponse(responseCode = "200", description = "Liste des commissions récupérée")
    @ApiResponse(responseCode = "401", description = "Non authentifié")
    public ResponseEntity<List<CommissionClientDto>> obtenirMesCommissions() {
        // TODO: Récupérer l'ID du client connecté depuis le token JWT
        // Pour l'instant, on utilise un ID par défaut
        Long clientId = 1L; // À remplacer par l'ID du client connecté
        
        log.info("Récupération des commissions pour le client ID: {}", clientId);
        
        List<CommissionClientDto> commissions = commissionClientService.obtenirCommissionsParClient(clientId);
        return ResponseEntity.ok(commissions);
    }
    
    @GetMapping("/solde")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Obtenir le solde de commission du client", description = "Récupère le solde total des commissions du client")
    @ApiResponse(responseCode = "200", description = "Solde de commission récupéré")
    @ApiResponse(responseCode = "401", description = "Non authentifié")
    public ResponseEntity<SoldeCommissionDto> obtenirSoldeCommission() {
        // TODO: Récupérer l'ID du client connecté depuis le token JWT
        Long clientId = 1L; // À remplacer par l'ID du client connecté
        
        log.info("Récupération du solde de commission pour le client ID: {}", clientId);
        
        SoldeCommissionDto solde = commissionClientService.obtenirSoldeCommission(clientId);
        return ResponseEntity.ok(solde);
    }
    
    @PostMapping("/retrait")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Demander un retrait de commission", description = "Demande un retrait de commission pour le client")
    @ApiResponse(responseCode = "200", description = "Demande de retrait créée")
    @ApiResponse(responseCode = "400", description = "Montant insuffisant")
    public ResponseEntity<Map<String, Object>> demanderRetrait(@RequestParam BigDecimal montant) {
        // TODO: Récupérer l'ID du client connecté depuis le token JWT
        Long clientId = 1L; // À remplacer par l'ID du client connecté
        
        log.info("Demande de retrait de {} FCFA pour le client ID: {}", montant, clientId);
        
        // Vérifier le solde disponible
        SoldeCommissionDto solde = commissionClientService.obtenirSoldeCommission(clientId);
        if (solde.getSoldeDisponible().compareTo(montant) < 0) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Solde insuffisant"
            ));
        }
        
        // TODO: Créer la demande de retrait dans la base de données
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Demande de retrait créée avec succès"
        ));
    }
    
    @GetMapping("/retraits")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Obtenir l'historique des retraits", description = "Récupère l'historique des retraits du client")
    @ApiResponse(responseCode = "200", description = "Historique des retraits récupéré")
    public ResponseEntity<List<Map<String, Object>>> obtenirHistoriqueRetraits() {
        // TODO: Récupérer l'ID du client connecté depuis le token JWT
        Long clientId = 1L; // À remplacer par l'ID du client connecté
        
        log.info("Récupération de l'historique des retraits pour le client ID: {}", clientId);
        
        // TODO: Récupérer l'historique des retraits depuis la base de données
        List<Map<String, Object>> historique = List.of(); // Placeholder
        
        return ResponseEntity.ok(historique);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Obtenir une commission par ID", description = "Récupère les détails d'une commission spécifique")
    @ApiResponse(responseCode = "200", description = "Commission trouvée")
    @ApiResponse(responseCode = "404", description = "Commission non trouvée")
    public ResponseEntity<CommissionClientDto> obtenirCommission(
            @Parameter(description = "ID de la commission") @PathVariable Long id) {
        
        return commissionClientService.obtenirCommissionClientParId(id)
                .map(commission -> ResponseEntity.ok(commission))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/statut/{statut}")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Obtenir les commissions par statut", description = "Récupère les commissions du client selon leur statut")
    @ApiResponse(responseCode = "200", description = "Liste des commissions filtrée")
    public ResponseEntity<List<CommissionClientDto>> obtenirCommissionsParStatut(
            @Parameter(description = "Statut de la commission") @PathVariable String statut) {
        
        // TODO: Récupérer l'ID du client connecté depuis le token JWT
        Long clientId = 1L; // À remplacer par l'ID du client connecté
        
        log.info("Récupération des commissions pour le client ID: {} avec statut: {}", clientId, statut);
        
        List<CommissionClientDto> commissions = commissionClientService.obtenirCommissionsParClientEtStatut(clientId, statut);
        return ResponseEntity.ok(commissions);
    }
    
    // Endpoints pour l'admin
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer une commission client", description = "Crée une nouvelle commission pour un client (Admin uniquement)")
    @ApiResponse(responseCode = "201", description = "Commission créée avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<CommissionClientDto> creerCommissionClient(@Valid @RequestBody CommissionClientDto commissionClientDto) {
        log.info("Création d'une nouvelle commission client: {}", commissionClientDto.getClientId());
        
        try {
            CommissionClientDto commissionCreee = commissionClientService.creerCommissionClient(commissionClientDto);
            return new ResponseEntity<>(commissionCreee, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la création de la commission: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PatchMapping("/{id}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Valider une commission", description = "Valide une commission (Admin uniquement)")
    @ApiResponse(responseCode = "200", description = "Commission validée avec succès")
    @ApiResponse(responseCode = "404", description = "Commission non trouvée")
    public ResponseEntity<CommissionClientDto> validerCommission(
            @Parameter(description = "ID de la commission") @PathVariable Long id) {
        
        log.info("Validation de la commission ID: {}", id);
        
        try {
            CommissionClientDto commissionValidee = commissionClientService.validerCommission(id);
            return ResponseEntity.ok(commissionValidee);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la validation de la commission {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/payer")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Payer une commission", description = "Marque une commission comme payée (Admin uniquement)")
    @ApiResponse(responseCode = "200", description = "Commission payée avec succès")
    @ApiResponse(responseCode = "404", description = "Commission non trouvée")
    public ResponseEntity<CommissionClientDto> payerCommission(
            @Parameter(description = "ID de la commission") @PathVariable Long id) {
        
        log.info("Paiement de la commission ID: {}", id);
        
        try {
            CommissionClientDto commissionPayee = commissionClientService.payerCommission(id);
            return ResponseEntity.ok(commissionPayee);
        } catch (RuntimeException e) {
            log.error("Erreur lors du paiement de la commission {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une commission", description = "Supprime une commission (Admin uniquement)")
    @ApiResponse(responseCode = "204", description = "Commission supprimée avec succès")
    @ApiResponse(responseCode = "404", description = "Commission non trouvée")
    public ResponseEntity<Void> supprimerCommission(
            @Parameter(description = "ID de la commission") @PathVariable Long id) {
        
        log.info("Suppression de la commission ID: {}", id);
        
        try {
            commissionClientService.supprimerCommissionClient(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression de la commission {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
