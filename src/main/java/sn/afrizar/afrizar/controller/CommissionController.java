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
import sn.afrizar.afrizar.model.Commission;
import sn.afrizar.afrizar.service.CommissionService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/commissions")
@RequiredArgsConstructor
@Tag(name = "Commissions", description = "API de gestion des tranches de commission")
public class CommissionController {
    
    private final CommissionService commissionService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer une nouvelle tranche de commission", description = "Crée une nouvelle tranche de commission pour une plage de montants")
    @ApiResponse(responseCode = "201", description = "Tranche de commission créée avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides ou conflit de plage")
    public ResponseEntity<Commission> creerCommission(@Valid @RequestBody Commission commission) {
        log.info("Création d'une nouvelle tranche de commission: {} - {}%", 
                commission.getSeuilMin(), commission.getPourcentage());
        
        try {
            Commission commissionCreee = commissionService.creerCommission(commission);
            return new ResponseEntity<>(commissionCreee, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la création de la tranche de commission: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir toutes les tranches de commission", description = "Récupère toutes les tranches de commission actives")
    @ApiResponse(responseCode = "200", description = "Liste des tranches de commission récupérée")
    public ResponseEntity<List<Commission>> obtenirToutesLesCommissions() {
        log.info("Récupération de toutes les tranches de commission");
        
        List<Commission> commissions = commissionService.obtenirToutesLesCommissionsActives();
        return ResponseEntity.ok(commissions);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir une tranche de commission par ID", description = "Récupère les détails d'une tranche de commission spécifique")
    @ApiResponse(responseCode = "200", description = "Tranche de commission trouvée")
    @ApiResponse(responseCode = "404", description = "Tranche de commission non trouvée")
    public ResponseEntity<Commission> obtenirCommission(
            @Parameter(description = "ID de la tranche de commission") @PathVariable Long id) {
        
        return commissionService.obtenirCommissionParId(id)
                .map(commission -> ResponseEntity.ok(commission))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour une tranche de commission", description = "Met à jour une tranche de commission existante")
    @ApiResponse(responseCode = "200", description = "Tranche de commission mise à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Tranche de commission non trouvée")
    public ResponseEntity<Commission> mettreAJourCommission(
            @Parameter(description = "ID de la tranche de commission") @PathVariable Long id,
            @Valid @RequestBody Commission commission) {
        
        log.info("Mise à jour de la tranche de commission ID: {}", id);
        
        try {
            Commission commissionModifiee = commissionService.mettreAJourCommission(id, commission);
            return ResponseEntity.ok(commissionModifiee);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour de la tranche de commission {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une tranche de commission", description = "Supprime une tranche de commission (désactivation)")
    @ApiResponse(responseCode = "204", description = "Tranche de commission supprimée avec succès")
    @ApiResponse(responseCode = "404", description = "Tranche de commission non trouvée")
    public ResponseEntity<Void> supprimerCommission(
            @Parameter(description = "ID de la tranche de commission") @PathVariable Long id) {
        
        log.info("Suppression de la tranche de commission ID: {}", id);
        
        try {
            commissionService.supprimerCommission(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression de la tranche de commission {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/activer")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activer une tranche de commission", description = "Active une tranche de commission désactivée")
    @ApiResponse(responseCode = "200", description = "Tranche de commission activée avec succès")
    @ApiResponse(responseCode = "404", description = "Tranche de commission non trouvée")
    public ResponseEntity<Commission> activerCommission(
            @Parameter(description = "ID de la tranche de commission") @PathVariable Long id) {
        
        log.info("Activation de la tranche de commission ID: {}", id);
        
        try {
            Commission commissionActivee = commissionService.activerCommission(id);
            return ResponseEntity.ok(commissionActivee);
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'activation de la tranche de commission {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/desactiver")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Désactiver une tranche de commission", description = "Désactive une tranche de commission active")
    @ApiResponse(responseCode = "200", description = "Tranche de commission désactivée avec succès")
    @ApiResponse(responseCode = "404", description = "Tranche de commission non trouvée")
    public ResponseEntity<Commission> desactiverCommission(
            @Parameter(description = "ID de la tranche de commission") @PathVariable Long id) {
        
        log.info("Désactivation de la tranche de commission ID: {}", id);
        
        try {
            Commission commissionDesactivee = commissionService.desactiverCommission(id);
            return ResponseEntity.ok(commissionDesactivee);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la désactivation de la tranche de commission {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/simulation")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Simuler une commission", description = "Simule le calcul de commission pour un montant donné")
    @ApiResponse(responseCode = "200", description = "Simulation de commission effectuée")
    public ResponseEntity<Map<String, Object>> simulerCommission(
            @Parameter(description = "Montant à simuler") @RequestParam BigDecimal montant) {
        
        log.info("Simulation de commission pour le montant: {}", montant);
        
        try {
            Map<String, Object> simulation = commissionService.simulerCommission(montant);
            return ResponseEntity.ok(simulation);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la simulation de commission: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/statistiques")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir les statistiques des commissions", description = "Récupère les statistiques d'utilisation des tranches de commission")
    @ApiResponse(responseCode = "200", description = "Statistiques récupérées")
    public ResponseEntity<Map<String, Object>> obtenirStatistiques() {
        log.info("Récupération des statistiques des commissions");
        
        try {
            Map<String, Object> statistiques = commissionService.obtenirStatistiques();
            return ResponseEntity.ok(statistiques);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la récupération des statistiques: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}