package sn.afrizar.afrizar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.TypeCategorie;
import sn.afrizar.afrizar.model.TypeCategorieEnum;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypeCategorieRepository extends JpaRepository<TypeCategorie, Long> {
    
    // Récupérer tous les types actifs triés par ordre
    List<TypeCategorie> findByActiveTrueOrderByOrdre();
    
    // Récupérer les types par type de catégorie
    List<TypeCategorie> findByTypeAndActiveTrueOrderByOrdre(TypeCategorieEnum type);
    
    // Vérifier si un nom existe déjà
    boolean existsByNomAndActiveTrue(String nom);
    
    // Récupérer un type par nom
    Optional<TypeCategorie> findByNomAndActiveTrue(String nom);
    
    // Rechercher par nom (insensible à la casse)
    List<TypeCategorie> findByNomContainingIgnoreCaseAndActiveTrue(String nom);
    
    // Compter le nombre de genres qui utilisent un type
    @Query("SELECT COUNT(c) FROM CategorieCombinaison c WHERE c.type.id = :typeId AND c.active = true")
    Long countGenresByType(@Param("typeId") Long typeId);
    
    // Récupérer les types les plus utilisés
    @Query("SELECT t FROM TypeCategorie t " +
           "LEFT JOIN CategorieCombinaison c ON t.id = c.type.id AND c.active = true " +
           "WHERE t.active = true " +
           "GROUP BY t.id " +
           "ORDER BY COUNT(c.id) DESC, t.ordre ASC")
    List<TypeCategorie> findTypesOrderByUsage();
}
