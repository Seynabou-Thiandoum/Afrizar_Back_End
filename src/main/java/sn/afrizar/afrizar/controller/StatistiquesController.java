package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.model.Commande;
import sn.afrizar.afrizar.model.Livraison;
import sn.afrizar.afrizar.model.Paiement;
import sn.afrizar.afrizar.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/statistiques")
@RequiredArgsConstructor
@Tag(name = "Statistiques", description = "API des statistiques générales de la plateforme")
public class StatistiquesController {
    
    private final CommandeService commandeService;
    private final ClientService clientService;
    private final VendeurService vendeurService;
    private final ProduitService produitService;
    private final CategorieService categorieService;
    
    @GetMapping("/dashboard")
    @Operation(summary = "Obtenir les statistiques du tableau de bord", description = "Récupère toutes les statistiques principales pour le dashboard")
    @ApiResponse(responseCode = "200", description = "Statistiques récupérées")
    public ResponseEntity<Map<String, Object>> obtenirStatistiquesDashboard() {
        log.info("Récupération des statistiques du dashboard");
        
        Map<String, Object> statistiques = new HashMap<>();
        
        try {
            // Statistiques des commandes
            statistiques.put("nombreCommandesTotal", commandeService.getNombreCommandesParStatut(null));
            statistiques.put("nombreCommandesEnAttente", commandeService.getNombreCommandesParStatut(Commande.StatutCommande.EN_ATTENTE));
            statistiques.put("nombreCommandesConfirmees", commandeService.getNombreCommandesParStatut(Commande.StatutCommande.CONFIRMEE));
            statistiques.put("nombreCommandesLivrees", commandeService.getNombreCommandesParStatut(Commande.StatutCommande.LIVREE));
            
            // Statistiques financières
            statistiques.put("chiffreAffairesTotal", commandeService.getTotalChiffreAffaires());
            statistiques.put("panierMoyen", commandeService.getMoyennePanier());
            
            // Chiffre d'affaires des 30 derniers jours
            LocalDateTime il30Jours = LocalDateTime.now().minusDays(30);
            statistiques.put("chiffreAffaires30Jours", commandeService.getChiffreAffairesDepuis(il30Jours));
            
            // Statistiques des utilisateurs
            statistiques.put("nombreVendeursVerifies", vendeurService.getNombreVendeursVerifies());
            statistiques.put("moyennePointsFidelite", clientService.getMoyennePointsFidelite());
            
            // Statistiques des produits
            statistiques.put("prixMoyenProduits", produitService.getMoyennePrix());
            
            log.info("Statistiques du dashboard récupérées avec succès");
            return ResponseEntity.ok(statistiques);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques: {}", e.getMessage());
            return ResponseEntity.ok(new HashMap<>()); // Retourne un objet vide en cas d'erreur
        }
    }
    
