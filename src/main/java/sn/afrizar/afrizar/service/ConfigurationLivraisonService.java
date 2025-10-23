package sn.afrizar.afrizar.service;

import sn.afrizar.afrizar.dto.ConfigurationLivraisonDto;
import sn.afrizar.afrizar.model.Livraison;

import java.math.BigDecimal;
import java.util.List;

public interface ConfigurationLivraisonService {
    
    /**
     * Créer une nouvelle configuration de livraison
     */
    ConfigurationLivraisonDto creerConfiguration(ConfigurationLivraisonDto dto, String emailAdmin);
    
    /**
     * Mettre à jour une configuration existante
     */
    ConfigurationLivraisonDto mettreAJourConfiguration(Long id, ConfigurationLivraisonDto dto, String emailAdmin);
    
    /**
     * Obtenir toutes les configurations actives
     */
    List<ConfigurationLivraisonDto> obtenirConfigurationsActives();
    
    /**
     * Obtenir toutes les configurations (actives et inactives)
     */
    List<ConfigurationLivraisonDto> obtenirToutesConfigurations();
    
    /**
     * Obtenir les configurations pour un pays spécifique
     */
    List<ConfigurationLivraisonDto> obtenirConfigurationsParPays(String pays);
    
    /**
     * Obtenir les configurations pour un type de livraison spécifique
     */
    List<ConfigurationLivraisonDto> obtenirConfigurationsParType(Livraison.TypeLivraison type);
    
    /**
     * Obtenir une configuration spécifique par ID
     */
    ConfigurationLivraisonDto obtenirConfigurationParId(Long id);
    
    /**
     * Activer/désactiver une configuration
     */
    ConfigurationLivraisonDto toggleActif(Long id, String emailAdmin);
    
    /**
     * Supprimer une configuration
     */
    void supprimerConfiguration(Long id);
    
    /**
     * Obtenir les tarifs pour un pays et un type de livraison
     */
    BigDecimal obtenirTarifLivraison(String pays, Livraison.TypeLivraison type, BigDecimal poids);
    
    /**
     * Obtenir le délai de livraison pour un pays et un type
     */
    Integer obtenirDelaiLivraison(String pays, Livraison.TypeLivraison type);
    
    /**
     * Initialiser les configurations par défaut
     */
    void initialiserConfigurationsParDefaut();
}
