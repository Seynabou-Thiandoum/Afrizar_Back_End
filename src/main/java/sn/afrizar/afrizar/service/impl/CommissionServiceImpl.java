package sn.afrizar.afrizar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.afrizar.afrizar.model.Commission;
import sn.afrizar.afrizar.repository.CommissionRepository;
import sn.afrizar.afrizar.service.CommissionService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommissionServiceImpl implements CommissionService {
    
    private final CommissionRepository commissionRepository;
    
    @Override
    public Commission creerCommission(Commission commission) {
        log.info("Création d'une nouvelle tranche de commission: {} - {}%", 
                commission.getSeuilMin(), commission.getPourcentage());
        
        // Vérifier qu'il n'y a pas de chevauchement
        if (commissionRepository.existsBySeuilMinAndSeuilMaxAndActiveTrue(
                commission.getSeuilMin(), commission.getSeuilMax())) {
            throw new RuntimeException("Une tranche de commission existe déjà pour cette plage de montants");
        }
        
        commission.setActive(true);
        Commission commissionSauvegardee = commissionRepository.save(commission);
        
        log.info("Tranche de commission créée avec succès avec ID: {}", commissionSauvegardee.getId());
        return commissionSauvegardee;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Commission> obtenirCommissionParId(Long id) {
        return commissionRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Commission> obtenirToutesLesCommissionsActives() {
        return commissionRepository.findByActiveTrueOrderByOrdre();
    }
    
    @Override
    public Commission mettreAJourCommission(Long id, Commission commission) {
        log.info("Mise à jour de la tranche de commission avec ID: {}", id);
        
        Commission commissionExistante = commissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commission non trouvée avec ID: " + id));
        
        commissionExistante.setSeuilMin(commission.getSeuilMin());
        commissionExistante.setSeuilMax(commission.getSeuilMax());
        commissionExistante.setPourcentage(commission.getPourcentage());
        commissionExistante.setDescription(commission.getDescription());
        commissionExistante.setOrdre(commission.getOrdre());
        
        Commission commissionMiseAJour = commissionRepository.save(commissionExistante);
        
        log.info("Tranche de commission mise à jour avec succès");
        return commissionMiseAJour;
    }
    
    @Override
    public void supprimerCommission(Long id) {
        log.info("Suppression de la tranche de commission avec ID: {}", id);
        
        if (!commissionRepository.existsById(id)) {
            throw new RuntimeException("Commission non trouvée avec ID: " + id);
        }
        
        commissionRepository.deleteById(id);
        log.info("Tranche de commission supprimée avec succès");
    }
    
    @Override
    public void activerCommission(Long id) {
        log.info("Activation de la tranche de commission avec ID: {}", id);
        
        Commission commission = commissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commission non trouvée avec ID: " + id));
        
        commission.setActive(true);
        commissionRepository.save(commission);
        
        log.info("Tranche de commission activée avec succès");
    }
    
    @Override
    public void desactiverCommission(Long id) {
        log.info("Désactivation de la tranche de commission avec ID: {}", id);
        
        Commission commission = commissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commission non trouvée avec ID: " + id));
        
        commission.setActive(false);
        commissionRepository.save(commission);
        
        log.info("Tranche de commission désactivée avec succès");
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculerCommission(BigDecimal montant) {
        log.debug("Calcul de la commission pour le montant: {}", montant);
        
        Optional<Commission> commissionApplicable = trouverCommissionApplicable(montant);
        
        if (commissionApplicable.isPresent()) {
            BigDecimal commission = commissionApplicable.get().calculerCommission(montant);
            log.debug("Commission calculée: {} ({}%)", commission, commissionApplicable.get().getPourcentage());
            return commission;
        }
        
        log.warn("Aucune tranche de commission trouvée pour le montant: {}", montant);
        return BigDecimal.ZERO;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Commission> trouverCommissionApplicable(BigDecimal montant) {
        return commissionRepository.findCommissionApplicable(montant);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculerCommissionPersonnalisee(BigDecimal montant, BigDecimal tauxPersonnalise) {
        log.debug("Calcul de commission personnalisée: montant={}, taux={}%", montant, tauxPersonnalise);
        
        if (tauxPersonnalise == null || tauxPersonnalise.compareTo(BigDecimal.ZERO) <= 0) {
            return calculerCommission(montant);
        }
        
        BigDecimal commission = montant.multiply(tauxPersonnalise).divide(BigDecimal.valueOf(100));
        log.debug("Commission personnalisée calculée: {}", commission);
        return commission;
    }
    
    @Override
    public void initialiserCommissionsParDefaut() {
        log.info("Initialisation des tranches de commission par défaut");
        
        // Vérifier si des commissions existent déjà
        List<Commission> commissionsExistantes = commissionRepository.findByActiveTrueOrderByOrdre();
        if (!commissionsExistantes.isEmpty()) {
            log.info("Des tranches de commission existent déjà, initialisation ignorée");
            return;
        }
        
        // Créer les tranches selon vos spécifications exactes
        // < 10 000 FCFA → +10%
        Commission tranche1 = new Commission();
        tranche1.setSeuilMin(BigDecimal.ZERO);
        tranche1.setSeuilMax(BigDecimal.valueOf(9999.99));
        tranche1.setPourcentage(BigDecimal.valueOf(10.00));
        tranche1.setDescription("Moins de 10 000 FCFA (+10%)");
        tranche1.setOrdre(1);
        tranche1.setActive(true);
        commissionRepository.save(tranche1);
        
        // 10 000–30 000 FCFA → +8%
        Commission tranche2 = new Commission();
        tranche2.setSeuilMin(BigDecimal.valueOf(10000));
        tranche2.setSeuilMax(BigDecimal.valueOf(30000));
        tranche2.setPourcentage(BigDecimal.valueOf(8.00));
        tranche2.setDescription("De 10 000 à 30 000 FCFA (+8%)");
        tranche2.setOrdre(2);
        tranche2.setActive(true);
        commissionRepository.save(tranche2);
        
        // 30 001–50 000 FCFA → +6%
        Commission tranche3 = new Commission();
        tranche3.setSeuilMin(BigDecimal.valueOf(30001));
        tranche3.setSeuilMax(BigDecimal.valueOf(50000));
        tranche3.setPourcentage(BigDecimal.valueOf(6.00));
        tranche3.setDescription("De 30 001 à 50 000 FCFA (+6%)");
        tranche3.setOrdre(3);
        tranche3.setActive(true);
        commissionRepository.save(tranche3);
        
        // > 50 000 FCFA → +5% (haut de gamme)
        Commission tranche4 = new Commission();
        tranche4.setSeuilMin(BigDecimal.valueOf(50001));
        tranche4.setSeuilMax(null); // Pas de limite supérieure
        tranche4.setPourcentage(BigDecimal.valueOf(5.00));
        tranche4.setDescription("Plus de 50 000 FCFA - Haut de gamme (+5%)");
        tranche4.setOrdre(4);
        tranche4.setActive(true);
        commissionRepository.save(tranche4);
        
        log.info("Tranches de commission par défaut créées avec succès");
    }
}
