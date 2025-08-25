package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.DetailPrixDto;
import sn.afrizar.afrizar.model.Livraison;
import sn.afrizar.afrizar.service.CalculPrixService;
import sn.afrizar.afrizar.service.LivraisonService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/calculs")
@RequiredArgsConstructor
@Tag(name = "Calculs", description = "API de calcul des prix et frais (commissions, expédition)")
public class CalculController {
    
    private final CalculPrixService calculPrixService;
    private final LivraisonService livraisonService;
    
    @GetMapping("/prix")
    @Operation(summary = "Calculer le prix final avec commission", description = "Calcule le prix final d'un produit avec détail transparent des commissions")
    @ApiResponse(responseCode = "200", description = "Prix calculé avec détail transparent")
    public ResponseEntity<DetailPrixDto> calculerPrixFinal(
            @Parameter(description = "Prix de base du vendeur") @RequestParam BigDecimal prixVendeur,
            @Parameter(description = "ID du vendeur (optionnel pour commission personnalisée)") @RequestParam(required = false) Long vendeurId) {
        
        log.info("Calcul du prix final pour prix vendeur: {} FCFA, vendeur: {}", prixVendeur, vendeurId);
        
        DetailPrixDto detail;
        if (vendeurId != null) {
            detail = calculPrixService.calculerPrixFinal(prixVendeur, vendeurId);
        } else {
            detail = calculPrixService.calculerPrixFinal(prixVendeur);
        }
        
        return ResponseEntity.ok(detail);
    }
    
    @GetMapping("/prix/exemples")
    @Operation(summary = "Obtenir des exemples de calculs de prix", description = "Génère des exemples de calculs selon les différentes tranches de commission")
    @ApiResponse(responseCode = "200", description = "Exemples de calculs générés")
    public ResponseEntity<String> obtenirExemplesCalculsPrix() {
        String exemples = calculPrixService.genererExemplesCalculs();
        return ResponseEntity.ok(exemples);
    }
    
    @GetMapping("/expedition")
    @Operation(summary = "Calculer les frais d'expédition", description = "Calcule les frais d'expédition selon le poids, la destination et le type")
    @ApiResponse(responseCode = "200", description = "Frais d'expédition calculés")
    public ResponseEntity<Map<String, Object>> calculerFraisExpedition(
            @Parameter(description = "Poids en kg") @RequestParam BigDecimal poids,
            @Parameter(description = "Pays de destination") @RequestParam String pays,
            @Parameter(description = "Ville de destination") @RequestParam(required = false) String ville,
            @Parameter(description = "Type d'expédition") @RequestParam Livraison.TypeLivraison type) {
        
        log.info("Calcul des frais d'expédition: {} kg vers {}, {}, type: {}", poids, pays, ville, type);
        
        Map<String, Object> resultat = new HashMap<>();
        
        // Calculer le coût
        BigDecimal cout = livraisonService.calculerCoutLivraison(poids, pays, ville, type);
        resultat.put("cout", cout);
        resultat.put("devise", "FCFA");
        
        // Calculer la date de livraison estimée
        LocalDate dateLivraison = livraisonService.calculerDateLivraisonEstimee(pays, type);
        resultat.put("dateLivraisonEstimee", dateLivraison);
        
        // Détails du calcul
        resultat.put("poids", poids);
        resultat.put("pays", pays);
        resultat.put("ville", ville);
        resultat.put("type", type);
        
        // Coût par kg
        BigDecimal coutParKg = cout.divide(poids, 2, BigDecimal.ROUND_HALF_UP);
        resultat.put("coutParKg", coutParKg);
        
        return ResponseEntity.ok(resultat);
    }
    
    @GetMapping("/expedition/tous-types")
    @Operation(summary = "Comparer tous les types d'expédition", description = "Compare les coûts et délais pour tous les types d'expédition vers une destination")
    @ApiResponse(responseCode = "200", description = "Comparaison des options d'expédition")
    public ResponseEntity<Map<String, Object>> comparerTypesExpedition(
            @Parameter(description = "Poids en kg") @RequestParam BigDecimal poids,
            @Parameter(description = "Pays de destination") @RequestParam String pays,
            @Parameter(description = "Ville de destination") @RequestParam(required = false) String ville) {
        
        log.info("Comparaison des types d'expédition: {} kg vers {}, {}", poids, pays, ville);
        
        Map<String, Object> comparaison = new HashMap<>();
        
        for (Livraison.TypeLivraison type : Livraison.TypeLivraison.values()) {
            Map<String, Object> option = new HashMap<>();
            
            BigDecimal cout = livraisonService.calculerCoutLivraison(poids, pays, ville, type);
            LocalDate dateLivraison = livraisonService.calculerDateLivraisonEstimee(pays, type);
            
            option.put("cout", cout);
            option.put("dateLivraisonEstimee", dateLivraison);
            option.put("delaiJours", dateLivraison.toEpochDay() - LocalDate.now().toEpochDay());
            
            // Calculer le rapport qualité/prix
            long delai = dateLivraison.toEpochDay() - LocalDate.now().toEpochDay();
            BigDecimal rapport = cout.divide(BigDecimal.valueOf(delai == 0 ? 1 : delai), 2, BigDecimal.ROUND_HALF_UP);
            option.put("rapportQualitePrix", rapport);
            
            comparaison.put(type.name(), option);
        }
        
        return ResponseEntity.ok(comparaison);
    }
    
