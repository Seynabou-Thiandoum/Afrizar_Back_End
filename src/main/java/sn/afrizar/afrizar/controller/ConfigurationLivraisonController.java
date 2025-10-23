package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.ConfigurationLivraisonDto;
import sn.afrizar.afrizar.model.Livraison;
import sn.afrizar.afrizar.service.ConfigurationLivraisonService;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/configurations-livraison")
@RequiredArgsConstructor
@Tag(name = "Configurations de Livraison", description = "API de gestion des configurations de livraison")
public class ConfigurationLivraisonController {
    
    private final ConfigurationLivraisonService configurationLivraisonService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer une nouvelle configuration de livraison", 
               description = "Permet à un administrateur de créer une nouvelle configuration de livraison")
    @ApiResponse(responseCode = "200", description = "Configuration créée avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "401", description = "Non autorisé")
    public ResponseEntity<ConfigurationLivraisonDto> creerConfiguration(@RequestBody ConfigurationLivraisonDto dto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String emailAdmin = authentication.getName();
            
            log.info("Création d'une nouvelle configuration de livraison par: {}", emailAdmin);
            
            ConfigurationLivraisonDto configurationCreee = configurationLivraisonService.creerConfiguration(dto, emailAdmin);
            
            return ResponseEntity.ok(configurationCreee);
        } catch (Exception e) {
            log.error("Erreur lors de la création de la configuration: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour une configuration de livraison", 
               description = "Permet à un administrateur de modifier une configuration existante")
    @ApiResponse(responseCode = "200", description = "Configuration mise à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Configuration non trouvée")
    @ApiResponse(responseCode = "401", description = "Non autorisé")
    public ResponseEntity<ConfigurationLivraisonDto> mettreAJourConfiguration(
            @PathVariable Long id, 
            @RequestBody ConfigurationLivraisonDto dto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String emailAdmin = authentication.getName();
            
            log.info("Mise à jour de la configuration ID: {} par: {}", id, emailAdmin);
            
            ConfigurationLivraisonDto configurationMiseAJour = configurationLivraisonService.mettreAJourConfiguration(id, dto, emailAdmin);
            
            return ResponseEntity.ok(configurationMiseAJour);
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour de la configuration: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir toutes les configurations de livraison", 
               description = "Récupère toutes les configurations (actives et inactives)")
    @ApiResponse(responseCode = "200", description = "Configurations récupérées avec succès")
    @ApiResponse(responseCode = "401", description = "Non autorisé")
    public ResponseEntity<List<ConfigurationLivraisonDto>> obtenirToutesConfigurations() {
        try {
            List<ConfigurationLivraisonDto> configurations = configurationLivraisonService.obtenirToutesConfigurations();
            return ResponseEntity.ok(configurations);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des configurations: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/actives")
    @Operation(summary = "Obtenir les configurations actives", 
               description = "Récupère uniquement les configurations actives (endpoint public)")
    @ApiResponse(responseCode = "200", description = "Configurations actives récupérées avec succès")
    public ResponseEntity<List<ConfigurationLivraisonDto>> obtenirConfigurationsActives() {
        try {
            List<ConfigurationLivraisonDto> configurations = configurationLivraisonService.obtenirConfigurationsActives();
            return ResponseEntity.ok(configurations);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des configurations actives: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/pays/{pays}")
    @Operation(summary = "Obtenir les configurations par pays", 
               description = "Récupère les configurations pour un pays spécifique")
    @ApiResponse(responseCode = "200", description = "Configurations récupérées avec succès")
    public ResponseEntity<List<ConfigurationLivraisonDto>> obtenirConfigurationsParPays(@PathVariable String pays) {
        try {
            List<ConfigurationLivraisonDto> configurations = configurationLivraisonService.obtenirConfigurationsParPays(pays);
            return ResponseEntity.ok(configurations);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des configurations pour le pays {}: {}", pays, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/type/{type}")
    @Operation(summary = "Obtenir les configurations par type", 
               description = "Récupère les configurations pour un type de livraison spécifique")
    @ApiResponse(responseCode = "200", description = "Configurations récupérées avec succès")
    public ResponseEntity<List<ConfigurationLivraisonDto>> obtenirConfigurationsParType(@PathVariable Livraison.TypeLivraison type) {
        try {
            List<ConfigurationLivraisonDto> configurations = configurationLivraisonService.obtenirConfigurationsParType(type);
            return ResponseEntity.ok(configurations);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des configurations pour le type {}: {}", type, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une configuration par ID", 
               description = "Récupère une configuration spécifique par son ID")
    @ApiResponse(responseCode = "200", description = "Configuration récupérée avec succès")
    @ApiResponse(responseCode = "404", description = "Configuration non trouvée")
    public ResponseEntity<ConfigurationLivraisonDto> obtenirConfigurationParId(@PathVariable Long id) {
        try {
            ConfigurationLivraisonDto configuration = configurationLivraisonService.obtenirConfigurationParId(id);
            return ResponseEntity.ok(configuration);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de la configuration ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/toggle-actif")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activer/désactiver une configuration", 
               description = "Permet à un administrateur d'activer ou désactiver une configuration")
    @ApiResponse(responseCode = "200", description = "Statut modifié avec succès")
    @ApiResponse(responseCode = "404", description = "Configuration non trouvée")
    @ApiResponse(responseCode = "401", description = "Non autorisé")
    public ResponseEntity<ConfigurationLivraisonDto> toggleActif(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String emailAdmin = authentication.getName();
            
            log.info("Changement du statut actif pour la configuration ID: {} par: {}", id, emailAdmin);
            
            ConfigurationLivraisonDto configuration = configurationLivraisonService.toggleActif(id, emailAdmin);
            
            return ResponseEntity.ok(configuration);
        } catch (Exception e) {
            log.error("Erreur lors du changement de statut de la configuration: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une configuration", 
               description = "Permet à un administrateur de supprimer une configuration")
    @ApiResponse(responseCode = "200", description = "Configuration supprimée avec succès")
    @ApiResponse(responseCode = "404", description = "Configuration non trouvée")
    @ApiResponse(responseCode = "401", description = "Non autorisé")
    public ResponseEntity<Void> supprimerConfiguration(@PathVariable Long id) {
        try {
            log.info("Suppression de la configuration ID: {}", id);
            
            configurationLivraisonService.supprimerConfiguration(id);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de la configuration: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/initialiser-par-defaut")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Initialiser les configurations par défaut", 
               description = "Crée les configurations de livraison par défaut pour le système")
    @ApiResponse(responseCode = "200", description = "Configurations initialisées avec succès")
    @ApiResponse(responseCode = "401", description = "Non autorisé")
    public ResponseEntity<Void> initialiserConfigurationsParDefaut() {
        try {
            log.info("Initialisation des configurations de livraison par défaut");
            
            configurationLivraisonService.initialiserConfigurationsParDefaut();
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Erreur lors de l'initialisation des configurations: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/calculer-tarif")
    @Operation(summary = "Calculer le tarif de livraison", 
               description = "Calcule le tarif de livraison pour un pays, type et poids donnés")
    @ApiResponse(responseCode = "200", description = "Tarif calculé avec succès")
    public ResponseEntity<BigDecimal> calculerTarifLivraison(
            @RequestParam String pays,
            @RequestParam Livraison.TypeLivraison type,
            @RequestParam BigDecimal poids) {
        try {
            BigDecimal tarif = configurationLivraisonService.obtenirTarifLivraison(pays, type, poids);
            return ResponseEntity.ok(tarif);
        } catch (Exception e) {
            log.error("Erreur lors du calcul du tarif: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/calculer-delai")
    @Operation(summary = "Calculer le délai de livraison", 
               description = "Calcule le délai de livraison pour un pays et type donnés")
    @ApiResponse(responseCode = "200", description = "Délai calculé avec succès")
    public ResponseEntity<Integer> calculerDelaiLivraison(
            @RequestParam String pays,
            @RequestParam Livraison.TypeLivraison type) {
        try {
            Integer delai = configurationLivraisonService.obtenirDelaiLivraison(pays, type);
            return ResponseEntity.ok(delai);
        } catch (Exception e) {
            log.error("Erreur lors du calcul du délai: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
