package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.model.Commission;
import sn.afrizar.afrizar.service.CommissionService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/commissions")
@RequiredArgsConstructor
@Tag(name = "Commissions", description = "API de gestion des commissions (Admin uniquement)")
public class CommissionController {
    
    private final CommissionService commissionService;
    
    @Operation(summary = "Obtenir toutes les tranches de commission actives")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Commission>> obtenirToutesLesCommissions() {
        log.info("Récupération de toutes les tranches de commission actives");
        List<Commission> commissions = commissionService.obtenirToutesLesCommissionsActives();
        return ResponseEntity.ok(commissions);
    }
    
    @Operation(summary = "Obtenir une commission par ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Commission> obtenirCommissionParId(@PathVariable Long id) {
        log.info("Récupération de la commission avec ID: {}", id);
        return commissionService.obtenirCommissionParId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Créer une nouvelle tranche de commission")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Commission> creerCommission(@RequestBody Commission commission) {
        log.info("Création d'une nouvelle tranche de commission");
        try {
            Commission nouvelleCommission = commissionService.creerCommission(commission);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleCommission);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la création de la commission: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Mettre à jour une tranche de commission")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Commission> mettreAJourCommission(
            @PathVariable Long id,
            @RequestBody Commission commission) {
        log.info("Mise à jour de la commission avec ID: {}", id);
        try {
            Commission commissionMiseAJour = commissionService.mettreAJourCommission(id, commission);
            return ResponseEntity.ok(commissionMiseAJour);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour de la commission: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Supprimer une tranche de commission")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimerCommission(@PathVariable Long id) {
        log.info("Suppression de la commission avec ID: {}", id);
        try {
            commissionService.supprimerCommission(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression de la commission: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Activer une tranche de commission")
    @PatchMapping("/{id}/activer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activerCommission(@PathVariable Long id) {
        log.info("Activation de la commission avec ID: {}", id);
        try {
            commissionService.activerCommission(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'activation de la commission: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Désactiver une tranche de commission")
    @PatchMapping("/{id}/desactiver")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desactiverCommission(@PathVariable Long id) {
        log.info("Désactivation de la commission avec ID: {}", id);
        try {
            commissionService.desactiverCommission(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la désactivation de la commission: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Calculer la commission pour un montant donné")
    @GetMapping("/calculer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> calculerCommission(
            @RequestParam BigDecimal montant) {
        log.info("Calcul de la commission pour le montant: {}", montant);
        
        BigDecimal commission = commissionService.calculerCommission(montant);
        
        Map<String, Object> response = new HashMap<>();
        response.put("montant", montant);
        response.put("commission", commission);
        response.put("total", montant.add(commission));
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Initialiser les tranches de commission par défaut")
    @PostMapping("/initialiser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> initialiserCommissionsParDefaut() {
        log.info("Initialisation des tranches de commission par défaut");
        try {
            commissionService.initialiserCommissionsParDefaut();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Tranches de commission initialisées avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur lors de l'initialisation des commissions: {}", e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}



