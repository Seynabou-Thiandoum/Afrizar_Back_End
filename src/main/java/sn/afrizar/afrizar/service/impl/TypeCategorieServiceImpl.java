package sn.afrizar.afrizar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.afrizar.afrizar.dto.TypeCategorieDto;
import sn.afrizar.afrizar.model.TypeCategorie;
import sn.afrizar.afrizar.model.TypeCategorieEnum;
import sn.afrizar.afrizar.repository.TypeCategorieRepository;
import sn.afrizar.afrizar.service.TypeCategorieService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TypeCategorieServiceImpl implements TypeCategorieService {
    
    private final TypeCategorieRepository typeCategorieRepository;
    
    @Override
    public TypeCategorieDto creerType(TypeCategorieDto typeDto) {
        log.info("Création d'un nouveau type: {}", typeDto.getNom());
        
        if (typeCategorieRepository.existsByNomAndActiveTrue(typeDto.getNom())) {
            throw new RuntimeException("Un type avec ce nom existe déjà");
        }
        
        TypeCategorie type = convertirDtoVersEntity(typeDto);
        type.setActive(true);
        
        TypeCategorie typeSauvegardee = typeCategorieRepository.save(type);
        
        log.info("Type créé avec succès avec ID: {}", typeSauvegardee.getId());
        return convertirEntityVersDto(typeSauvegardee);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TypeCategorieDto obtenirTypeParId(Long id) {
        return typeCategorieRepository.findById(id)
                .map(this::convertirEntityVersDto)
                .orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TypeCategorieDto> obtenirTousLesTypesActifs() {
        return typeCategorieRepository.findByActiveTrueOrderByOrdre()
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TypeCategorieDto> obtenirTypesParType(String type) {
        TypeCategorieEnum typeEnum = TypeCategorieEnum.valueOf(type);
        return typeCategorieRepository.findByTypeAndActiveTrueOrderByOrdre(typeEnum)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TypeCategorieDto> rechercherTypesParNom(String nom) {
        return typeCategorieRepository.findByNomContainingIgnoreCaseAndActiveTrue(nom)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TypeCategorieDto> obtenirTypesParUsage() {
        return typeCategorieRepository.findTypesOrderByUsage()
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public TypeCategorieDto mettreAJourType(Long id, TypeCategorieDto typeDto) {
        log.info("Mise à jour du type avec ID: {}", id);
        
        TypeCategorie type = typeCategorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Type non trouvé avec ID: " + id));
        
        // Vérifier si le nom est déjà utilisé par un autre type
        if (!type.getNom().equals(typeDto.getNom()) && 
            typeCategorieRepository.existsByNomAndActiveTrue(typeDto.getNom())) {
            throw new RuntimeException("Un type avec ce nom existe déjà");
        }
        
        // Mettre à jour les champs
        type.setNom(typeDto.getNom());
        type.setDescription(typeDto.getDescription());
        type.setImageUrl(typeDto.getImageUrl());
        type.setOrdre(typeDto.getOrdre());
        type.setType(typeDto.getType() != null ? TypeCategorieEnum.valueOf(typeDto.getType()) : TypeCategorieEnum.VETEMENTS);
        
        TypeCategorie typeMiseAJour = typeCategorieRepository.save(type);
        
        log.info("Type mis à jour avec succès");
        return convertirEntityVersDto(typeMiseAJour);
    }
    
    @Override
    public void activerType(Long id) {
        log.info("Activation du type avec ID: {}", id);
        
        TypeCategorie type = typeCategorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Type non trouvé avec ID: " + id));
        
        type.setActive(true);
        typeCategorieRepository.save(type);
        
        log.info("Type activé avec succès");
    }
    
    @Override
    public void desactiverType(Long id) {
        log.info("Désactivation du type avec ID: {}", id);
        
        TypeCategorie type = typeCategorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Type non trouvé avec ID: " + id));
        
        type.setActive(false);
        typeCategorieRepository.save(type);
        
        log.info("Type désactivé avec succès");
    }
    
    @Override
    public void supprimerType(Long id) {
        log.info("Suppression du type avec ID: {}", id);
        
        TypeCategorie type = typeCategorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Type non trouvé avec ID: " + id));
        
        typeCategorieRepository.delete(type);
        log.info("Type supprimé avec succès");
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean verifierNomDisponible(String nom) {
        return !typeCategorieRepository.existsByNomAndActiveTrue(nom);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getNombreGenresParType(Long typeId) {
        return typeCategorieRepository.countGenresByType(typeId);
    }
    
    // Méthodes de conversion
    private TypeCategorieDto convertirEntityVersDto(TypeCategorie type) {
        TypeCategorieDto dto = new TypeCategorieDto();
        dto.setId(type.getId());
        dto.setNom(type.getNom());
        dto.setDescription(type.getDescription());
        dto.setType(type.getType() != null ? type.getType().name() : "VETEMENTS");
        dto.setImageUrl(type.getImageUrl());
        dto.setOrdre(type.getOrdre());
        dto.setActive(type.isActive());
        
        // Statistiques
        dto.setNombreGenres(typeCategorieRepository.countGenresByType(type.getId()).intValue());
        
        return dto;
    }
    
    private TypeCategorie convertirDtoVersEntity(TypeCategorieDto dto) {
        TypeCategorie type = new TypeCategorie();
        type.setId(dto.getId());
        type.setNom(dto.getNom());
        type.setDescription(dto.getDescription());
        type.setType(dto.getType() != null ? TypeCategorieEnum.valueOf(dto.getType()) : TypeCategorieEnum.VETEMENTS);
        type.setImageUrl(dto.getImageUrl());
        type.setOrdre(dto.getOrdre() != null ? dto.getOrdre() : 0);
        type.setActive(dto.isActive());
        return type;
    }
}
