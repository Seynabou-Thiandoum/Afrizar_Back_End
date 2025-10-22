package sn.afrizar.afrizar.service;

import sn.afrizar.afrizar.dto.TypeCategorieDto;

import java.util.List;

public interface TypeCategorieService {
    
    // Créer un nouveau type
    TypeCategorieDto creerType(TypeCategorieDto typeDto);
    
    // Récupérer un type par ID
    TypeCategorieDto obtenirTypeParId(Long id);
    
    // Récupérer tous les types actifs
    List<TypeCategorieDto> obtenirTousLesTypesActifs();
    
    // Récupérer les types par type de catégorie
    List<TypeCategorieDto> obtenirTypesParType(String type);
    
    // Rechercher des types par nom
    List<TypeCategorieDto> rechercherTypesParNom(String nom);
    
    // Récupérer les types les plus utilisés
    List<TypeCategorieDto> obtenirTypesParUsage();
    
    // Mettre à jour un type
    TypeCategorieDto mettreAJourType(Long id, TypeCategorieDto typeDto);
    
    // Activer un type
    void activerType(Long id);
    
    // Désactiver un type
    void desactiverType(Long id);
    
    // Supprimer un type
    void supprimerType(Long id);
    
    // Vérifier si un nom est disponible
    boolean verifierNomDisponible(String nom);
    
    // Obtenir le nombre de genres qui utilisent ce type
    Long getNombreGenresParType(Long typeId);
}

