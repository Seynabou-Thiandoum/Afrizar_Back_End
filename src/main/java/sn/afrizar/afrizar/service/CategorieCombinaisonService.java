package sn.afrizar.afrizar.service;

import sn.afrizar.afrizar.dto.CategorieCombinaisonDto;
import sn.afrizar.afrizar.dto.GenreCategorieDto;
import sn.afrizar.afrizar.dto.TypeCategorieDto;

import java.util.List;

public interface CategorieCombinaisonService {
    
    // Créer une nouvelle association Genre + Type
    CategorieCombinaisonDto creerAssociation(Long genreId, Long typeId);
    
    // Récupérer toutes les associations actives
    List<CategorieCombinaisonDto> obtenirToutesLesAssociationsActives();
    
    // Récupérer les types associés à un genre
    List<TypeCategorieDto> obtenirTypesParGenre(Long genreId);
    
    // Récupérer les genres associés à un type
    List<GenreCategorieDto> obtenirGenresParType(Long typeId);
    
    // Récupérer les associations par type de catégorie
    List<CategorieCombinaisonDto> obtenirAssociationsParType(String type);
    
    // Vérifier si une association existe
    boolean verifierAssociationExiste(Long genreId, Long typeId);
    
    // Mettre à jour l'ordre d'une association
    CategorieCombinaisonDto mettreAJourOrdre(Long id, Integer nouvelOrdre);
    
    // Activer une association
    void activerAssociation(Long id);
    
    // Désactiver une association
    void desactiverAssociation(Long id);
    
    // Supprimer une association
    void supprimerAssociation(Long id);
    
    // Supprimer toutes les associations d'un genre
    void supprimerAssociationsParGenre(Long genreId);
    
    // Supprimer toutes les associations d'un type
    void supprimerAssociationsParType(Long typeId);
    
    // Créer plusieurs associations en lot
    List<CategorieCombinaisonDto> creerAssociationsEnLot(List<Long> genreIds, List<Long> typeIds);
}
