package sn.afrizar.afrizar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.afrizar.afrizar.dto.*;
import sn.afrizar.afrizar.model.*;
import sn.afrizar.afrizar.repository.*;
import sn.afrizar.afrizar.service.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommandeServiceImpl implements CommandeService {
    
    private final CommandeRepository commandeRepository;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final PaiementRepository paiementRepository;
    private final LivraisonService livraisonService;
    private final CalculPrixService calculPrixService;
    
    @Override
    public CommandeDto creerCommande(CreateCommandeDto createCommandeDto) {
        log.info("Création d'une nouvelle commande pour le client ID: {}", createCommandeDto.getClientId());
        
        // 1. Vérifier que le client existe
        Client client = clientRepository.findById(createCommandeDto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec ID: " + createCommandeDto.getClientId()));
        
        // 2. Créer la commande
        Commande commande = new Commande();
        commande.setClient(client);
        commande.setType(createCommandeDto.getType());
        commande.setDateLivraisonSouhaitee(createCommandeDto.getDateLivraisonSouhaitee());
        commande.setNotes(createCommandeDto.getNotes());
        commande.setStatut(Commande.StatutCommande.EN_ATTENTE);
        commande.setPointsFideliteUtilises(createCommandeDto.getPointsFideliteUtilises());
        
        // 3. Valider et créer les lignes de commande
        List<LigneCommande> lignesCommande = new ArrayList<>();
        BigDecimal montantHT = BigDecimal.ZERO;
        BigDecimal montantCommissionTotal = BigDecimal.ZERO;
        
        for (CreateLigneCommandeDto ligneDto : createCommandeDto.getLignesCommande()) {
            // Récupérer le produit
            Produit produit = produitRepository.findById(ligneDto.getProduitId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé avec ID: " + ligneDto.getProduitId()));
            
            // Vérifier le stock
            if (!verifierStock(produit, ligneDto.getQuantite())) {
                throw new RuntimeException("Stock insuffisant pour le produit: " + produit.getNom() + 
                                         " (disponible: " + produit.getStock() + ", demandé: " + ligneDto.getQuantite() + ")");
            }
            
            // Créer la ligne de commande
            LigneCommande ligne = new LigneCommande();
            ligne.setCommande(commande);
            ligne.setProduit(produit);
            ligne.setQuantite(ligneDto.getQuantite());
            ligne.setPrixUnitaire(produit.getPrix());
            ligne.setTaille(ligneDto.getTaille());
            ligne.setPersonnalisation(ligneDto.getPersonnalisation());
            ligne.setNotes(ligneDto.getNotes());
            
            // Calculer le sous-total
            BigDecimal sousTotal = produit.getPrix().multiply(BigDecimal.valueOf(ligneDto.getQuantite()));
            ligne.setSousTotal(sousTotal);
            
            // Calculer la commission pour cette ligne
            DetailPrixDto detailPrix = calculPrixService.calculerPrixFinal(sousTotal, produit.getVendeur().getId());
            ligne.setCommission(detailPrix.getMontantCommission());
            
            lignesCommande.add(ligne);
            
            montantHT = montantHT.add(sousTotal);
            montantCommissionTotal = montantCommissionTotal.add(detailPrix.getMontantCommission());
            
            // Décrémenter le stock
            decremeneterStock(produit, ligneDto.getQuantite());
        }
        
        commande.setLignesCommande(lignesCommande);
        commande.setMontantHT(montantHT);
        commande.setMontantCommission(montantCommissionTotal);
        
        // 4. Calculer les frais de livraison si les infos de livraison sont fournies
        BigDecimal fraisLivraison = BigDecimal.ZERO;
        if (createCommandeDto.getLivraison() != null) {
            fraisLivraison = calculerFraisLivraisonPourCommande(commande, createCommandeDto.getLivraison());
        }
        commande.setFraisLivraison(fraisLivraison);
        
        // 5. Calculer la réduction avec les points de fidélité
        BigDecimal reduction = BigDecimal.ZERO;
        if (createCommandeDto.getPointsFideliteUtilises() != null && createCommandeDto.getPointsFideliteUtilises() > 0) {
            reduction = calculerReductionPointsFidelite(client, createCommandeDto.getPointsFideliteUtilises());
            // Déduire les points du client
            utiliserPointsFidelite(client, createCommandeDto.getPointsFideliteUtilises());
        }
        commande.setReduction(reduction);
        
        // 6. Calculer le montant total
        BigDecimal montantTotal = montantHT
                .add(montantCommissionTotal)
                .add(fraisLivraison)
                .subtract(reduction);
        commande.setMontantTotal(montantTotal);
        
        // 7. Sauvegarder la commande
        Commande commandeSauvegardee = commandeRepository.save(commande);
        
        // 8. Créer le paiement initial
        creerPaiementInitial(commandeSauvegardee);
        
        // 9. Créer la livraison si les infos sont fournies
        if (createCommandeDto.getLivraison() != null) {
            livraisonService.creerLivraison(createCommandeDto.getLivraison(), commandeSauvegardee.getId());
        }
        
        // 10. Attribuer des points de fidélité (1% du montant)
        attribuerPointsFidelite(client, montantTotal);
        
        log.info("Commande créée avec succès: {} - Montant total: {} FCFA", 
                commandeSauvegardee.getNumeroCommande(), montantTotal);
        
        return convertirEntityVersDtoComplet(commandeSauvegardee);
    }
    
    @Override
    public Optional<CommandeDto> obtenirCommandeParId(Long id) {
        log.info("Récupération de la commande avec ID: {}", id);
        return commandeRepository.findById(id)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    public Optional<CommandeDto> obtenirCommandeParNumero(String numeroCommande) {
        log.info("Récupération de la commande avec numéro: {}", numeroCommande);
        return commandeRepository.findByNumeroCommande(numeroCommande)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CommandeDto> obtenirToutesLesCommandesAvecPagination(Pageable pageable) {
        log.info("Récupération de toutes les commandes avec pagination");
        return commandeRepository.findAll(pageable)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    public List<CommandeDto> obtenirCommandesParClient(Long clientId) {
        log.info("Récupération des commandes pour le client ID: {}", clientId);
        return commandeRepository.findByClientId(clientId)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<CommandeDto> obtenirCommandesParClientAvecPagination(Long clientId, Pageable pageable) {
        log.info("Récupération des commandes paginées pour le client ID: {}", clientId);
        return commandeRepository.findByClientIdOrderByDateCreationDesc(clientId, pageable)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    public List<CommandeDto> obtenirCommandesParVendeur(Long vendeurId) {
        log.info("Récupération des commandes pour le vendeur ID: {}", vendeurId);
        return commandeRepository.findCommandesByVendeur(vendeurId)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<CommandeDto> obtenirCommandesParVendeurAvecPagination(Long vendeurId, Pageable pageable) {
        log.info("Récupération des commandes paginées pour le vendeur ID: {}", vendeurId);
        return commandeRepository.findCommandesByVendeurOrderByDateDesc(vendeurId, pageable)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    public List<CommandeDto> obtenirCommandesParStatut(Commande.StatutCommande statut) {
        log.info("Récupération des commandes avec statut: {}", statut);
        return commandeRepository.findByStatut(statut)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public CommandeDto changerStatutCommande(Long commandeId, Commande.StatutCommande nouveauStatut) {
        log.info("Changement du statut de la commande {} vers {}", commandeId, nouveauStatut);
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec ID: " + commandeId));
        
        commande.setStatut(nouveauStatut);
        Commande commandeMiseAJour = commandeRepository.save(commande);
        return convertirEntityVersDto(commandeMiseAJour);
    }
    
    @Override
    public CommandeDto confirmerCommande(Long commandeId) {
        log.info("Confirmation de la commande ID: {}", commandeId);
        return changerStatutCommande(commandeId, Commande.StatutCommande.CONFIRMEE);
    }
    
    
    @Override
    public CommandeDto expedierCommande(Long commandeId, String numeroSuivi, String transporteur) {
        log.info("Expédition de la commande ID: {} avec numéro de suivi: {}", commandeId, numeroSuivi);
        return changerStatutCommande(commandeId, Commande.StatutCommande.EXPEDIEE);
    }
    
    @Override
    public CommandeDto livrerCommande(Long commandeId) {
        log.info("Livraison de la commande ID: {}", commandeId);
        return changerStatutCommande(commandeId, Commande.StatutCommande.LIVREE);
    }
    
    @Override
    public Page<CommandeDto> rechercherCommandesAvecFiltres(
            Long clientId,
            Commande.StatutCommande statut,
            BigDecimal montantMin,
            BigDecimal montantMax,
            LocalDateTime dateDebut,
            LocalDateTime dateFin,
            Pageable pageable) {
        log.info("Recherche de commandes avec filtres");
        return commandeRepository.findCommandesAvecFiltres(clientId, statut, montantMin, montantMax, dateDebut, dateFin, pageable)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    public List<CommandeDto> obtenirCommandesEnRetard() {
        log.info("Récupération des commandes en retard");
        return commandeRepository.findCommandesEnRetard()
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CommandeDto> obtenirCommandesParPeriode(LocalDateTime debut, LocalDateTime fin) {
        log.info("Récupération des commandes pour la période {} - {}", debut, fin);
        return commandeRepository.findCommandesParPeriode(debut, fin)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public CommandeDto calculerTotauxCommande(CreateCommandeDto createCommandeDto) {
        log.info("Calcul des totaux pour la commande (prévisualisation)");
        
        // Vérifier que le client existe
        Client client = clientRepository.findById(createCommandeDto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec ID: " + createCommandeDto.getClientId()));
        
        // Calculer les montants sans sauvegarder
        BigDecimal montantHT = BigDecimal.ZERO;
        BigDecimal montantCommissionTotal = BigDecimal.ZERO;
        List<LigneCommandeDto> lignesDtos = new ArrayList<>();
        
        for (CreateLigneCommandeDto ligneDto : createCommandeDto.getLignesCommande()) {
            Produit produit = produitRepository.findById(ligneDto.getProduitId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé avec ID: " + ligneDto.getProduitId()));
            
            // Vérifier le stock
            if (!verifierStock(produit, ligneDto.getQuantite())) {
                throw new RuntimeException("Stock insuffisant pour le produit: " + produit.getNom());
            }
            
            BigDecimal sousTotal = produit.getPrix().multiply(BigDecimal.valueOf(ligneDto.getQuantite()));
            DetailPrixDto detailPrix = calculPrixService.calculerPrixFinal(sousTotal, produit.getVendeur().getId());
            
            LigneCommandeDto ligneCommandeDto = new LigneCommandeDto();
            ligneCommandeDto.setProduitId(produit.getId());
            ligneCommandeDto.setNomProduit(produit.getNom());
            ligneCommandeDto.setQuantite(ligneDto.getQuantite());
            ligneCommandeDto.setPrixUnitaire(produit.getPrix());
            ligneCommandeDto.setTaille(ligneDto.getTaille());
            ligneCommandeDto.setPersonnalisation(ligneDto.getPersonnalisation());
            ligneCommandeDto.setSousTotal(sousTotal);
            ligneCommandeDto.setCommission(detailPrix.getMontantCommission());
            lignesDtos.add(ligneCommandeDto);
            
            montantHT = montantHT.add(sousTotal);
            montantCommissionTotal = montantCommissionTotal.add(detailPrix.getMontantCommission());
        }
        
        // Calculer les frais de livraison
        BigDecimal fraisLivraison = BigDecimal.ZERO;
        if (createCommandeDto.getLivraison() != null) {
            // Calculer le poids total
            BigDecimal poidsTotal = BigDecimal.ZERO;
            for (CreateLigneCommandeDto ligneDto : createCommandeDto.getLignesCommande()) {
                Produit produit = produitRepository.findById(ligneDto.getProduitId()).orElseThrow();
                BigDecimal poidsProduit = produit.getPoids() != null ? produit.getPoids() : BigDecimal.valueOf(0.5);
                poidsTotal = poidsTotal.add(poidsProduit.multiply(BigDecimal.valueOf(ligneDto.getQuantite())));
            }
            
            fraisLivraison = livraisonService.calculerCoutLivraison(
                poidsTotal,
                createCommandeDto.getLivraison().getPays(),
                createCommandeDto.getLivraison().getVille(),
                createCommandeDto.getLivraison().getType()
            );
        }
        
        // Calculer la réduction avec les points de fidélité
        BigDecimal reduction = BigDecimal.ZERO;
        if (createCommandeDto.getPointsFideliteUtilises() != null && createCommandeDto.getPointsFideliteUtilises() > 0) {
            reduction = calculerReductionPointsFidelite(client, createCommandeDto.getPointsFideliteUtilises());
        }
        
        // Calculer le montant total
        BigDecimal montantTotal = montantHT
                .add(montantCommissionTotal)
                .add(fraisLivraison)
                .subtract(reduction);
        
        // Créer le DTO de réponse
        CommandeDto commandeDto = new CommandeDto();
        commandeDto.setType(createCommandeDto.getType());
        commandeDto.setMontantHT(montantHT);
        commandeDto.setMontantCommission(montantCommissionTotal);
        commandeDto.setFraisLivraison(fraisLivraison);
        commandeDto.setReduction(reduction);
        commandeDto.setMontantTotal(montantTotal);
        commandeDto.setPointsFideliteUtilises(createCommandeDto.getPointsFideliteUtilises());
        commandeDto.setLignesCommande(lignesDtos);
        commandeDto.setClientId(client.getId());
        commandeDto.setNomClient(client.getNom());
        commandeDto.setEmailClient(client.getEmail());
        
        log.info("Totaux calculés: HT={}, Commission={}, Livraison={}, Réduction={}, Total={}", 
                montantHT, montantCommissionTotal, fraisLivraison, reduction, montantTotal);
        
        return commandeDto;
    }
    
    @Override
    public BigDecimal getTotalChiffreAffaires() {
        log.info("Calcul du total chiffre d'affaires");
        return commandeRepository.getTotalChiffreAffaires();
    }
    
    @Override
    public BigDecimal getChiffreAffairesDepuis(LocalDateTime debut) {
        log.info("Calcul du chiffre d'affaires depuis {}", debut);
        return commandeRepository.getChiffreAffairesDepuis(debut);
    }
    
    @Override
    public Long getNombreCommandesParClient(Long clientId) {
        log.info("Calcul du nombre de commandes pour le client {}", clientId);
        return commandeRepository.countCommandesByClient(clientId);
    }
    
    @Override
    public BigDecimal calculerCommissionTotale(Long commandeId) {
        log.info("Calcul de la commission totale pour la commande {}", commandeId);
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec ID: " + commandeId));
        
        return commande.getMontantCommission();
    }
    
    @Override
    public BigDecimal calculerFraisLivraison(Long commandeId) {
        log.info("Calcul des frais de livraison pour la commande {}", commandeId);
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec ID: " + commandeId));
        
        return commande.getFraisLivraison();
    }
    
    @Override
    public Long getNombreCommandesParStatut(Commande.StatutCommande statut) {
        log.info("Calcul du nombre de commandes avec statut {}", statut);
        return commandeRepository.countByStatut(statut);
    }
    
    @Override
    public BigDecimal getMoyennePanier() {
        log.info("Calcul de la moyenne panier");
        return commandeRepository.getMoyennePanier();
    }
    
    // ==================== MÉTHODES AUXILIAIRES ====================
    
    /**
     * Vérifie si le stock est suffisant pour la quantité demandée
     */
    private boolean verifierStock(Produit produit, Integer quantiteDemandee) {
        // Si le produit est sur commande, pas de vérification de stock
        if (produit.getDisponibilite() == Produit.Disponibilite.SUR_COMMANDE) {
            return true;
        }
        
        // Vérifier le stock
        return produit.getStock() != null && produit.getStock() >= quantiteDemandee;
    }
    
    /**
     * Décrémente le stock d'un produit
     */
    private void decremeneterStock(Produit produit, Integer quantite) {
        // Si le produit est sur commande, ne pas décrémenter le stock
        if (produit.getDisponibilite() == Produit.Disponibilite.SUR_COMMANDE) {
            return;
        }
        
        Integer stockActuel = produit.getStock() != null ? produit.getStock() : 0;
        Integer nouveauStock = stockActuel - quantite;
        
        produit.setStock(nouveauStock);
        
        // Mettre à jour la disponibilité si le stock est à zéro
        if (nouveauStock <= 0) {
            produit.setDisponibilite(Produit.Disponibilite.RUPTURE_STOCK);
        }
        
        produitRepository.save(produit);
        log.debug("Stock décrémenté pour le produit {}: {} -> {}", 
                 produit.getNom(), stockActuel, nouveauStock);
    }
    
    /**
     * Restaure le stock d'un produit (en cas d'annulation de commande)
     */
    private void restaurerStock(Produit produit, Integer quantite) {
        Integer stockActuel = produit.getStock() != null ? produit.getStock() : 0;
        Integer nouveauStock = stockActuel + quantite;
        
        produit.setStock(nouveauStock);
        
        // Mettre à jour la disponibilité
        if (nouveauStock > 0 && produit.getDisponibilite() == Produit.Disponibilite.RUPTURE_STOCK) {
            produit.setDisponibilite(Produit.Disponibilite.EN_STOCK);
        }
        
        produitRepository.save(produit);
        log.debug("Stock restauré pour le produit {}: {} -> {}", 
                 produit.getNom(), stockActuel, nouveauStock);
    }
    
    /**
     * Calcule les frais de livraison pour une commande
     */
    private BigDecimal calculerFraisLivraisonPourCommande(Commande commande, CreateLivraisonDto livraisonDto) {
        // Calculer le poids total des produits
        BigDecimal poidsTotal = commande.getLignesCommande().stream()
                .map(ligne -> {
                    BigDecimal poidsProduit = ligne.getProduit().getPoids() != null ? 
                        ligne.getProduit().getPoids() : BigDecimal.valueOf(0.5);
                    return poidsProduit.multiply(BigDecimal.valueOf(ligne.getQuantite()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return livraisonService.calculerCoutLivraison(
            poidsTotal,
            livraisonDto.getPays(),
            livraisonDto.getVille(),
            livraisonDto.getType()
        );
    }
    
    /**
     * Calcule la réduction en fonction des points de fidélité utilisés
     * Règle: 1 point = 1 FCFA de réduction
     */
    private BigDecimal calculerReductionPointsFidelite(Client client, Integer pointsUtilises) {
        if (pointsUtilises == null || pointsUtilises <= 0) {
            return BigDecimal.ZERO;
        }
        
        // Vérifier que le client a suffisamment de points
        if (client.getPointsFidelite() < pointsUtilises) {
            throw new RuntimeException("Points de fidélité insuffisants. Disponible: " + 
                                     client.getPointsFidelite() + ", Demandé: " + pointsUtilises);
        }
        
        // 1 point = 1 FCFA
        return BigDecimal.valueOf(pointsUtilises);
    }
    
    /**
     * Utilise les points de fidélité d'un client
     */
    private void utiliserPointsFidelite(Client client, Integer pointsUtilises) {
        if (pointsUtilises == null || pointsUtilises <= 0) {
            return;
        }
        
        Integer pointsActuels = client.getPointsFidelite() != null ? client.getPointsFidelite() : 0;
        Integer nouveauxPoints = pointsActuels - pointsUtilises;
        
        client.setPointsFidelite(Math.max(0, nouveauxPoints));
        clientRepository.save(client);
        
        log.info("Points de fidélité utilisés pour le client {}: {} points (reste: {})", 
                client.getEmail(), pointsUtilises, nouveauxPoints);
    }
    
    /**
     * Attribue des points de fidélité à un client
     * Règle: 1% du montant de la commande
     */
    private void attribuerPointsFidelite(Client client, BigDecimal montantCommande) {
        // Calculer 1% du montant (arrondi à l'unité inférieure)
        int pointsAAttribuer = montantCommande
                .multiply(BigDecimal.valueOf(0.01))
                .setScale(0, RoundingMode.DOWN)
                .intValue();
        
        if (pointsAAttribuer > 0) {
            Integer pointsActuels = client.getPointsFidelite() != null ? client.getPointsFidelite() : 0;
            Integer nouveauxPoints = pointsActuels + pointsAAttribuer;
            
            client.setPointsFidelite(nouveauxPoints);
            clientRepository.save(client);
            
            log.info("Points de fidélité attribués au client {}: +{} points (total: {})", 
                    client.getEmail(), pointsAAttribuer, nouveauxPoints);
        }
    }
    
    /**
     * Crée un paiement initial pour une commande
     */
    private void creerPaiementInitial(Commande commande) {
        Paiement paiement = new Paiement();
        paiement.setCommande(commande);
        paiement.setMontant(commande.getMontantTotal());
        paiement.setStatut(Paiement.StatutPaiement.EN_ATTENTE);
        paiement.setMethode(Paiement.MethodePaiement.CARTE_CREDIT); // Par défaut
        paiement.setDevise("XOF");
        paiement.setFraisTransaction(BigDecimal.ZERO);
        paiement.setMontantNet(commande.getMontantTotal());
        
        paiementRepository.save(paiement);
        log.debug("Paiement initial créé pour la commande {}: {} FCFA", 
                 commande.getNumeroCommande(), commande.getMontantTotal());
    }
    
    /**
     * Annule une commande et restaure le stock
     */
    @Override
    public CommandeDto annulerCommande(Long commandeId, String motif) {
        log.info("Annulation de la commande ID: {} - Motif: {}", commandeId, motif);
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec ID: " + commandeId));
        
        // Restaurer le stock pour chaque ligne de commande
        for (LigneCommande ligne : commande.getLignesCommande()) {
            restaurerStock(ligne.getProduit(), ligne.getQuantite());
        }
        
        // Restaurer les points de fidélité utilisés
        if (commande.getPointsFideliteUtilises() != null && commande.getPointsFideliteUtilises() > 0) {
            Client client = commande.getClient();
            Integer pointsActuels = client.getPointsFidelite() != null ? client.getPointsFidelite() : 0;
            client.setPointsFidelite(pointsActuels + commande.getPointsFideliteUtilises());
            clientRepository.save(client);
        }
        
        commande.setStatut(Commande.StatutCommande.ANNULEE);
        commande.setNotes(motif);
        Commande commandeMiseAJour = commandeRepository.save(commande);
        
        log.info("Commande {} annulée avec succès, stock restauré", commande.getNumeroCommande());
        
        return convertirEntityVersDto(commandeMiseAJour);
    }
    
    // ==================== MÉTHODES DE CONVERSION ====================
    
    /**
     * Convertit une entité Commande en DTO simple
     */
    private CommandeDto convertirEntityVersDto(Commande commande) {
        CommandeDto dto = new CommandeDto();
        dto.setId(commande.getId());
        dto.setNumeroCommande(commande.getNumeroCommande());
        dto.setDateCreation(commande.getDateCreation());
        dto.setStatut(commande.getStatut());
        dto.setType(commande.getType());
        dto.setDateLivraisonSouhaitee(commande.getDateLivraisonSouhaitee());
        dto.setDateLivraisonEstimee(commande.getDateLivraisonEstimee());
        dto.setMontantHT(commande.getMontantHT());
        dto.setMontantCommission(commande.getMontantCommission());
        dto.setFraisLivraison(commande.getFraisLivraison());
        dto.setMontantTotal(commande.getMontantTotal());
        dto.setPointsFideliteUtilises(commande.getPointsFideliteUtilises());
        dto.setReduction(commande.getReduction());
        dto.setNotes(commande.getNotes());
        
        if (commande.getClient() != null) {
            dto.setClientId(commande.getClient().getId());
            dto.setNomClient(commande.getClient().getNom());
            dto.setEmailClient(commande.getClient().getEmail());
        }
        
        return dto;
    }
    
    /**
     * Convertit une entité Commande en DTO complet (avec lignes, paiement, livraison)
     */
    private CommandeDto convertirEntityVersDtoComplet(Commande commande) {
        CommandeDto dto = convertirEntityVersDto(commande);
        
        // Ajouter les lignes de commande
        if (commande.getLignesCommande() != null && !commande.getLignesCommande().isEmpty()) {
            List<LigneCommandeDto> lignesDtos = commande.getLignesCommande().stream()
                    .map(this::convertirLigneCommandeVersDto)
                    .collect(Collectors.toList());
            dto.setLignesCommande(lignesDtos);
        }
        
        // Ajouter le paiement
        if (commande.getPaiement() != null) {
            dto.setPaiement(convertirPaiementVersDto(commande.getPaiement()));
        }
        
        // Ajouter la livraison
        if (commande.getLivraison() != null) {
            dto.setLivraison(convertirLivraisonVersDto(commande.getLivraison()));
        }
        
        return dto;
    }
    
    /**
     * Convertit une LigneCommande en LigneCommandeDto
     */
    private LigneCommandeDto convertirLigneCommandeVersDto(LigneCommande ligne) {
        LigneCommandeDto dto = new LigneCommandeDto();
        dto.setId(ligne.getId());
        dto.setCommandeId(ligne.getCommande().getId());
        dto.setProduitId(ligne.getProduit().getId());
        dto.setNomProduit(ligne.getProduit().getNom());
        
        // Récupérer la première photo si disponible
        if (ligne.getProduit().getPhotos() != null && !ligne.getProduit().getPhotos().isEmpty()) {
            dto.setPhotoProduit(ligne.getProduit().getPhotos().get(0));
        }
        
        dto.setQuantite(ligne.getQuantite());
        dto.setPrixUnitaire(ligne.getPrixUnitaire());
        dto.setTaille(ligne.getTaille());
        dto.setPersonnalisation(ligne.getPersonnalisation());
        dto.setSousTotal(ligne.getSousTotal());
        dto.setCommission(ligne.getCommission());
        dto.setNotes(ligne.getNotes());
        
        return dto;
    }
    
    /**
     * Convertit un Paiement en PaiementDto
     */
    private PaiementDto convertirPaiementVersDto(Paiement paiement) {
        PaiementDto dto = new PaiementDto();
        dto.setId(paiement.getId());
        dto.setMontant(paiement.getMontant());
        dto.setMethode(paiement.getMethode());
        dto.setStatut(paiement.getStatut());
        dto.setDateCreation(paiement.getDateCreation());
        dto.setDatePaiement(paiement.getDatePaiement());
        dto.setReferenceExterne(paiement.getReferenceExterne());
        dto.setNumeroTransaction(paiement.getNumeroTransaction());
        dto.setDevise(paiement.getDevise());
        dto.setFraisTransaction(paiement.getFraisTransaction());
        dto.setMontantNet(paiement.getMontantNet());
        dto.setDetailsPaiement(paiement.getDetailsPaiement());
        
        return dto;
    }
    
    /**
     * Convertit une Livraison en LivraisonDto
     */
    private LivraisonDto convertirLivraisonVersDto(Livraison livraison) {
        LivraisonDto dto = new LivraisonDto();
        dto.setId(livraison.getId());
        dto.setCommandeId(livraison.getCommande().getId());
        dto.setType(livraison.getType());
        dto.setAdresseLivraison(livraison.getAdresseLivraison());
        dto.setVille(livraison.getVille());
        dto.setCodePostal(livraison.getCodePostal());
        dto.setPays(livraison.getPays());
        dto.setCout(livraison.getCout());
        dto.setPoidsTotal(livraison.getPoidsTotal());
        dto.setStatut(livraison.getStatut());
        dto.setDateExpedition(livraison.getDateExpedition());
        dto.setDateLivraisonPrevue(livraison.getDateLivraisonPrevue());
        dto.setDateLivraisonEffective(livraison.getDateLivraisonEffective());
        dto.setNumeroSuivi(livraison.getNumeroSuivi());
        dto.setTransporteur(livraison.getTransporteur());
        dto.setDateCreation(livraison.getDateCreation());
        dto.setNotes(livraison.getNotes());
        
        return dto;
    }
}
