package sn.afrizar.afrizar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.afrizar.afrizar.dto.AjouterAuPanierDto;
import sn.afrizar.afrizar.dto.PanierDto;
import sn.afrizar.afrizar.dto.PanierItemDto;
import sn.afrizar.afrizar.model.*;
import sn.afrizar.afrizar.repository.*;
import sn.afrizar.afrizar.service.PanierService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PanierServiceImpl implements PanierService {
    
    private final PanierRepository panierRepository;
    private final PanierItemRepository panierItemRepository;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;
    
    @Override
    @Transactional
    public PanierDto obtenirPanierClient(Long clientId) {
        log.info("Récupération du panier pour le client ID: {}", clientId);
        
        // Vérifier que le client existe
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec ID: " + clientId));
        
        // Chercher ou créer le panier
        Panier panier = panierRepository.findByClientIdAndActifTrue(clientId)
                .orElseGet(() -> creerNouveauPanier(client));
        
        return convertirPanierVersDto(panier);
    }
    
    @Override
    public PanierDto ajouterAuPanier(Long clientId, AjouterAuPanierDto dto) {
        log.info("Ajout au panier - Client: {}, Produit: {}, Quantité: {}", 
                clientId, dto.getProduitId(), dto.getQuantite());
        
        // Récupérer le panier
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        
        Panier panier = panierRepository.findByClientIdAndActifTrue(clientId)
                .orElseGet(() -> creerNouveauPanier(client));
        
        // Récupérer le produit
        Produit produit = produitRepository.findById(dto.getProduitId())
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec ID: " + dto.getProduitId()));
        
        // Vérifier le stock
        if (produit.getStock() < dto.getQuantite()) {
            throw new RuntimeException("Stock insuffisant. Stock disponible: " + produit.getStock());
        }
        
        // Chercher si l'item existe déjà avec les mêmes options
        Optional<PanierItem> itemExistant = panierItemRepository
                .findByPanierIdAndProduitIdAndOptions(
                        panier.getId(), 
                        dto.getProduitId(), 
                        dto.getTaille(), 
                        dto.getCouleur()
                );
        
        if (itemExistant.isPresent()) {
            // Incrémenter la quantité
            PanierItem item = itemExistant.get();
            int nouvelleQuantite = item.getQuantite() + dto.getQuantite();
            
            // Vérifier le stock pour la nouvelle quantité
            if (produit.getStock() < nouvelleQuantite) {
                throw new RuntimeException("Stock insuffisant. Stock disponible: " + produit.getStock());
            }
            
            item.setQuantite(nouvelleQuantite);
            panierItemRepository.save(item);
            log.info("Quantité incrémentée - Item ID: {}, Nouvelle quantité: {}", item.getId(), nouvelleQuantite);
        } else {
            // Créer un nouvel item
            PanierItem nouvelItem = new PanierItem();
            nouvelItem.setPanier(panier);
            nouvelItem.setProduit(produit);
            nouvelItem.setQuantite(dto.getQuantite());
            nouvelItem.setPrixUnitaire(produit.getPrix());
            nouvelItem.setTaille(dto.getTaille());
            nouvelItem.setCouleur(dto.getCouleur());
            nouvelItem.setOptionsPersonnalisation(dto.getOptionsPersonnalisation());
            nouvelItem.setDateAjout(LocalDateTime.now());
            
            panier.ajouterItem(nouvelItem);
            panierItemRepository.save(nouvelItem);
            log.info("Nouvel item ajouté au panier - Produit: {}", produit.getNom());
        }
        
        panier.setDateModification(LocalDateTime.now());
        panierRepository.save(panier);
        
        return convertirPanierVersDto(panier);
    }
    
    @Override
    public PanierDto modifierQuantite(Long clientId, Long itemId, Integer nouvelleQuantite) {
        log.info("Modification quantité - Client: {}, Item: {}, Nouvelle quantité: {}", 
                clientId, itemId, nouvelleQuantite);
        
        Panier panier = panierRepository.findByClientIdAndActifTrue(clientId)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));
        
        PanierItem item = panierItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item non trouvé"));
        
        // Vérifier que l'item appartient au bon panier
        if (!item.getPanier().getId().equals(panier.getId())) {
            throw new RuntimeException("Cet item n'appartient pas à votre panier");
        }
        
        // Vérifier le stock
        if (item.getProduit().getStock() < nouvelleQuantite) {
            throw new RuntimeException("Stock insuffisant. Stock disponible: " + item.getProduit().getStock());
        }
        
        if (nouvelleQuantite <= 0) {
            // Retirer l'item si quantité = 0
            return retirerDuPanier(clientId, itemId);
        }
        
        item.setQuantite(nouvelleQuantite);
        panierItemRepository.save(item);
        
        panier.setDateModification(LocalDateTime.now());
        panierRepository.save(panier);
        
        return convertirPanierVersDto(panier);
    }
    
    @Override
    public PanierDto retirerDuPanier(Long clientId, Long itemId) {
        log.info("Retrait du panier - Client: {}, Item: {}", clientId, itemId);
        
        Panier panier = panierRepository.findByClientIdAndActifTrue(clientId)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));
        
        PanierItem item = panierItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item non trouvé"));
        
        // Vérifier que l'item appartient au bon panier
        if (!item.getPanier().getId().equals(panier.getId())) {
            throw new RuntimeException("Cet item n'appartient pas à votre panier");
        }
        
        panier.retirerItem(item);
        panierItemRepository.delete(item);
        
        panier.setDateModification(LocalDateTime.now());
        panierRepository.save(panier);
        
        return convertirPanierVersDto(panier);
    }
    
    @Override
    public void viderPanier(Long clientId) {
        log.info("Vidage du panier - Client: {}", clientId);
        
        Panier panier = panierRepository.findByClientIdAndActifTrue(clientId)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));
        
        panier.vider();
        panierItemRepository.deleteByPanierId(panier.getId());
        
        panier.setDateModification(LocalDateTime.now());
        panierRepository.save(panier);
    }
    
    @Override
    public PanierDto synchroniserPanier(Long clientId) {
        log.info("Synchronisation du panier - Client: {}", clientId);
        
        Panier panier = panierRepository.findByClientIdAndActifTrue(clientId)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));
        
        boolean modifie = false;
        
        for (PanierItem item : panier.getItems()) {
            Produit produit = item.getProduit();
            
            // Vérifier et mettre à jour le prix
            if (!item.getPrixUnitaire().equals(produit.getPrix())) {
                log.info("Mise à jour du prix - Produit: {}, Ancien: {}, Nouveau: {}", 
                        produit.getNom(), item.getPrixUnitaire(), produit.getPrix());
                item.setPrixUnitaire(produit.getPrix());
                modifie = true;
            }
            
            // Vérifier le stock et ajuster la quantité si nécessaire
            if (item.getQuantite() > produit.getStock()) {
                log.warn("Stock insuffisant - Produit: {}, Demandé: {}, Disponible: {}", 
                        produit.getNom(), item.getQuantite(), produit.getStock());
                item.setQuantite(produit.getStock());
                modifie = true;
            }
            
            panierItemRepository.save(item);
        }
        
        if (modifie) {
            panier.setDateModification(LocalDateTime.now());
            panierRepository.save(panier);
        }
        
        return convertirPanierVersDto(panier);
    }
    
    @Override
    @Transactional(readOnly = true)
    public int obtenirNombreArticles(Long clientId) {
        Optional<Panier> panier = panierRepository.findByClientIdAndActifTrue(clientId);
        return panier.map(Panier::getNombreTotalArticles).orElse(0);
    }
    
    // ===================== MÉTHODES PRIVÉES =====================
    
    private Panier creerNouveauPanier(Client client) {
        log.info("Création d'un nouveau panier pour le client: {}", client.getEmail());
        
        Panier panier = new Panier();
        panier.setClient(client);
        panier.setDateCreation(LocalDateTime.now());
        panier.setActif(true);
        
        return panierRepository.save(panier);
    }
    
    private PanierDto convertirPanierVersDto(Panier panier) {
        PanierDto dto = new PanierDto();
        dto.setId(panier.getId());
        dto.setClientId(panier.getClient().getId());
        dto.setClientNom(panier.getClient().getNom() + " " + panier.getClient().getPrenom());
        dto.setDateCreation(panier.getDateCreation());
        dto.setDateModification(panier.getDateModification());
        dto.setActif(panier.isActif());
        
        // Convertir les items
        dto.setItems(panier.getItems().stream()
                .map(this::convertirItemVersDto)
                .collect(Collectors.toList()));
        
        // Calculer les totaux
        dto.setMontantTotal(panier.getMontantTotal());
        dto.setNombreTotalArticles(panier.getNombreTotalArticles());
        
        return dto;
    }
    
    private PanierItemDto convertirItemVersDto(PanierItem item) {
        PanierItemDto dto = new PanierItemDto();
        dto.setId(item.getId());
        dto.setProduitId(item.getProduit().getId());
        dto.setProduitNom(item.getProduit().getNom());
        dto.setProduitDescription(item.getProduit().getDescription());
        dto.setProduitPhotos(item.getProduit().getPhotos());
        dto.setPrixUnitaire(item.getPrixUnitaire());
        dto.setQuantite(item.getQuantite());
        dto.setTaille(item.getTaille());
        dto.setCouleur(item.getCouleur());
        dto.setOptionsPersonnalisation(item.getOptionsPersonnalisation());
        dto.setSousTotal(item.getSousTotal());
        dto.setDateAjout(item.getDateAjout());
        dto.setStockDisponible(item.getProduit().getStock());
        
        if (item.getProduit().getVendeur() != null) {
            dto.setVendeurNom(item.getProduit().getVendeur().getNom() + " " + 
                             item.getProduit().getVendeur().getPrenom());
            dto.setNomBoutique(item.getProduit().getVendeur().getNomBoutique());
        }
        
        return dto;
    }
}



