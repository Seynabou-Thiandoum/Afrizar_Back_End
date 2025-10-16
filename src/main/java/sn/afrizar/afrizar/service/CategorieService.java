package sn.afrizar.afrizar.service;

import sn.afrizar.afrizar.dto.CategorieDto;

import java.util.List;
import java.util.Optional;

public interface CategorieService {
    
    CategorieDto creerCategorie(CategorieDto categorieDto);
    
    Optional<CategorieDto> obtenirCategorieParId(Long id);
    
    List<CategorieDto> obtenirToutesLesCategoriesActives();
    
    List<CategorieDto> obtenirCategoriesRacines();
    
    List<CategorieDto> obtenirSousCategories(Long parentId);
    
    List<CategorieDto> rechercherCategoriesParNom(String nom);
    
    List<CategorieDto> obtenirCategoriesParPopularite();
    
    CategorieDto mettreAJourCategorie(Long id, CategorieDto categorieDto);
    
    void supprimerCategorie(Long id);
    
    void activerCategorie(Long id);
    
    void desactiverCategorie(Long id);
    
    Long getNombreProduitsParCategorie(Long categorieId);
    
    boolean verifierNomDisponible(String nom);
    
    // Nouvelles méthodes pour la hiérarchie
    Optional<CategorieDto> obtenirCategorieParSlug(String slug);
    
    List<CategorieDto> obtenirCategoriesRacinesAvecSousCategories();
    
    List<CategorieDto> obtenirHierarchieComplete();
}

