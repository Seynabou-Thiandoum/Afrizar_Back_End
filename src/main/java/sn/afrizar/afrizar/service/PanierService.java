package sn.afrizar.afrizar.service;

import sn.afrizar.afrizar.dto.AjouterAuPanierDto;
import sn.afrizar.afrizar.dto.PanierDto;

public interface PanierService {
    
    /**
     * Obtenir ou créer le panier d'un client
     */
    PanierDto obtenirPanierClient(Long clientId);
    
    /**
     * Ajouter un produit au panier
     */
    PanierDto ajouterAuPanier(Long clientId, AjouterAuPanierDto dto);
    
    /**
     * Modifier la quantité d'un item du panier
     */
    PanierDto modifierQuantite(Long clientId, Long itemId, Integer nouvelleQuantite);
    
    /**
     * Retirer un item du panier
     */
    PanierDto retirerDuPanier(Long clientId, Long itemId);
    
    /**
     * Vider le panier
     */
    void viderPanier(Long clientId);
    
    /**
     * Synchroniser le panier (vérifier stock, prix)
     */
    PanierDto synchroniserPanier(Long clientId);
    
    /**
     * Obtenir le nombre d'articles dans le panier
     */
    int obtenirNombreArticles(Long clientId);
}