    @GetMapping("/commandes/periode")
    @Operation(summary = "Statistiques des commandes par période", description = "Récupère les statistiques des commandes pour une période donnée")
    @ApiResponse(responseCode = "200", description = "Statistiques de période récupérées")
    public ResponseEntity<Map<String, Object>> obtenirStatistiquesCommandesPeriode(
            @Parameter(description = "Date de début (ISO format)") @RequestParam LocalDateTime debut,
            @Parameter(description = "Date de fin (ISO format)") @RequestParam LocalDateTime fin) {
        
        log.info("Récupération des statistiques des commandes du {} au {}", debut, fin);
        
        Map<String, Object> statistiques = new HashMap<>();
        
        try {
            // Nombre de commandes dans la période
            var commandesPeriode = commandeService.obtenirCommandesParPeriode(debut, fin);
            statistiques.put("nombreCommandes", commandesPeriode.size());
            
            // Chiffre d'affaires de la période
            BigDecimal caPeriode = commandesPeriode.stream()
                    .map(cmd -> cmd.getMontantTotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            statistiques.put("chiffreAffaires", caPeriode);
            
            // Panier moyen de la période
            if (!commandesPeriode.isEmpty()) {
                BigDecimal panierMoyen = caPeriode.divide(BigDecimal.valueOf(commandesPeriode.size()), 2, BigDecimal.ROUND_HALF_UP);
                statistiques.put("panierMoyen", panierMoyen);
            } else {
                statistiques.put("panierMoyen", BigDecimal.ZERO);
            }
            
            return ResponseEntity.ok(statistiques);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques de période: {}", e.getMessage());
            return ResponseEntity.ok(new HashMap<>());
        }
    }
    
    @GetMapping("/vendeurs/{vendeurId}")
    @Operation(summary = "Statistiques d'un vendeur", description = "Récupère les statistiques détaillées d'un vendeur spécifique")
    @ApiResponse(responseCode = "200", description = "Statistiques du vendeur récupérées")
    @ApiResponse(responseCode = "404", description = "Vendeur non trouvé")
    public ResponseEntity<Map<String, Object>> obtenirStatistiquesVendeur(
            @Parameter(description = "ID du vendeur") @PathVariable Long vendeurId) {
        
        log.info("Récupération des statistiques du vendeur ID: {}", vendeurId);
        
        try {
            // Vérifier que le vendeur existe
            if (vendeurService.obtenirVendeurParId(vendeurId).isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> statistiques = new HashMap<>();
            
            // Nombre de produits
            statistiques.put("nombreProduits", vendeurService.getNombreProduitsVendeur(vendeurId));
            
            // Commandes du vendeur
            var commandesVendeur = commandeService.obtenirCommandesParVendeur(vendeurId);
            statistiques.put("nombreCommandes", commandesVendeur.size());
            
            // Chiffre d'affaires du vendeur
            BigDecimal caVendeur = commandesVendeur.stream()
                    .map(cmd -> cmd.getMontantTotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            statistiques.put("chiffreAffaires", caVendeur);
            
            return ResponseEntity.ok(statistiques);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques du vendeur {}: {}", vendeurId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/clients/{clientId}")
    @Operation(summary = "Statistiques d'un client", description = "Récupère les statistiques détaillées d'un client spécifique")
    @ApiResponse(responseCode = "200", description = "Statistiques du client récupérées")
    @ApiResponse(responseCode = "404", description = "Client non trouvé")
    public ResponseEntity<Map<String, Object>> obtenirStatistiquesClient(
            @Parameter(description = "ID du client") @PathVariable Long clientId) {
        
        log.info("Récupération des statistiques du client ID: {}", clientId);
        
        try {
            // Vérifier que le client existe
            var clientOpt = clientService.obtenirClientParId(clientId);
            if (clientOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> statistiques = new HashMap<>();
            
            var client = clientOpt.get();
            statistiques.put("pointsFidelite", client.getPointsFidelite());
            
            // Nombre de commandes du client
            statistiques.put("nombreCommandes", commandeService.getNombreCommandesParClient(clientId));
            
            // Commandes du client
            var commandesClient = commandeService.obtenirCommandesParClient(clientId);
            
            // Montant total dépensé
            BigDecimal montantTotal = commandesClient.stream()
                    .map(cmd -> cmd.getMontantTotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            statistiques.put("montantTotalDepense", montantTotal);
            
            // Panier moyen du client
            if (!commandesClient.isEmpty()) {
                BigDecimal panierMoyen = montantTotal.divide(BigDecimal.valueOf(commandesClient.size()), 2, BigDecimal.ROUND_HALF_UP);
                statistiques.put("panierMoyen", panierMoyen);
            } else {
                statistiques.put("panierMoyen", BigDecimal.ZERO);
            }
            
            return ResponseEntity.ok(statistiques);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques du client {}: {}", clientId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/categories/{categorieId}")
    @Operation(summary = "Statistiques d'une catégorie", description = "Récupère les statistiques d'une catégorie de produits")
    @ApiResponse(responseCode = "200", description = "Statistiques de la catégorie récupérées")
    @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    public ResponseEntity<Map<String, Object>> obtenirStatistiquesCategorie(
            @Parameter(description = "ID de la catégorie") @PathVariable Long categorieId) {
        
        log.info("Récupération des statistiques de la catégorie ID: {}", categorieId);
        
        try {
            // Vérifier que la catégorie existe
            if (categorieService.obtenirCategorieParId(categorieId).isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> statistiques = new HashMap<>();
            
            // Nombre de produits dans la catégorie
            statistiques.put("nombreProduits", categorieService.getNombreProduitsParCategorie(categorieId));
            
            // Produits de la catégorie
            var produitsCategorie = produitService.obtenirProduitsParCategorie(categorieId);
            
            // Prix moyen des produits de la catégorie
            if (!produitsCategorie.isEmpty()) {
                BigDecimal prixMoyen = produitsCategorie.stream()
                        .map(p -> p.getPrix())
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(produitsCategorie.size()), 2, BigDecimal.ROUND_HALF_UP);
                statistiques.put("prixMoyen", prixMoyen);
            } else {
                statistiques.put("prixMoyen", BigDecimal.ZERO);
            }
            
            // Stock total de la catégorie
            Integer stockTotal = produitsCategorie.stream()
                    .mapToInt(p -> p.getStock() != null ? p.getStock() : 0)
                    .sum();
            statistiques.put("stockTotal", stockTotal);
            
            return ResponseEntity.ok(statistiques);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques de la catégorie {}: {}", categorieId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/resume")
    @Operation(summary = "Résumé exécutif", description = "Résumé des indicateurs clés de performance (KPI)")
    @ApiResponse(responseCode = "200", description = "Résumé des KPI récupéré")
    public ResponseEntity<Map<String, Object>> obtenirResumeExecutif() {
        log.info("Récupération du résumé exécutif");
        
        Map<String, Object> resume = new HashMap<>();
        
        try {
            // KPI principaux
            resume.put("chiffreAffairesTotal", commandeService.getTotalChiffreAffaires());
            resume.put("nombreCommandesTotal", commandeService.getNombreCommandesParStatut(null));
            resume.put("nombreVendeursActifs", vendeurService.getNombreVendeursVerifies());
            resume.put("panierMoyen", commandeService.getMoyennePanier());
            
            // Croissance sur 30 jours
            LocalDateTime il30Jours = LocalDateTime.now().minusDays(30);
            LocalDateTime il60Jours = LocalDateTime.now().minusDays(60);
            
            BigDecimal ca30Jours = commandeService.getChiffreAffairesDepuis(il30Jours);
            BigDecimal ca60Jours = commandeService.getChiffreAffairesDepuis(il60Jours);
            BigDecimal caPrecedent30Jours = ca60Jours.subtract(ca30Jours);
            
            if (caPrecedent30Jours.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal croissance = ca30Jours.subtract(caPrecedent30Jours)
                        .divide(caPrecedent30Jours, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                resume.put("croissanceCA30Jours", croissance);
            } else {
                resume.put("croissanceCA30Jours", BigDecimal.ZERO);
            }
            
            return ResponseEntity.ok(resume);
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du résumé exécutif: {}", e.getMessage());
            return ResponseEntity.ok(new HashMap<>());
        }
    }
}

