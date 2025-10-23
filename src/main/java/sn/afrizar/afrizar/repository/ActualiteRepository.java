package sn.afrizar.afrizar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.Actualite;

import java.util.List;

@Repository
public interface ActualiteRepository extends JpaRepository<Actualite, Long> {
    
    // Récupérer les actualités visibles
    Page<Actualite> findByEstVisibleTrueOrderByDatePublicationDesc(Pageable pageable);
    
    // Récupérer les actualités tendance
    List<Actualite> findByEstVisibleTrueAndEstTendanceTrueOrderByDatePublicationDesc();
    
    // Récupérer les actualités par catégorie
    Page<Actualite> findByEstVisibleTrueAndCategorieOrderByDatePublicationDesc(String categorie, Pageable pageable);
    
    // Recherche par titre ou contenu
    @Query("SELECT a FROM Actualite a WHERE a.estVisible = true AND " +
           "(LOWER(a.titre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.contenu) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY a.datePublication DESC")
    Page<Actualite> searchActualites(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Récupérer les actualités les plus likées
    @Query("SELECT a FROM Actualite a WHERE a.estVisible = true ORDER BY a.nombreLikes DESC")
    Page<Actualite> findMostLikedActualites(Pageable pageable);
    
    // Compter les actualités par catégorie
    long countByCategorieAndEstVisibleTrue(String categorie);
    
    // Compter toutes les actualités visibles
    long countByEstVisibleTrue();
    
    // Récupérer les actualités récentes
    @Query("SELECT a FROM Actualite a WHERE a.estVisible = true ORDER BY a.datePublication DESC")
    List<Actualite> findRecentActualites(Pageable pageable);
}
