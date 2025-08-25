package sn.afrizar.afrizar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.Client;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    List<Client> findByPays(String pays);
    
    List<Client> findByVille(String ville);
    
    Optional<Client> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT c FROM Client c WHERE c.pointsFidelite >= :points")
    List<Client> findClientsAvecPointsMinimum(@Param("points") Integer points);
    
    @Query("SELECT c FROM Client c ORDER BY c.pointsFidelite DESC")
    List<Client> findClientsByPointsFideliteDesc();
    
    @Query("SELECT c FROM Client c WHERE c.actif = true AND SIZE(c.commandes) > :nombreCommandes")
    List<Client> findClientsActifsAvecPlusieursCommandes(@Param("nombreCommandes") int nombreCommandes);
    
    @Query("SELECT AVG(c.pointsFidelite) FROM Client c WHERE c.actif = true")
    Double getMoyennePointsFidelite();
}
