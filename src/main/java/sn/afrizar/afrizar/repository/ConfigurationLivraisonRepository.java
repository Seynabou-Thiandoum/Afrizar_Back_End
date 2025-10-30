package sn.afrizar.afrizar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.ConfigurationLivraison;
import sn.afrizar.afrizar.model.Livraison;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigurationLivraisonRepository extends JpaRepository<ConfigurationLivraison, Long> {
    
    /**
     * Trouve les configurations actives pour un pays et un type de livraison
     */
    Optional<ConfigurationLivraison> findByPaysAndTypeAndActifTrue(String pays, Livraison.TypeLivraison type);
    
    /**
     * Trouve toutes les configurations actives pour un pays
     */
    List<ConfigurationLivraison> findByPaysAndActifTrue(String pays);
    
    /**
     * Trouve toutes les configurations actives pour un type de livraison
     */
    List<ConfigurationLivraison> findByTypeAndActifTrue(Livraison.TypeLivraison type);
    
    /**
     * Trouve toutes les configurations actives
     */
    List<ConfigurationLivraison> findByActifTrue();
    
    /**
     * Trouve les configurations par pays (actives et inactives)
     */
    List<ConfigurationLivraison> findByPays(String pays);
    
    /**
     * Trouve les configurations par type (actives et inactives)
     */
    List<ConfigurationLivraison> findByType(Livraison.TypeLivraison type);
    
    /**
     * Vérifie si une configuration existe pour un pays et un type
     */
    boolean existsByPaysAndType(String pays, Livraison.TypeLivraison type);
    
    /**
     * Trouve les configurations pour un pays avec fallback sur les configurations générales
     */
    @Query("SELECT c FROM ConfigurationLivraison c WHERE " +
           "(c.pays = :pays OR c.pays = 'GENERAL') AND c.type = :type AND c.actif = true " +
           "ORDER BY CASE WHEN c.pays = :pays THEN 0 ELSE 1 END")
    List<ConfigurationLivraison> findByPaysWithFallback(@Param("pays") String pays, @Param("type") Livraison.TypeLivraison type);
}

