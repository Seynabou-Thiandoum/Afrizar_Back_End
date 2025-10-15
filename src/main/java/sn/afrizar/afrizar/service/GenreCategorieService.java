package sn.afrizar.afrizar.service;

import sn.afrizar.afrizar.dto.GenreCategorieDto;

import java.util.List;

public interface GenreCategorieService {
    
    // Créer un nouveau genre
    GenreCategorieDto creerGenre(GenreCategorieDto genreDto);
    
    // Récupérer un genre par ID
    GenreCategorieDto obtenirGenreParId(Long id);
    
    // Récupérer tous les genres actifs
    List<GenreCategorieDto> obtenirTousLesGenresActifs();
    
    // Récupérer les genres par type de catégorie
    List<GenreCategorieDto> obtenirGenresParType(String type);
    
    // Rechercher des genres par nom
    List<GenreCategorieDto> rechercherGenresParNom(String nom);
    
    // Récupérer les genres les plus utilisés
    List<GenreCategorieDto> obtenirGenresParUsage();
    
    // Mettre à jour un genre
    GenreCategorieDto mettreAJourGenre(Long id, GenreCategorieDto genreDto);
    
    // Activer un genre
    void activerGenre(Long id);
    
    // Désactiver un genre
    void desactiverGenre(Long id);
    
    // Supprimer un genre
    void supprimerGenre(Long id);
    
    // Vérifier si un nom est disponible
    boolean verifierNomDisponible(String nom);
    
    // Obtenir le nombre de types associés à ce genre
    Long getNombreTypesParGenre(Long genreId);
}
