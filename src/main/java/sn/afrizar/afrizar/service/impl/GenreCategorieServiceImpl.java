package sn.afrizar.afrizar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.afrizar.afrizar.dto.GenreCategorieDto;
import sn.afrizar.afrizar.model.GenreCategorie;
import sn.afrizar.afrizar.model.TypeCategorieEnum;
import sn.afrizar.afrizar.repository.GenreCategorieRepository;
import sn.afrizar.afrizar.service.GenreCategorieService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GenreCategorieServiceImpl implements GenreCategorieService {
    
    private final GenreCategorieRepository genreCategorieRepository;
    
    @Override
    public GenreCategorieDto creerGenre(GenreCategorieDto genreDto) {
        log.info("Création d'un nouveau genre: {}", genreDto.getNom());
        
        if (genreCategorieRepository.existsByNomAndActiveTrue(genreDto.getNom())) {
            throw new RuntimeException("Un genre avec ce nom existe déjà");
        }
        
        GenreCategorie genre = convertirDtoVersEntity(genreDto);
        genre.setActive(true);
        
        GenreCategorie genreSauvegardee = genreCategorieRepository.save(genre);
        
        log.info("Genre créé avec succès avec ID: {}", genreSauvegardee.getId());
        return convertirEntityVersDto(genreSauvegardee);
    }
    
    @Override
    @Transactional(readOnly = true)
    public GenreCategorieDto obtenirGenreParId(Long id) {
        return genreCategorieRepository.findById(id)
                .map(this::convertirEntityVersDto)
                .orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GenreCategorieDto> obtenirTousLesGenresActifs() {
        return genreCategorieRepository.findByActiveTrueOrderByOrdre()
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GenreCategorieDto> obtenirGenresParType(String type) {
        TypeCategorieEnum typeEnum = TypeCategorieEnum.valueOf(type);
        return genreCategorieRepository.findByTypeAndActiveTrueOrderByOrdre(typeEnum)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GenreCategorieDto> rechercherGenresParNom(String nom) {
        return genreCategorieRepository.findByNomContainingIgnoreCaseAndActiveTrue(nom)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GenreCategorieDto> obtenirGenresParUsage() {
        return genreCategorieRepository.findGenresOrderByUsage()
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public GenreCategorieDto mettreAJourGenre(Long id, GenreCategorieDto genreDto) {
        log.info("Mise à jour du genre avec ID: {}", id);
        
        GenreCategorie genre = genreCategorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre non trouvé avec ID: " + id));
        
        // Vérifier si le nom est déjà utilisé par un autre genre
        if (!genre.getNom().equals(genreDto.getNom()) && 
            genreCategorieRepository.existsByNomAndActiveTrue(genreDto.getNom())) {
            throw new RuntimeException("Un genre avec ce nom existe déjà");
        }
        
        // Mettre à jour les champs
        genre.setNom(genreDto.getNom());
        genre.setDescription(genreDto.getDescription());
        genre.setImageUrl(genreDto.getImageUrl());
        genre.setOrdre(genreDto.getOrdre());
        genre.setType(genreDto.getType() != null ? TypeCategorieEnum.valueOf(genreDto.getType()) : TypeCategorieEnum.VETEMENTS);
        
        GenreCategorie genreMiseAJour = genreCategorieRepository.save(genre);
        
        log.info("Genre mis à jour avec succès");
        return convertirEntityVersDto(genreMiseAJour);
    }
    
    @Override
    public void activerGenre(Long id) {
        log.info("Activation du genre avec ID: {}", id);
        
        GenreCategorie genre = genreCategorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre non trouvé avec ID: " + id));
        
        genre.setActive(true);
        genreCategorieRepository.save(genre);
        
        log.info("Genre activé avec succès");
    }
    
    @Override
    public void desactiverGenre(Long id) {
        log.info("Désactivation du genre avec ID: {}", id);
        
        GenreCategorie genre = genreCategorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre non trouvé avec ID: " + id));
        
        genre.setActive(false);
        genreCategorieRepository.save(genre);
        
        log.info("Genre désactivé avec succès");
    }
    
    @Override
    public void supprimerGenre(Long id) {
        log.info("Suppression du genre avec ID: {}", id);
        
        GenreCategorie genre = genreCategorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre non trouvé avec ID: " + id));
        
        genreCategorieRepository.delete(genre);
        log.info("Genre supprimé avec succès");
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean verifierNomDisponible(String nom) {
        return !genreCategorieRepository.existsByNomAndActiveTrue(nom);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getNombreTypesParGenre(Long genreId) {
        return genreCategorieRepository.countTypesByGenre(genreId);
    }
    
    // Méthodes de conversion
    private GenreCategorieDto convertirEntityVersDto(GenreCategorie genre) {
        GenreCategorieDto dto = new GenreCategorieDto();
        dto.setId(genre.getId());
        dto.setNom(genre.getNom());
        dto.setDescription(genre.getDescription());
        dto.setType(genre.getType() != null ? genre.getType().name() : "VETEMENTS");
        dto.setImageUrl(genre.getImageUrl());
        dto.setOrdre(genre.getOrdre());
        dto.setActive(genre.isActive());
        
        // Statistiques
        dto.setNombreTypes(genreCategorieRepository.countTypesByGenre(genre.getId()).intValue());
        
        return dto;
    }
    
    private GenreCategorie convertirDtoVersEntity(GenreCategorieDto dto) {
        GenreCategorie genre = new GenreCategorie();
        genre.setId(dto.getId());
        genre.setNom(dto.getNom());
        genre.setDescription(dto.getDescription());
        genre.setType(dto.getType() != null ? TypeCategorieEnum.valueOf(dto.getType()) : TypeCategorieEnum.VETEMENTS);
        genre.setImageUrl(dto.getImageUrl());
        genre.setOrdre(dto.getOrdre() != null ? dto.getOrdre() : 0);
        genre.setActive(dto.isActive());
        return genre;
    }
}