    @GetMapping("/prix-complet")
    @Operation(summary = "Calculer le prix total (produit + expédition)", description = "Calcule le prix total incluant commission et frais d'expédition")
    @ApiResponse(responseCode = "200", description = "Prix total calculé")
    public ResponseEntity<Map<String, Object>> calculerPrixTotal(
            @Parameter(description = "Prix de base du vendeur") @RequestParam BigDecimal prixVendeur,
            @Parameter(description = "ID du vendeur") @RequestParam(required = false) Long vendeurId,
            @Parameter(description = "Poids en kg") @RequestParam BigDecimal poids,
            @Parameter(description = "Pays de destination") @RequestParam String pays,
            @Parameter(description = "Ville de destination") @RequestParam(required = false) String ville,
            @Parameter(description = "Type d'expédition") @RequestParam Livraison.TypeLivraison typeExpedition) {
        
        log.info("Calcul du prix total: produit {} FCFA + expédition {} kg vers {}", prixVendeur, poids, pays);
        
        Map<String, Object> prixTotal = new HashMap<>();
        
        // 1. Calculer le prix du produit avec commission
        DetailPrixDto detailPrix = vendeurId != null 
            ? calculPrixService.calculerPrixFinal(prixVendeur, vendeurId)
            : calculPrixService.calculerPrixFinal(prixVendeur);
        
        prixTotal.put("detailProduit", detailPrix);
        
        // 2. Calculer les frais d'expédition
        BigDecimal fraisExpedition = livraisonService.calculerCoutLivraison(poids, pays, ville, typeExpedition);
        LocalDate dateLivraison = livraisonService.calculerDateLivraisonEstimee(pays, typeExpedition);
        
        Map<String, Object> detailExpedition = new HashMap<>();
        detailExpedition.put("cout", fraisExpedition);
        detailExpedition.put("poids", poids);
        detailExpedition.put("destination", pays + (ville != null ? ", " + ville : ""));
        detailExpedition.put("type", typeExpedition);
        detailExpedition.put("dateLivraisonEstimee", dateLivraison);
        
        prixTotal.put("detailExpedition", detailExpedition);
        
        // 3. Calculer le total général
        BigDecimal totalGeneral = detailPrix.getPrixFinal().add(fraisExpedition);
        prixTotal.put("totalGeneral", totalGeneral);
        
        // 4. Résumé transparent
        Map<String, Object> resume = new HashMap<>();
        resume.put("prixVendeur", detailPrix.getPrixVendeur());
        resume.put("commission", detailPrix.getMontantCommission());
        resume.put("sousTotal", detailPrix.getPrixFinal());
        resume.put("fraisExpedition", fraisExpedition);
        resume.put("total", totalGeneral);
        resume.put("devise", "FCFA");
        
        prixTotal.put("resume", resume);
        
        // 5. Message transparent pour le client
        String messageTransparent = String.format(
            "Prix de vente: %,.0f FCFA (prix vendeur: %,.0f FCFA + commission %,.1f%%: %,.0f FCFA) + Expédition %s: %,.0f FCFA = TOTAL: %,.0f FCFA",
            detailPrix.getPrixFinal(),
            detailPrix.getPrixVendeur(),
            detailPrix.getPourcentageCommission(),
            detailPrix.getMontantCommission(),
            typeExpedition.name(),
            fraisExpedition,
            totalGeneral
        );
        
        prixTotal.put("messageTransparent", messageTransparent);
        
        return ResponseEntity.ok(prixTotal);
    }
    
    @GetMapping("/tarifs-expedition")
    @Operation(summary = "Obtenir la grille des tarifs d'expédition", description = "Affiche tous les tarifs d'expédition par destination et type")
    @ApiResponse(responseCode = "200", description = "Grille des tarifs récupérée")
    public ResponseEntity<Map<String, Object>> obtenirGrilleTarifs() {
        
        Map<String, Object> grille = new HashMap<>();
        
        // Créer des exemples pour 1 kg vers différentes destinations
        String[] destinations = {"SENEGAL", "FRANCE", "USA", "CANADA"};
        BigDecimal poids1kg = BigDecimal.ONE;
        
        for (String destination : destinations) {
            Map<String, Object> tarifsDestination = new HashMap<>();
            
            for (Livraison.TypeLivraison type : Livraison.TypeLivraison.values()) {
                Map<String, Object> detailType = new HashMap<>();
                
                BigDecimal cout = livraisonService.calculerCoutLivraison(poids1kg, destination, null, type);
                LocalDate dateLivraison = livraisonService.calculerDateLivraisonEstimee(destination, type);
                long delai = dateLivraison.toEpochDay() - LocalDate.now().toEpochDay();
                
                detailType.put("coutPar1kg", cout);
                detailType.put("delaiJours", delai);
                detailType.put("description", String.format("%,.0f FCFA/kg - %d jours", cout, delai));
                
                tarifsDestination.put(type.name(), detailType);
            }
            
            grille.put(destination, tarifsDestination);
        }
        
        // Ajouter des notes explicatives
        Map<String, String> notes = new HashMap<>();
        notes.put("reduction_gros_colis", "Réduction de 10% pour les colis > 5 kg");
        notes.put("supplement_ville_eloignee", "Supplément de 15% pour certaines villes éloignées");
        notes.put("minimum_facturation", "Minimum 1000 FCFA (Sénégal) / 5000 FCFA (International)");
        notes.put("devise", "Tous les prix sont en FCFA");
        
        grille.put("notes", notes);
        
        return ResponseEntity.ok(grille);
    }
}

