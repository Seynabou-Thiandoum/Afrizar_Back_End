package sn.afrizar.afrizar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.CategorieCombinaison;
import sn.afrizar.afrizar.model.GenreCategorie;
import sn.afrizar.afrizar.model.TypeCategorie;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategorieCombinaisonRepository extends JpaRepository<CategorieCombinaison, Long> {
    
    // Récupérer toutes les combinaisons actives triées par ordre
    List<CategorieCombinaison> findByActiveTrueOrderByOrdre();
    
    // Récupérer les types associés à un genre
    @Query("SELECT c FROM CategorieCombinaison c " +
           "JOIN FETCH c.type " +
           "WHERE c.genre.id = :genreId AND c.active = true " +
           "ORDER BY c.ordre")
    List<CategorieCombinaison> findByGenreIdAndActiveTrueOrderByOrdre(@Param("genreId") Long genreId);
    
    // Récupérer les genres associés à un type
    @Query("SELECT c FROM CategorieCombinaison c " +
           "JOIN FETCH c.genre " +
           "WHERE c.type.id = :typeId AND c.active = true " +
           "ORDER BY c.ordre")
    List<CategorieCombinaison> findByTypeIdAndActiveTrueOrderByOrdre(@Param("typeId") Long typeId);
    
    // Vérifier si une combinaison existe déjà
    boolean existsByGenreAndTypeAndActiveTrue(GenreCategorie genre, TypeCategorie type);
    
    // Récupérer une combinaison spécifique
    Optional<CategorieCombinaison> findByGenreAndTypeAndActiveTrue(GenreCategorie genre, TypeCategorie type);
    
    // Récupérer une combinaison par IDs
    @Query("SELECT c FROM CategorieCombinaison c " +
           "JOIN FETCH c.genre " +
           "JOIN FETCH c.type " +
           "WHERE c.genre.id = :genreId AND c.type.id = :typeId AND c.active = true")
    Optional<CategorieCombinaison> findByGenreIdAndTypeIdAndActiveTrue(
        @Param("genreId") Long genreId, 
        @Param("typeId") Long typeId
    );
    
    // Supprimer toutes les combinaisons d'un genre
    void deleteByGenreId(Long genreId);
    
    // Supprimer toutes les combinaisons d'un type
    void deleteByTypeId(Long typeId);
    
    // Récupérer les combinaisons par type de catégorie
    @Query("SELECT c FROM CategorieCombinaison c " +
           "JOIN FETCH c.genre g " +
           "JOIN FETCH c.type t " +
           "WHERE g.type = :typeEnum AND c.active = true " +
           "ORDER BY g.ordre, c.ordre")
    List<CategorieCombinaison> findByGenreTypeAndActiveTrueOrderByGenreOrdreAndOrdre(
        @Param("typeEnum") sn.afrizar.afrizar.model.TypeCategorieEnum typeEnum
    );
}
