package sn.afrizar.afrizar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.afrizar.afrizar.dto.ModePaiementConfigDto;
import sn.afrizar.afrizar.dto.ModePaiementCreateDto;
import sn.afrizar.afrizar.dto.ModePaiementDto;
import sn.afrizar.afrizar.dto.ModePaiementUpdateDto;
import sn.afrizar.afrizar.model.ModePaiement;
import sn.afrizar.afrizar.repository.ModePaiementRepository;
import sn.afrizar.afrizar.service.ModePaiementService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ModePaiementServiceImpl implements ModePaiementService {
    
    private final ModePaiementRepository modePaiementRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<ModePaiementDto> getAllModesPaiement() {
        return modePaiementRepository.findAllByOrderByOrdreAsc().stream()
                .map(ModePaiementDto::fromEntityForAdmin)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ModePaiementDto> getActivesModesPaiement() {
        return modePaiementRepository.findByActifTrueOrderByOrdreAsc().stream()
                .map(ModePaiementDto::fromEntityForClient)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ModePaiementDto getModePaiementById(Long id) {
        ModePaiement modePaiement = modePaiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mode de paiement non trouvé avec l'ID : " + id));
        return ModePaiementDto.fromEntityForAdmin(modePaiement);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ModePaiementDto getModePaiementByCode(String code) {
        ModePaiement modePaiement = modePaiementRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Mode de paiement non trouvé avec le code : " + code));
        return ModePaiementDto.fromEntityForClient(modePaiement);
    }
    
    @Override
    public ModePaiementDto createModePaiement(ModePaiementCreateDto dto, String adminUsername) {
        log.info("Création d'un nouveau mode de paiement : {} par {}", dto.getNom(), adminUsername);
        
        // Vérifier si le code existe déjà
        if (modePaiementRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Un mode de paiement avec ce code existe déjà : " + dto.getCode());
        }
        
        ModePaiement modePaiement = new ModePaiement();
        modePaiement.setNom(dto.getNom());
        modePaiement.setCode(dto.getCode());
        modePaiement.setType(dto.getType());
        modePaiement.setDescription(dto.getDescription());
        modePaiement.setLogo(dto.getLogo());
        modePaiement.setActif(dto.getActif());
        modePaiement.setConfiguration(dto.getConfiguration());
        modePaiement.setInstructions(dto.getInstructions());
        modePaiement.setFraisPourcentage(dto.getFraisPourcentage());
        modePaiement.setFraisFixe(dto.getFraisFixe());
        modePaiement.setMontantMinimum(dto.getMontantMinimum());
        modePaiement.setMontantMaximum(dto.getMontantMaximum());
        modePaiement.setPaysSupportes(dto.getPaysSupportes());
        modePaiement.setDelaiTraitement(dto.getDelaiTraitement());
        modePaiement.setOrdre(dto.getOrdre());
        modePaiement.setCallbackUrl(dto.getCallbackUrl());
        modePaiement.setEnvironnement(dto.getEnvironnement());
        modePaiement.setModifiePar(adminUsername);
        
        ModePaiement saved = modePaiementRepository.save(modePaiement);
        log.info("Mode de paiement créé avec succès : ID {}", saved.getId());
        
        return ModePaiementDto.fromEntityForAdmin(saved);
    }
    
    @Override
    public ModePaiementDto updateModePaiement(Long id, ModePaiementUpdateDto dto, String adminUsername) {
        log.info("Mise à jour du mode de paiement ID {} par {}", id, adminUsername);
        
        ModePaiement modePaiement = modePaiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mode de paiement non trouvé avec l'ID : " + id));
        
        // Vérifier si le nouveau code existe déjà (si changé)
        if (dto.getCode() != null && !dto.getCode().equals(modePaiement.getCode())) {
            if (modePaiementRepository.existsByCode(dto.getCode())) {
                throw new RuntimeException("Un mode de paiement avec ce code existe déjà : " + dto.getCode());
            }
            modePaiement.setCode(dto.getCode());
        }
        
        if (dto.getNom() != null) modePaiement.setNom(dto.getNom());
        if (dto.getType() != null) modePaiement.setType(dto.getType());
        if (dto.getDescription() != null) modePaiement.setDescription(dto.getDescription());
        if (dto.getLogo() != null) modePaiement.setLogo(dto.getLogo());
        if (dto.getActif() != null) modePaiement.setActif(dto.getActif());
        if (dto.getConfiguration() != null) modePaiement.setConfiguration(dto.getConfiguration());
        if (dto.getInstructions() != null) modePaiement.setInstructions(dto.getInstructions());
        if (dto.getFraisPourcentage() != null) modePaiement.setFraisPourcentage(dto.getFraisPourcentage());
        if (dto.getFraisFixe() != null) modePaiement.setFraisFixe(dto.getFraisFixe());
        if (dto.getMontantMinimum() != null) modePaiement.setMontantMinimum(dto.getMontantMinimum());
        if (dto.getMontantMaximum() != null) modePaiement.setMontantMaximum(dto.getMontantMaximum());
        if (dto.getPaysSupportes() != null) modePaiement.setPaysSupportes(dto.getPaysSupportes());
        if (dto.getDelaiTraitement() != null) modePaiement.setDelaiTraitement(dto.getDelaiTraitement());
        if (dto.getOrdre() != null) modePaiement.setOrdre(dto.getOrdre());
        if (dto.getCallbackUrl() != null) modePaiement.setCallbackUrl(dto.getCallbackUrl());
        if (dto.getEnvironnement() != null) modePaiement.setEnvironnement(dto.getEnvironnement());
        
        modePaiement.setDateModification(LocalDateTime.now());
        modePaiement.setModifiePar(adminUsername);
        
        ModePaiement updated = modePaiementRepository.save(modePaiement);
        log.info("Mode de paiement mis à jour avec succès : ID {}", updated.getId());
        
        return ModePaiementDto.fromEntityForAdmin(updated);
    }
    
    @Override
    public void updateConfiguration(Long id, ModePaiementConfigDto configDto, String adminUsername) {
        log.info("Mise à jour de la configuration du mode de paiement ID {} par {}", id, adminUsername);
        
        ModePaiement modePaiement = modePaiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mode de paiement non trouvé avec l'ID : " + id));
        
        modePaiement.setConfiguration(configDto.getConfiguration());
        if (configDto.getCallbackUrl() != null) {
            modePaiement.setCallbackUrl(configDto.getCallbackUrl());
        }
        modePaiement.setDateModification(LocalDateTime.now());
        modePaiement.setModifiePar(adminUsername);
        
        modePaiementRepository.save(modePaiement);
        log.info("Configuration mise à jour avec succès");
    }
    
    @Override
    public void toggleActif(Long id, Boolean actif, String adminUsername) {
        log.info("Changement du statut actif du mode de paiement ID {} à {} par {}", id, actif, adminUsername);
        
        ModePaiement modePaiement = modePaiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mode de paiement non trouvé avec l'ID : " + id));
        
        modePaiement.setActif(actif);
        modePaiement.setDateModification(LocalDateTime.now());
        modePaiement.setModifiePar(adminUsername);
        
        modePaiementRepository.save(modePaiement);
        log.info("Statut actif changé avec succès");
    }
    
    @Override
    public void deleteModePaiement(Long id) {
        log.info("Suppression du mode de paiement ID {}", id);
        
        ModePaiement modePaiement = modePaiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mode de paiement non trouvé avec l'ID : " + id));
        
        // Vérifier si le mode de paiement est utilisé dans des paiements
        // Cette logique peut être ajoutée selon vos besoins
        
        modePaiementRepository.delete(modePaiement);
        log.info("Mode de paiement supprimé avec succès");
    }
    
    @Override
    public void reorderModesPaiement(List<Long> orderedIds) {
        log.info("Réorganisation des modes de paiement");
        
        for (int i = 0; i < orderedIds.size(); i++) {
            Long id = orderedIds.get(i);
            ModePaiement modePaiement = modePaiementRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Mode de paiement non trouvé avec l'ID : " + id));
            
            modePaiement.setOrdre(i);
            modePaiementRepository.save(modePaiement);
        }
        
        log.info("Modes de paiement réorganisés avec succès");
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ModePaiementDto> getModesPaiementByType(ModePaiement.TypePaiement type) {
        return modePaiementRepository.findByTypeAndActifTrueOrderByOrdreAsc(type).stream()
                .map(ModePaiementDto::fromEntityForClient)
                .collect(Collectors.toList());
    }
}

