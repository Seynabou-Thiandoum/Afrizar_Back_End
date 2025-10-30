package sn.afrizar.afrizar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.Panier;

import java.util.Optional;

@Repository
public interface PanierRepository extends JpaRepository<Panier, Long> {
    
    // Trouver le panier d'un client
    Optional<Panier> findByClientIdAndActifTrue(Long clientId);
    
    // Trouver le panier d'un client (actif ou non)
    Optional<Panier> findByClientId(Long clientId);
    
    // Vérifier si un client a un panier actif
    boolean existsByClientIdAndActifTrue(Long clientId);
    
    // Compter le nombre d'articles dans le panier d'un client
    @Query("SELECT COUNT(pi) FROM Panier p JOIN p.items pi WHERE p.client.id = :clientId AND p.actif = true")
    Long countItemsByClientId(@Param("clientId") Long clientId);
}




