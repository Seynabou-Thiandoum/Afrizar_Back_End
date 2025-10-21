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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraisLivraisonServiceImpl implements FraisLivraisonService {
    
    private final FraisLivraisonRepository fraisLivraisonRepository;
    
    @Override
    @Transactional
    public FraisLivraisonDto creerFraisLivraison(FraisLivraisonDto fraisLivraisonDto) {
        log.info("Création des frais de livraison: {}", fraisLivraisonDto.getNom());
        
        FraisLivraison fraisLivraison = convertirDtoVersEntity(fraisLivraisonDto);
        fraisLivraison.setDateCreation(LocalDateTime.now());
        
        FraisLivraison fraisLivraisonSauvegarde = fraisLivraisonRepository.save(fraisLivraison);
        log.info("✅ Frais de livraison créés avec l'ID: {}", fraisLivraisonSauvegarde.getId());
        
        return convertirEntityVersDto(fraisLivraisonSauvegarde);
    }
    
    @Override
    public FraisLivraisonDto obtenirFraisLivraison(Long id) {
        log.info("Récupération des frais de livraison ID: {}", id);
        
        FraisLivraison fraisLivraison = fraisLivraisonRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Frais de livraison non trouvé avec l'ID: " + id));
        
        return convertirEntityVersDto(fraisLivraison);
    }
    
    @Override
    public Page<FraisLivraisonDto> obtenirTousLesFraisLivraison(Pageable pageable) {
        log.info("Récupération de tous les frais de livraison");
        
        Page<FraisLivraison> fraisLivraisonPage = fraisLivraisonRepository.findAll(pageable);
        return fraisLivraisonPage.map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional
    public FraisLivraisonDto mettreAJourFraisLivraison(Long id, FraisLivraisonDto fraisLivraisonDto) {
        log.info("Mise à jour des frais de livraison ID: {}", id);
        
        FraisLivraison fraisLivraison = fraisLivraisonRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Frais de livraison non trouvé avec l'ID: " + id));
        
        // Mettre à jour les champs
        fraisLivraison.setNom(fraisLivraisonDto.getNom());
        fraisLivraison.setDescription(fraisLivraisonDto.getDescription());
        fraisLivraison.setType(FraisLivraison.TypeLivraison.valueOf(fraisLivraisonDto.getType()));
        fraisLivraison.setFrais(fraisLivraisonDto.getFrais());
        fraisLivraison.setDelaiMinJours(fraisLivraisonDto.getDelaiMinJours());
        fraisLivraison.setDelaiMaxJours(fraisLivraisonDto.getDelaiMaxJours());
        fraisLivraison.setActif(fraisLivraisonDto.getActif());
        fraisLivraison.setPoidsMin(fraisLivraisonDto.getPoidsMin());
        fraisLivraison.setPoidsMax(fraisLivraisonDto.getPoidsMax());
        fraisLivraison.setZone(fraisLivraisonDto.getZone());
        fraisLivraison.setDateModification(LocalDateTime.now());
        
        FraisLivraison fraisLivraisonMisAJour = fraisLivraisonRepository.save(fraisLivraison);
        log.info("✅ Frais de livraison mis à jour");
        
        return convertirEntityVersDto(fraisLivraisonMisAJour);
    }
    
    @Override
    @Transactional
    public void supprimerFraisLivraison(Long id) {
        log.info("Suppression des frais de livraison ID: {}", id);
        
        if (!fraisLivraisonRepository.existsById(id)) {
            throw new RuntimeException("Frais de livraison non trouvé avec l'ID: " + id);
        }
        
        fraisLivraisonRepository.deleteById(id);
        log.info("✅ Frais de livraison supprimés");
    }
    
    @Override
    public List<FraisLivraisonDto> obtenirFraisLivraisonActifs() {
        log.info("Récupération des frais de livraison actifs");
        
        List<FraisLivraison> fraisLivraisonList = fraisLivraisonRepository.findByActifTrueOrderByTypeAsc();
        return fraisLivraisonList.stream()
            .map(this::convertirEntityVersDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<FraisLivraisonDto> obtenirFraisLivraisonParType(String type) {
        log.info("Récupération des frais de livraison par type: {}", type);
        
        FraisLivraison.TypeLivraison typeLivraison = FraisLivraison.TypeLivraison.valueOf(type);
        List<FraisLivraison> fraisLivraisonList = fraisLivraisonRepository.findByTypeAndActifTrue(typeLivraison);
        
        return fraisLivraisonList.stream()
            .map(this::convertirEntityVersDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<FraisLivraisonDto> obtenirFraisLivraisonApplicables(BigDecimal poids, String zone) {
        log.info("Récupération des frais de livraison applicables - Poids: {}, Zone: {}", poids, zone);
        
        List<FraisLivraison> fraisLivraisonList;
        
        if (zone != null && !zone.isEmpty()) {
            fraisLivraisonList = fraisLivraisonRepository.findByZoneAndActifTrue(zone);
        } else {
            fraisLivraisonList = fraisLivraisonRepository.findApplicableByPoids(poids);
        }
        
        return fraisLivraisonList.stream()
            .map(this::convertirEntityVersDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public FraisLivraisonDto activerFraisLivraison(Long id) {
        log.info("Activation des frais de livraison ID: {}", id);
        
        FraisLivraison fraisLivraison = fraisLivraisonRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Frais de livraison non trouvé avec l'ID: " + id));
        
        fraisLivraison.setActif(true);
        fraisLivraison.setDateModification(LocalDateTime.now());
        
        FraisLivraison fraisLivraisonActive = fraisLivraisonRepository.save(fraisLivraison);
        log.info("✅ Frais de livraison activés");
        
        return convertirEntityVersDto(fraisLivraisonActive);
    }
    
    @Override
    @Transactional
    public FraisLivraisonDto desactiverFraisLivraison(Long id) {
        log.info("Désactivation des frais de livraison ID: {}", id);
        
        FraisLivraison fraisLivraison = fraisLivraisonRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Frais de livraison non trouvé avec l'ID: " + id));
        
        fraisLivraison.setActif(false);
        fraisLivraison.setDateModification(LocalDateTime.now());
        
        FraisLivraison fraisLivraisonDesactive = fraisLivraisonRepository.save(fraisLivraison);
        log.info("✅ Frais de livraison désactivés");
        
        return convertirEntityVersDto(fraisLivraisonDesactive);
    }
    
    @Override
    public List<FraisLivraisonDto> obtenirOptionsLivraison() {
        log.info("Récupération des options de livraison pour les clients");
        
        List<FraisLivraison> fraisLivraisonList = fraisLivraisonRepository.findByActifTrueOrderByTypeAsc();
        return fraisLivraisonList.stream()
            .map(this::convertirEntityVersDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public FraisLivraisonDto calculerFraisLivraison(String type, BigDecimal poids, String zone) {
        log.info("Calcul des frais de livraison - Type: {}, Poids: {}, Zone: {}", type, poids, zone);
        
        FraisLivraison.TypeLivraison typeLivraison = FraisLivraison.TypeLivraison.valueOf(type);
        List<FraisLivraison> fraisLivraisonList = fraisLivraisonRepository.findByTypeAndActifTrue(typeLivraison);
        
        // Trouver le frais applicable selon le poids et la zone
        FraisLivraison fraisApplicable = fraisLivraisonList.stream()
            .filter(frais -> {
                boolean poidsOk = frais.getPoidsMin() == null || frais.getPoidsMin().compareTo(poids) <= 0;
                boolean poidsMaxOk = frais.getPoidsMax() == null || frais.getPoidsMax().compareTo(poids) >= 0;
                boolean zoneOk = frais.getZone() == null || frais.getZone().equals(zone);
                return poidsOk && poidsMaxOk && zoneOk;
            })
            .findFirst()
            .orElse(fraisLivraisonList.get(0)); // Fallback sur le premier disponible
        
        return convertirEntityVersDto(fraisApplicable);
    }
    
    // Méthodes de conversion
    private FraisLivraisonDto convertirEntityVersDto(FraisLivraison fraisLivraison) {
        FraisLivraisonDto dto = new FraisLivraisonDto();
        dto.setId(fraisLivraison.getId());
        dto.setNom(fraisLivraison.getNom());
        dto.setDescription(fraisLivraison.getDescription());
        dto.setType(fraisLivraison.getType().name());
        dto.setTypeNom(fraisLivraison.getType().getNom());
        dto.setTypeDescription(fraisLivraison.getType().getDescription());
        dto.setFrais(fraisLivraison.getFrais());
        dto.setDelaiMinJours(fraisLivraison.getDelaiMinJours());
        dto.setDelaiMaxJours(fraisLivraison.getDelaiMaxJours());
        dto.setActif(fraisLivraison.getActif());
        dto.setDateCreation(fraisLivraison.getDateCreation());
        dto.setDateModification(fraisLivraison.getDateModification());
        dto.setPoidsMin(fraisLivraison.getPoidsMin());
        dto.setPoidsMax(fraisLivraison.getPoidsMax());
        dto.setZone(fraisLivraison.getZone());
        return dto;
    }
    
    private FraisLivraison convertirDtoVersEntity(FraisLivraisonDto dto) {
        FraisLivraison fraisLivraison = new FraisLivraison();
        fraisLivraison.setId(dto.getId());
        fraisLivraison.setNom(dto.getNom());
        fraisLivraison.setDescription(dto.getDescription());
        fraisLivraison.setType(FraisLivraison.TypeLivraison.valueOf(dto.getType()));
        fraisLivraison.setFrais(dto.getFrais());
        fraisLivraison.setDelaiMinJours(dto.getDelaiMinJours());
        fraisLivraison.setDelaiMaxJours(dto.getDelaiMaxJours());
        fraisLivraison.setActif(dto.getActif());
        fraisLivraison.setPoidsMin(dto.getPoidsMin());
        fraisLivraison.setPoidsMax(dto.getPoidsMax());
        fraisLivraison.setZone(dto.getZone());
        return fraisLivraison;
    }
}


