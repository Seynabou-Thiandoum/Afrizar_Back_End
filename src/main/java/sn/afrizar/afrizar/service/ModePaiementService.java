package sn.afrizar.afrizar.service;

import sn.afrizar.afrizar.dto.ModePaiementConfigDto;
import sn.afrizar.afrizar.dto.ModePaiementCreateDto;
import sn.afrizar.afrizar.dto.ModePaiementDto;
import sn.afrizar.afrizar.dto.ModePaiementUpdateDto;
import sn.afrizar.afrizar.model.ModePaiement;

import java.util.List;

public interface ModePaiementService {
    
    /**
     * Récupère tous les modes de paiement (admin)
     */
    List<ModePaiementDto> getAllModesPaiement();
    
    /**
     * Récupère tous les modes de paiement actifs (clients)
     */
    List<ModePaiementDto> getActivesModesPaiement();
    
    /**
     * Récupère un mode de paiement par ID
     */
    ModePaiementDto getModePaiementById(Long id);
    
    /**
     * Récupère un mode de paiement par code
     */
    ModePaiementDto getModePaiementByCode(String code);
    
    /**
     * Crée un nouveau mode de paiement
     */
    ModePaiementDto createModePaiement(ModePaiementCreateDto dto, String adminUsername);
    
    /**
     * Met à jour un mode de paiement
     */
    ModePaiementDto updateModePaiement(Long id, ModePaiementUpdateDto dto, String adminUsername);
    
    /**
     * Met à jour la configuration d'un mode de paiement
     */
    void updateConfiguration(Long id, ModePaiementConfigDto configDto, String adminUsername);
    
    /**
     * Active ou désactive un mode de paiement
     */
    void toggleActif(Long id, Boolean actif, String adminUsername);
    
    /**
     * Supprime un mode de paiement
     */
    void deleteModePaiement(Long id);
    
    /**
     * Réorganise les modes de paiement
     */
    void reorderModesPaiement(List<Long> orderedIds);
    
    /**
     * Récupère les modes de paiement par type
     */
    List<ModePaiementDto> getModesPaiementByType(ModePaiement.TypePaiement type);
}

