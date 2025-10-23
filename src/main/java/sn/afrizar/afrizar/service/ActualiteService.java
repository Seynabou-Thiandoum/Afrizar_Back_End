package sn.afrizar.afrizar.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.afrizar.afrizar.dto.ActualiteDto;
import sn.afrizar.afrizar.dto.CreateActualiteDto;

import java.util.List;
import java.util.Optional;

public interface ActualiteService {
    
    ActualiteDto creerActualite(CreateActualiteDto createActualiteDto);
    
    Optional<ActualiteDto> obtenirActualiteParId(Long id);
    
    Page<ActualiteDto> obtenirActualitesPubliques(Pageable pageable);
    
    Page<ActualiteDto> obtenirActualitesParCategorie(String categorie, Pageable pageable);
    
    List<ActualiteDto> obtenirActualitesTendance();
    
    List<ActualiteDto> obtenirActualitesRecentes(int limit);
    
    Page<ActualiteDto> rechercherActualites(String searchTerm, Pageable pageable);
    
    Page<ActualiteDto> obtenirActualitesPlusLikees(Pageable pageable);
    
    // Méthodes d'administration
    Page<ActualiteDto> obtenirToutesLesActualites(Pageable pageable);
    
    ActualiteDto mettreAJourActualite(Long id, CreateActualiteDto createActualiteDto);
    
    void supprimerActualite(Long id);
    
    ActualiteDto changerVisibiliteActualite(Long id, boolean visible);
    
    ActualiteDto marquerActualiteTendance(Long id, boolean tendance);
    
    ActualiteDto incrementerLikes(Long id);
    
    ActualiteDto incrementerCommentaires(Long id);
    
    ActualiteDto incrementerPartages(Long id);
    
    long compterActualites();
    
    long compterActualitesVisibles();
    
    long compterActualitesTendance();
}
