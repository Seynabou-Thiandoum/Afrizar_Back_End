package sn.afrizar.afrizar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.Commission;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Long> {
    
    List<Commission> findByActiveTrueOrderByOrdre();
    
    @Query("SELECT c FROM Commission c WHERE c.active = true AND " +
           "c.seuilMin <= :montant AND " +
           "(c.seuilMax IS NULL OR c.seuilMax >= :montant) " +
           "ORDER BY c.ordre ASC")
    Optional<Commission> findCommissionApplicable(@Param("montant") BigDecimal montant);
    
    @Query("SELECT c FROM Commission c WHERE c.active = true AND c.seuilMin <= :montant ORDER BY c.seuilMin DESC")
    List<Commission> findCommissionsApplicables(@Param("montant") BigDecimal montant);
    
    boolean existsBySeuilMinAndSeuilMaxAndActiveTrue(BigDecimal seuilMin, BigDecimal seuilMax);
}

