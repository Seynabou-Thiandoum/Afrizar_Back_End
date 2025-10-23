package sn.afrizar.afrizar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.afrizar.afrizar.dto.ActualiteDto;
import sn.afrizar.afrizar.dto.CreateActualiteDto;
import sn.afrizar.afrizar.model.Actualite;
import sn.afrizar.afrizar.repository.ActualiteRepository;
import sn.afrizar.afrizar.service.ActualiteService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ActualiteServiceImpl implements ActualiteService {
    
    private final ActualiteRepository actualiteRepository;
    
    @Override
    public ActualiteDto creerActualite(CreateActualiteDto createActualiteDto) {
        log.info("Création d'une nouvelle actualité: {}", createActualiteDto.getTitre());
        
        Actualite actualite = convertirCreateDtoVersEntity(createActualiteDto);
        actualite.setDatePublication(LocalDateTime.now());
        actualite.setDateCreation(LocalDateTime.now());
        
        Actualite actualiteSauvee = actualiteRepository.save(actualite);
        
        log.info("Actualité créée avec succès avec ID: {}", actualiteSauvee.getId());
        return convertirEntityVersDto(actualiteSauvee);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ActualiteDto> obtenirActualiteParId(Long id) {
        log.info("Récupération de l'actualité avec ID: {}", id);
        
        return actualiteRepository.findById(id)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ActualiteDto> obtenirActualitesPubliques(Pageable pageable) {
        log.info("Récupération des actualités publiques");
        
        Page<Actualite> actualites = actualiteRepository.findByEstVisibleTrueOrderByDatePublicationDesc(pageable);
        return actualites.map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ActualiteDto> obtenirActualitesParCategorie(String categorie, Pageable pageable) {
        log.info("Récupération des actualités pour la catégorie: {}", categorie);
        
        Page<Actualite> actualites = actualiteRepository.findByEstVisibleTrueAndCategorieOrderByDatePublicationDesc(categorie, pageable);
        return actualites.map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ActualiteDto> obtenirActualitesTendance() {
        log.info("Récupération des actualités tendance");
        
        List<Actualite> actualites = actualiteRepository.findByEstVisibleTrueAndEstTendanceTrueOrderByDatePublicationDesc();
        return actualites.stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ActualiteDto> obtenirActualitesRecentes(int limit) {
        log.info("Récupération des {} actualités récentes", limit);
        
        Pageable pageable = PageRequest.of(0, limit);
        List<Actualite> actualites = actualiteRepository.findRecentActualites(pageable);
        return actualites.stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ActualiteDto> rechercherActualites(String searchTerm, Pageable pageable) {
        log.info("Recherche d'actualités avec le terme: {}", searchTerm);
        
        Page<Actualite> actualites = actualiteRepository.searchActualites(searchTerm, pageable);
        return actualites.map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ActualiteDto> obtenirActualitesPlusLikees(Pageable pageable) {
        log.info("Récupération des actualités les plus likées");
        
        Page<Actualite> actualites = actualiteRepository.findMostLikedActualites(pageable);
        return actualites.map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ActualiteDto> obtenirToutesLesActualites(Pageable pageable) {
        log.info("Récupération de toutes les actualités pour l'admin");
        
        Page<Actualite> actualites = actualiteRepository.findAll(pageable);
        return actualites.map(this::convertirEntityVersDto);
    }
    
    @Override
    public ActualiteDto mettreAJourActualite(Long id, CreateActualiteDto createActualiteDto) {
        log.info("Mise à jour de l'actualité avec ID: {}", id);
        
        Actualite actualite = actualiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actualité non trouvée avec ID: " + id));
        
        // Mise à jour des champs
        actualite.setTitre(createActualiteDto.getTitre());
        actualite.setResume(createActualiteDto.getResume());
        actualite.setContenu(createActualiteDto.getContenu());
        actualite.setImageUrl(createActualiteDto.getImageUrl());
        actualite.setAuteur(createActualiteDto.getAuteur());
        actualite.setCategorie(createActualiteDto.getCategorie());
        actualite.setTags(createActualiteDto.getTags());
        actualite.setEstVisible(createActualiteDto.getEstVisible());
        actualite.setEstTendance(createActualiteDto.getEstTendance());
        actualite.setDateModification(LocalDateTime.now());
        
        Actualite actualiteSauvee = actualiteRepository.save(actualite);
        
        log.info("Actualité mise à jour avec succès");
        return convertirEntityVersDto(actualiteSauvee);
    }
    
    @Override
    public void supprimerActualite(Long id) {
        log.info("Suppression de l'actualité avec ID: {}", id);
        
        Actualite actualite = actualiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actualité non trouvée avec ID: " + id));
        
        actualiteRepository.delete(actualite);
        
        log.info("Actualité supprimée avec succès");
    }
    
    @Override
    public ActualiteDto changerVisibiliteActualite(Long id, boolean visible) {
        log.info("Changement de visibilité de l'actualité {} vers {}", id, visible);
        
        Actualite actualite = actualiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actualité non trouvée avec ID: " + id));
        
        actualite.setEstVisible(visible);
        actualite.setDateModification(LocalDateTime.now());
        
        Actualite actualiteSauvee = actualiteRepository.save(actualite);
        
        return convertirEntityVersDto(actualiteSauvee);
    }
    
    @Override
    public ActualiteDto marquerActualiteTendance(Long id, boolean tendance) {
        log.info("Marquage de l'actualité {} comme tendance: {}", id, tendance);
        
        Actualite actualite = actualiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actualité non trouvée avec ID: " + id));
        
        actualite.setEstTendance(tendance);
        actualite.setDateModification(LocalDateTime.now());
        
        Actualite actualiteSauvee = actualiteRepository.save(actualite);
        
        return convertirEntityVersDto(actualiteSauvee);
    }
    
    @Override
    public ActualiteDto incrementerLikes(Long id) {
        log.info("Incrémentation des likes pour l'actualité {}", id);
        
        Actualite actualite = actualiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actualité non trouvée avec ID: " + id));
        
        actualite.setNombreLikes(actualite.getNombreLikes() + 1);
        
        Actualite actualiteSauvee = actualiteRepository.save(actualite);
        
        return convertirEntityVersDto(actualiteSauvee);
    }
    
    @Override
    public ActualiteDto incrementerCommentaires(Long id) {
        log.info("Incrémentation des commentaires pour l'actualité {}", id);
        
        Actualite actualite = actualiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actualité non trouvée avec ID: " + id));
        
        actualite.setNombreCommentaires(actualite.getNombreCommentaires() + 1);
        
        Actualite actualiteSauvee = actualiteRepository.save(actualite);
        
        return convertirEntityVersDto(actualiteSauvee);
    }
    
    @Override
    public ActualiteDto incrementerPartages(Long id) {
        log.info("Incrémentation des partages pour l'actualité {}", id);
        
        Actualite actualite = actualiteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actualité non trouvée avec ID: " + id));
        
        actualite.setNombrePartages(actualite.getNombrePartages() + 1);
        
        Actualite actualiteSauvee = actualiteRepository.save(actualite);
        
        return convertirEntityVersDto(actualiteSauvee);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long compterActualites() {
        return actualiteRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long compterActualitesVisibles() {
        return actualiteRepository.countByEstVisibleTrue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long compterActualitesTendance() {
        return actualiteRepository.findByEstVisibleTrueAndEstTendanceTrueOrderByDatePublicationDesc().size();
    }
    
    // Méthodes de conversion
    private ActualiteDto convertirEntityVersDto(Actualite actualite) {
        ActualiteDto dto = new ActualiteDto();
        dto.setId(actualite.getId());
        dto.setTitre(actualite.getTitre());
        dto.setResume(actualite.getResume());
        dto.setContenu(actualite.getContenu());
        dto.setImageUrl(actualite.getImageUrl());
        dto.setAuteur(actualite.getAuteur());
        dto.setDatePublication(actualite.getDatePublication());
        dto.setCategorie(actualite.getCategorie());
        dto.setTags(actualite.getTags());
        dto.setNombreLikes(actualite.getNombreLikes());
        dto.setNombreCommentaires(actualite.getNombreCommentaires());
        dto.setNombrePartages(actualite.getNombrePartages());
        dto.setEstVisible(actualite.getEstVisible());
        dto.setEstTendance(actualite.getEstTendance());
        dto.setDateCreation(actualite.getDateCreation());
        dto.setDateModification(actualite.getDateModification());
        return dto;
    }
    
    private Actualite convertirCreateDtoVersEntity(CreateActualiteDto dto) {
        Actualite actualite = new Actualite();
        actualite.setTitre(dto.getTitre());
        actualite.setResume(dto.getResume());
        actualite.setContenu(dto.getContenu());
        actualite.setImageUrl(dto.getImageUrl());
        actualite.setAuteur(dto.getAuteur());
        actualite.setCategorie(dto.getCategorie());
        actualite.setTags(dto.getTags());
        actualite.setEstVisible(dto.getEstVisible() != null ? dto.getEstVisible() : true);
        actualite.setEstTendance(dto.getEstTendance() != null ? dto.getEstTendance() : false);
        actualite.setNombreLikes(0L);
        actualite.setNombreCommentaires(0L);
        actualite.setNombrePartages(0L);
        return actualite;
    }
}
