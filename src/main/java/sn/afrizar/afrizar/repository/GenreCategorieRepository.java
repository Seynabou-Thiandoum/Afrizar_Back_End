package sn.afrizar.afrizar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.GenreCategorie;
import sn.afrizar.afrizar.model.TypeCategorieEnum;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreCategorieRepository extends JpaRepository<GenreCategorie, Long> {
    
    // Récupérer tous les genres actifs triés par ordre
    List<GenreCategorie> findByActiveTrueOrderByOrdre();
    
    // Récupérer les genres par type de catégorie
    List<GenreCategorie> findByTypeAndActiveTrueOrderByOrdre(TypeCategorieEnum type);
    
    // Vérifier si un nom existe déjà
    boolean existsByNomAndActiveTrue(String nom);
    
    // Récupérer un genre par nom
    Optional<GenreCategorie> findByNomAndActiveTrue(String nom);
    
    // Rechercher par nom (insensible à la casse)
    List<GenreCategorie> findByNomContainingIgnoreCaseAndActiveTrue(String nom);
    
    // Compter le nombre de types associés à un genre
    @Query("SELECT COUNT(c) FROM CategorieCombinaison c WHERE c.genre.id = :genreId AND c.active = true")
    Long countTypesByGenre(@Param("genreId") Long genreId);
    
    // Récupérer les genres les plus utilisés
    @Query("SELECT g FROM GenreCategorie g " +
           "LEFT JOIN CategorieCombinaison c ON g.id = c.genre.id AND c.active = true " +
           "WHERE g.active = true " +
           "GROUP BY g.id " +
           "ORDER BY COUNT(c.id) DESC, g.ordre ASC")
    List<GenreCategorie> findGenresOrderByUsage();
}
