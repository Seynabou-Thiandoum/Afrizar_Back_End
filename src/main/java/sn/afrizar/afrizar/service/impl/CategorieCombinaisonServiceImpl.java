package sn.afrizar.afrizar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.afrizar.afrizar.dto.CategorieCombinaisonDto;
import sn.afrizar.afrizar.dto.GenreCategorieDto;
import sn.afrizar.afrizar.dto.TypeCategorieDto;
import sn.afrizar.afrizar.model.CategorieCombinaison;
import sn.afrizar.afrizar.model.GenreCategorie;
import sn.afrizar.afrizar.model.TypeCategorie;
import sn.afrizar.afrizar.model.TypeCategorieEnum;
import sn.afrizar.afrizar.repository.CategorieCombinaisonRepository;
import sn.afrizar.afrizar.repository.GenreCategorieRepository;
import sn.afrizar.afrizar.repository.TypeCategorieRepository;
import sn.afrizar.afrizar.service.CategorieCombinaisonService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategorieCombinaisonServiceImpl implements CategorieCombinaisonService {
    
    private final CategorieCombinaisonRepository combinaisonRepository;
    private final GenreCategorieRepository genreRepository;
    private final TypeCategorieRepository typeRepository;
    
    @Override
    public CategorieCombinaisonDto creerAssociation(Long genreId, Long typeId) {
        log.info("Création d'une association Genre {} + Type {}", genreId, typeId);
        
        GenreCategorie genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new RuntimeException("Genre non trouvé avec ID: " + genreId));
        
        TypeCategorie type = typeRepository.findById(typeId)
                .orElseThrow(() -> new RuntimeException("Type non trouvé avec ID: " + typeId));
        
        // Vérifier si l'association existe déjà
        if (combinaisonRepository.existsByGenreAndTypeAndActiveTrue(genre, type)) {
            throw new RuntimeException("Cette association existe déjà");
        }
        
        CategorieCombinaison combinaison = new CategorieCombinaison();
        combinaison.setGenre(genre);
        combinaison.setType(type);
        combinaison.setActive(true);
        combinaison.setOrdre(0);
        
        CategorieCombinaison combinaisonSauvegardee = combinaisonRepository.save(combinaison);
        
        log.info("Association créée avec succès avec ID: {}", combinaisonSauvegardee.getId());
        return convertirEntityVersDto(combinaisonSauvegardee);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CategorieCombinaisonDto> obtenirToutesLesAssociationsActives() {
        return combinaisonRepository.findByActiveTrueOrderByOrdre()
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TypeCategorieDto> obtenirTypesParGenre(Long genreId) {
        return combinaisonRepository.findByGenreIdAndActiveTrueOrderByOrdre(genreId)
                .stream()
                .map(combinaison -> convertirTypeVersDto(combinaison.getType()))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GenreCategorieDto> obtenirGenresParType(Long typeId) {
        return combinaisonRepository.findByTypeIdAndActiveTrueOrderByOrdre(typeId)
                .stream()
                .map(combinaison -> convertirGenreVersDto(combinaison.getGenre()))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CategorieCombinaisonDto> obtenirAssociationsParType(String type) {
        TypeCategorieEnum typeEnum = TypeCategorieEnum.valueOf(type);
        return combinaisonRepository.findByGenreTypeAndActiveTrueOrderByGenreOrdreAndOrdre(typeEnum)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean verifierAssociationExiste(Long genreId, Long typeId) {
        return combinaisonRepository.findByGenreIdAndTypeIdAndActiveTrue(genreId, typeId).isPresent();
    }
    
    @Override
    public CategorieCombinaisonDto mettreAJourOrdre(Long id, Integer nouvelOrdre) {
        log.info("Mise à jour de l'ordre de l'association avec ID: {}", id);
        
        CategorieCombinaison combinaison = combinaisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Association non trouvée avec ID: " + id));
        
        combinaison.setOrdre(nouvelOrdre);
        CategorieCombinaison combinaisonMiseAJour = combinaisonRepository.save(combinaison);
        
        log.info("Ordre mis à jour avec succès");
        return convertirEntityVersDto(combinaisonMiseAJour);
    }
    
    @Override
    public void activerAssociation(Long id) {
        log.info("Activation de l'association avec ID: {}", id);
        
        CategorieCombinaison combinaison = combinaisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Association non trouvée avec ID: " + id));
        
        combinaison.setActive(true);
        combinaisonRepository.save(combinaison);
        
        log.info("Association activée avec succès");
    }
    
    @Override
    public void desactiverAssociation(Long id) {
        log.info("Désactivation de l'association avec ID: {}", id);
        
        CategorieCombinaison combinaison = combinaisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Association non trouvée avec ID: " + id));
        
        combinaison.setActive(false);
        combinaisonRepository.save(combinaison);
        
        log.info("Association désactivée avec succès");
    }
    
    @Override
    public void supprimerAssociation(Long id) {
        log.info("Suppression de l'association avec ID: {}", id);
        
        CategorieCombinaison combinaison = combinaisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Association non trouvée avec ID: " + id));
        
        combinaisonRepository.delete(combinaison);
        log.info("Association supprimée avec succès");
    }
    
    @Override
    public void supprimerAssociationsParGenre(Long genreId) {
        log.info("Suppression de toutes les associations du genre avec ID: {}", genreId);
        combinaisonRepository.deleteByGenreId(genreId);
        log.info("Associations supprimées avec succès");
    }
    
    @Override
    public void supprimerAssociationsParType(Long typeId) {
        log.info("Suppression de toutes les associations du type avec ID: {}", typeId);
        combinaisonRepository.deleteByTypeId(typeId);
        log.info("Associations supprimées avec succès");
    }
    
    @Override
    public List<CategorieCombinaisonDto> creerAssociationsEnLot(List<Long> genreIds, List<Long> typeIds) {
        log.info("Création d'associations en lot pour {} genres et {} types", genreIds.size(), typeIds.size());
        
        List<CategorieCombinaisonDto> associationsCreees = genreIds.stream()
                .flatMap(genreId -> typeIds.stream()
                        .map(typeId -> {
                            try {
                                return creerAssociation(genreId, typeId);
                            } catch (RuntimeException e) {
                                log.warn("Association Genre {} + Type {} déjà existante", genreId, typeId);
                                return null;
                            }
                        }))
                .filter(association -> association != null)
                .collect(Collectors.toList());
        
        log.info("{} associations créées avec succès", associationsCreees.size());
        return associationsCreees;
    }
    
    // Méthodes de conversion
    private CategorieCombinaisonDto convertirEntityVersDto(CategorieCombinaison combinaison) {
        CategorieCombinaisonDto dto = new CategorieCombinaisonDto();
        dto.setId(combinaison.getId());
        dto.setGenreId(combinaison.getGenre().getId());
        dto.setNomGenre(combinaison.getGenre().getNom());
        dto.setTypeId(combinaison.getType().getId());
        dto.setNomType(combinaison.getType().getNom());
        dto.setOrdre(combinaison.getOrdre());
        dto.setActive(combinaison.isActive());
        dto.setAffichage(combinaison.getGenre().getNom() + " - " + combinaison.getType().getNom());
        return dto;
    }
    
    private TypeCategorieDto convertirTypeVersDto(TypeCategorie type) {
        TypeCategorieDto dto = new TypeCategorieDto();
        dto.setId(type.getId());
        dto.setNom(type.getNom());
        dto.setDescription(type.getDescription());
        dto.setType(type.getType() != null ? type.getType().name() : "VETEMENTS");
        dto.setImageUrl(type.getImageUrl());
        dto.setOrdre(type.getOrdre());
        dto.setActive(type.isActive());
        return dto;
    }
    
    private GenreCategorieDto convertirGenreVersDto(GenreCategorie genre) {
        GenreCategorieDto dto = new GenreCategorieDto();
        dto.setId(genre.getId());
        dto.setNom(genre.getNom());
        dto.setDescription(genre.getDescription());
        dto.setType(genre.getType() != null ? genre.getType().name() : "VETEMENTS");
        dto.setImageUrl(genre.getImageUrl());
        dto.setOrdre(genre.getOrdre());
        dto.setActive(genre.isActive());
        return dto;
    }
}
