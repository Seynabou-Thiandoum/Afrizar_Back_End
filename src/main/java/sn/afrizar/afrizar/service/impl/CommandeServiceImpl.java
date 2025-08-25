package sn.afrizar.afrizar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.afrizar.afrizar.dto.CommandeDto;
import sn.afrizar.afrizar.dto.CreateCommandeDto;
import sn.afrizar.afrizar.model.Commande;
import sn.afrizar.afrizar.repository.CommandeRepository;
import sn.afrizar.afrizar.service.CommandeService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommandeServiceImpl implements CommandeService {
    
    private final CommandeRepository commandeRepository;
    
    @Override
    public CommandeDto creerCommande(CreateCommandeDto createCommandeDto) {
        log.info("Création d'une nouvelle commande");
        // TODO: Implémenter la logique de création de commande
        throw new UnsupportedOperationException("Méthode non implémentée");
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
    public CommandeDto annulerCommande(Long commandeId, String motif) {
        log.info("Annulation de la commande ID: {} - Motif: {}", commandeId, motif);
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec ID: " + commandeId));
        
        commande.setStatut(Commande.StatutCommande.ANNULEE);
        commande.setNotes(motif);
        Commande commandeMiseAJour = commandeRepository.save(commande);
        return convertirEntityVersDto(commandeMiseAJour);
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
        log.info("Calcul des totaux pour la commande");
        // TODO: Implémenter le calcul des totaux
        throw new UnsupportedOperationException("Méthode non implémentée");
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
        // TODO: Implémenter le calcul de commission
        throw new UnsupportedOperationException("Méthode non implémentée");
    }
    
    @Override
    public BigDecimal calculerFraisLivraison(Long commandeId) {
        log.info("Calcul des frais de livraison pour la commande {}", commandeId);
        // TODO: Implémenter le calcul des frais de livraison
        throw new UnsupportedOperationException("Méthode non implémentée");
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
    
    // Méthode de conversion
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
}
