package sn.afrizar.afrizar.repository;

import sn.afrizar.afrizar.model.VenteFlash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VenteFlashRepository extends JpaRepository<VenteFlash, Long> {
    
    List<VenteFlash> findByActifTrue();
    
    @Query("SELECT vf FROM VenteFlash vf WHERE vf.actif = true AND :now BETWEEN vf.dateDebut AND vf.dateFin")
    List<VenteFlash> findVentesFlashEnCours(LocalDateTime now);
    
    @Query("SELECT vf FROM VenteFlash vf WHERE vf.actif = true AND :now BETWEEN vf.dateDebut AND vf.dateFin ORDER BY vf.dateFin ASC")
    Optional<VenteFlash> findVenteFlashActivePrincipale(LocalDateTime now);
    
    List<VenteFlash> findByDateFinBeforeAndActifTrue(LocalDateTime date);
}

