package sn.afrizar.afrizar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.afrizar.afrizar.dto.CategorieDto;
import sn.afrizar.afrizar.model.Categorie;
import sn.afrizar.afrizar.model.TypeCategorie;
import sn.afrizar.afrizar.model.GenreCategorie;
import sn.afrizar.afrizar.repository.CategorieRepository;
import sn.afrizar.afrizar.service.CategorieService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategorieServiceImpl implements CategorieService {
    
    private final CategorieRepository categorieRepository;
    
    @Override
    public CategorieDto creerCategorie(CategorieDto categorieDto) {
        log.info("Création d'une nouvelle catégorie: {}", categorieDto.getNom());
        
        if (categorieRepository.existsByNomAndActiveTrue(categorieDto.getNom())) {
            throw new RuntimeException("Une catégorie avec ce nom existe déjà");
        }
        
        Categorie categorie = convertirDtoVersEntity(categorieDto);
        categorie.setActive(true);
        
        // Gérer la catégorie parent si spécifiée
        if (categorieDto.getParentId() != null) {
            Categorie parent = categorieRepository.findById(categorieDto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Catégorie parent non trouvée avec ID: " + categorieDto.getParentId()));
            categorie.setParent(parent);
        }
        
        Categorie categorieSauvegardee = categorieRepository.save(categorie);
        
        log.info("Catégorie créée avec succès avec ID: {}", categorieSauvegardee.getId());
        return convertirEntityVersDto(categorieSauvegardee);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<CategorieDto> obtenirCategorieParId(Long id) {
        return categorieRepository.findById(id)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CategorieDto> obtenirToutesLesCategoriesActives() {
        return categorieRepository.findByActiveTrue()
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CategorieDto> obtenirCategoriesRacines() {
        return categorieRepository.findCategoriesRacines()
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CategorieDto> obtenirSousCategories(Long parentId) {
        return categorieRepository.findByParentIdAndActiveTrueOrderByOrdre(parentId)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CategorieDto> rechercherCategoriesParNom(String nom) {
        return categorieRepository.findByNomContainingIgnoreCaseAndActiveTrue(nom)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CategorieDto> obtenirCategoriesParPopularite() {
        return categorieRepository.findCategoriesOrderByNombreProduits()
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public CategorieDto mettreAJourCategorie(Long id, CategorieDto categorieDto) {
        log.info("Mise à jour de la catégorie avec ID: {}", id);
        
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec ID: " + id));
        
        // Vérifier si le nom est déjà utilisé par une autre catégorie
        if (!categorie.getNom().equals(categorieDto.getNom()) && 
            categorieRepository.existsByNomAndActiveTrue(categorieDto.getNom())) {
            throw new RuntimeException("Une catégorie avec ce nom existe déjà");
        }
        
        // Mettre à jour les champs
        categorie.setNom(categorieDto.getNom());
        categorie.setDescription(categorieDto.getDescription());
        categorie.setIcone(categorieDto.getIcone());
        categorie.setOrdre(categorieDto.getOrdre());
        
        // Gérer le changement de parent
        if (categorieDto.getParentId() != null) {
            if (!categorieDto.getParentId().equals(id)) { // Éviter l'auto-référence
                Categorie nouveauParent = categorieRepository.findById(categorieDto.getParentId())
                        .orElseThrow(() -> new RuntimeException("Catégorie parent non trouvée avec ID: " + categorieDto.getParentId()));
                categorie.setParent(nouveauParent);
            } else {
                throw new RuntimeException("Une catégorie ne peut pas être son propre parent");
            }
        } else {
            categorie.setParent(null);
        }
        
        Categorie categorieMiseAJour = categorieRepository.save(categorie);
        
        log.info("Catégorie mise à jour avec succès");
        return convertirEntityVersDto(categorieMiseAJour);
    }
    
    @Override
    public void supprimerCategorie(Long id) {
        log.info("Suppression de la catégorie avec ID: {}", id);
        
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec ID: " + id));
        
        // Vérifier qu'il n'y a pas de produits associés
        Long nombreProduits = categorieRepository.countProduitsActifsByCategorie(id);
        if (nombreProduits > 0) {
            throw new RuntimeException("Impossible de supprimer la catégorie : elle contient " + nombreProduits + " produit(s)");
        }
        
        // Vérifier qu'il n'y a pas de sous-catégories
        List<Categorie> sousCategories = categorieRepository.findByParentIdAndActiveTrueOrderByOrdre(id);
        if (!sousCategories.isEmpty()) {
            throw new RuntimeException("Impossible de supprimer la catégorie : elle contient " + sousCategories.size() + " sous-catégorie(s)");
        }
        
        categorieRepository.delete(categorie);
        log.info("Catégorie supprimée avec succès");
    }
    
    @Override
    public void activerCategorie(Long id) {
        log.info("Activation de la catégorie avec ID: {}", id);
        
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec ID: " + id));
        
        categorie.setActive(true);
        categorieRepository.save(categorie);
        
        log.info("Catégorie activée avec succès");
    }
    
    @Override
    public void desactiverCategorie(Long id) {
        log.info("Désactivation de la catégorie avec ID: {}", id);
        
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec ID: " + id));
        
        categorie.setActive(false);
        categorieRepository.save(categorie);
        
        log.info("Catégorie désactivée avec succès");
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getNombreProduitsParCategorie(Long categorieId) {
        if (!categorieRepository.existsById(categorieId)) {
            throw new RuntimeException("Catégorie non trouvée avec ID: " + categorieId);
        }
        return categorieRepository.countProduitsActifsByCategorie(categorieId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean verifierNomDisponible(String nom) {
        return !categorieRepository.existsByNomAndActiveTrue(nom);
    }
    
    // Méthodes de conversion
    private CategorieDto convertirEntityVersDto(Categorie categorie) {
        CategorieDto dto = new CategorieDto();
        dto.setId(categorie.getId());
        dto.setNom(categorie.getNom());
        dto.setDescription(categorie.getDescription());
        dto.setIcone(categorie.getIcone());
        dto.setOrdre(categorie.getOrdre());
        dto.setType(categorie.getType() != null ? categorie.getType().name() : "VETEMENTS");
        dto.setGenre(categorie.getGenre() != null ? categorie.getGenre().name() : "HOMME");
        dto.setImageUrl(categorie.getImageUrl());
        dto.setActive(categorie.isActive());
        
        // Parent
        if (categorie.getParent() != null) {
            dto.setParentId(categorie.getParent().getId());
            dto.setNomParent(categorie.getParent().getNom());
        }
        
        // Sous-catégories (récursif léger)
        if (categorie.getSousCategories() != null && !categorie.getSousCategories().isEmpty()) {
            dto.setSousCategories(categorie.getSousCategories().stream()
                    .filter(Categorie::isActive)
                    .map(this::convertirEntityVersDto)
                    .collect(Collectors.toList()));
        }
        
        // Statistiques
        dto.setNombreProduits(categorieRepository.countProduitsActifsByCategorie(categorie.getId()).intValue());
        
        return dto;
    }
    
    private Categorie convertirDtoVersEntity(CategorieDto dto) {
        Categorie categorie = new Categorie();
        categorie.setId(dto.getId());
        categorie.setNom(dto.getNom());
        categorie.setDescription(dto.getDescription());
        categorie.setIcone(dto.getIcone());
        categorie.setOrdre(dto.getOrdre() != null ? dto.getOrdre() : 0);
        categorie.setType(dto.getType() != null ? TypeCategorie.valueOf(dto.getType()) : TypeCategorie.VETEMENTS);
        categorie.setGenre(dto.getGenre() != null ? GenreCategorie.valueOf(dto.getGenre()) : GenreCategorie.HOMME);
        categorie.setImageUrl(dto.getImageUrl());
        categorie.setActive(dto.isActive());
        return categorie;
    }
}

