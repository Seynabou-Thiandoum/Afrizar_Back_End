package sn.afrizar.afrizar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.afrizar.afrizar.dto.FraisLivraisonDto;
import sn.afrizar.afrizar.model.FraisLivraison;
import sn.afrizar.afrizar.repository.FraisLivraisonRepository;
import sn.afrizar.afrizar.service.FraisLivraisonService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FraisLivraisonServiceImpl implements FraisLivraisonService {
    
    private final FraisLivraisonRepository fraisLivraisonRepository;
    
    @Override
    public FraisLivraisonDto creerFraisLivraison(FraisLivraisonDto fraisLivraisonDto) {
        log.info("Création d'un nouveau frais de livraison: {}", fraisLivraisonDto.getNom());
        
        FraisLivraison fraisLivraison = convertirDtoVersEntity(fraisLivraisonDto);
        FraisLivraison fraisLivraisonSauvegarde = fraisLivraisonRepository.save(fraisLivraison);
        
        log.info("Frais de livraison créé avec succès avec ID: {}", fraisLivraisonSauvegarde.getId());
        return convertirEntityVersDto(fraisLivraisonSauvegarde);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<FraisLivraisonDto> obtenirFraisLivraisonParId(Long id) {
        return fraisLivraisonRepository.findById(id)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<FraisLivraisonDto> obtenirTousLesFraisLivraison(Pageable pageable) {
        log.info("Récupération de tous les frais de livraison avec pagination");
        
        Page<FraisLivraison> fraisLivraisonPage = fraisLivraisonRepository.findByActifTrueOrderByType(pageable);
        return fraisLivraisonPage.map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FraisLivraisonDto> obtenirFraisLivraisonActifs() {
        log.info("Récupération de tous les frais de livraison actifs");
        
        List<FraisLivraison> fraisLivraisonList = fraisLivraisonRepository.findByActifTrueOrderByType();
        return fraisLivraisonList.stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public FraisLivraisonDto mettreAJourFraisLivraison(Long id, FraisLivraisonDto fraisLivraisonDto) {
        log.info("Mise à jour du frais de livraison avec ID: {}", id);
        
        FraisLivraison fraisLivraison = fraisLivraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Frais de livraison non trouvé avec ID: " + id));
        
        // Mettre à jour les champs
        fraisLivraison.setNom(fraisLivraisonDto.getNom());
        fraisLivraison.setDescription(fraisLivraisonDto.getDescription());
        fraisLivraison.setType(fraisLivraisonDto.getType());
        fraisLivraison.setFrais(fraisLivraisonDto.getFrais());
        fraisLivraison.setDelaiMinJours(fraisLivraisonDto.getDelaiMinJours());
        fraisLivraison.setDelaiMaxJours(fraisLivraisonDto.getDelaiMaxJours());
        fraisLivraison.setPoidsMin(fraisLivraisonDto.getPoidsMin());
        fraisLivraison.setPoidsMax(fraisLivraisonDto.getPoidsMax());
        fraisLivraison.setZone(fraisLivraisonDto.getZone());
        
        FraisLivraison fraisLivraisonModifie = fraisLivraisonRepository.save(fraisLivraison);
        
        log.info("Frais de livraison mis à jour avec succès");
        return convertirEntityVersDto(fraisLivraisonModifie);
    }
    
    @Override
    public void supprimerFraisLivraison(Long id) {
        log.info("Suppression du frais de livraison avec ID: {}", id);
        
        if (!fraisLivraisonRepository.existsById(id)) {
            throw new RuntimeException("Frais de livraison non trouvé avec ID: " + id);
        }
        
        fraisLivraisonRepository.deleteById(id);
        log.info("Frais de livraison supprimé avec succès");
    }
    
    @Override
    public FraisLivraisonDto activerFraisLivraison(Long id) {
        log.info("Activation du frais de livraison avec ID: {}", id);
        
        FraisLivraison fraisLivraison = fraisLivraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Frais de livraison non trouvé avec ID: " + id));
        
        fraisLivraison.setActif(true);
        FraisLivraison fraisLivraisonActive = fraisLivraisonRepository.save(fraisLivraison);
        
        log.info("Frais de livraison activé avec succès");
        return convertirEntityVersDto(fraisLivraisonActive);
    }
    
    @Override
    public FraisLivraisonDto desactiverFraisLivraison(Long id) {
        log.info("Désactivation du frais de livraison avec ID: {}", id);
        
        FraisLivraison fraisLivraison = fraisLivraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Frais de livraison non trouvé avec ID: " + id));
        
        fraisLivraison.setActif(false);
        FraisLivraison fraisLivraisonDesactive = fraisLivraisonRepository.save(fraisLivraison);
        
        log.info("Frais de livraison désactivé avec succès");
        return convertirEntityVersDto(fraisLivraisonDesactive);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FraisLivraisonDto> obtenirFraisLivraisonParType(FraisLivraison.TypeLivraison type) {
        log.info("Récupération des frais de livraison pour le type: {}", type);
        
        List<FraisLivraison> fraisLivraisonList = fraisLivraisonRepository.findByTypeAndActifTrue(type);
        return fraisLivraisonList.stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FraisLivraisonDto> obtenirFraisLivraisonApplicables(Double poids, String zone) {
        log.info("Récupération des frais de livraison applicables pour poids: {} kg, zone: {}", poids, zone);
        
        List<FraisLivraison> fraisLivraisonList = fraisLivraisonRepository.findApplicableFraisLivraison(poids, zone);
        return fraisLivraisonList.stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    private FraisLivraison convertirDtoVersEntity(FraisLivraisonDto dto) {
        FraisLivraison entity = new FraisLivraison();
        entity.setId(dto.getId());
        entity.setNom(dto.getNom());
        entity.setDescription(dto.getDescription());
        entity.setType(dto.getType());
        entity.setFrais(dto.getFrais());
        entity.setDelaiMinJours(dto.getDelaiMinJours());
        entity.setDelaiMaxJours(dto.getDelaiMaxJours());
        entity.setActif(dto.getActif() != null ? dto.getActif() : true);
        entity.setPoidsMin(dto.getPoidsMin());
        entity.setPoidsMax(dto.getPoidsMax());
        entity.setZone(dto.getZone());
        return entity;
    }
    
    private FraisLivraisonDto convertirEntityVersDto(FraisLivraison entity) {
        FraisLivraisonDto dto = new FraisLivraisonDto();
        dto.setId(entity.getId());
        dto.setNom(entity.getNom());
        dto.setDescription(entity.getDescription());
        dto.setType(entity.getType());
        dto.setTypeNom(entity.getType().getDisplayName());
        dto.setTypeDescription("Livraison " + entity.getType().getDisplayName().toLowerCase());
        dto.setFrais(entity.getFrais());
        dto.setDelaiMinJours(entity.getDelaiMinJours());
        dto.setDelaiMaxJours(entity.getDelaiMaxJours());
        dto.setActif(entity.getActif());
        dto.setDateCreation(entity.getDateCreation());
        dto.setDateModification(entity.getDateModification());
        dto.setPoidsMin(entity.getPoidsMin());
        dto.setPoidsMax(entity.getPoidsMax());
        dto.setZone(entity.getZone());
        return dto;
    }
}
