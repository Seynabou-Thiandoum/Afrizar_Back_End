package sn.afrizar.afrizar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.CommissionClient;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CommissionClientRepository extends JpaRepository<CommissionClient, Long> {
    
    List<CommissionClient> findByClientIdOrderByDateCreationDesc(Long clientId);
    
    List<CommissionClient> findByClientIdAndStatutOrderByDateCreationDesc(Long clientId, CommissionClient.StatutCommission statut);
    
    @Query("SELECT SUM(cc.montantCommission) FROM CommissionClient cc WHERE cc.clientId = :clientId AND cc.statut = :statut")
    BigDecimal calculerTotalCommissionsParStatut(@Param("clientId") Long clientId, @Param("statut") CommissionClient.StatutCommission statut);
    
    @Query("SELECT COUNT(cc) FROM CommissionClient cc WHERE cc.clientId = :clientId")
    Long compterCommissionsParClient(@Param("clientId") Long clientId);
    
    @Query("SELECT SUM(cc.montantCommission) FROM CommissionClient cc WHERE cc.clientId = :clientId")
    BigDecimal calculerTotalCommissionsClient(@Param("clientId") Long clientId);
}
