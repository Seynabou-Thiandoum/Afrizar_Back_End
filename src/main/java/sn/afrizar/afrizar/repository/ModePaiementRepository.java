package sn.afrizar.afrizar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.ModePaiement;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModePaiementRepository extends JpaRepository<ModePaiement, Long> {
    
    /**
     * Trouve tous les modes de paiement actifs, triés par ordre
     */
    List<ModePaiement> findByActifTrueOrderByOrdreAsc();
    
    /**
     * Trouve un mode de paiement par son code
     */
    Optional<ModePaiement> findByCode(String code);
    
    /**
     * Vérifie si un code existe déjà
     */
    boolean existsByCode(String code);
    
    /**
     * Trouve tous les modes de paiement par type
     */
    List<ModePaiement> findByType(ModePaiement.TypePaiement type);
    
    /**
     * Trouve tous les modes de paiement actifs d'un type spécifique
     */
    List<ModePaiement> findByTypeAndActifTrueOrderByOrdreAsc(ModePaiement.TypePaiement type);
    
    /**
     * Trouve tous les modes de paiement triés par ordre
     */
    List<ModePaiement> findAllByOrderByOrdreAsc();
}

