package sn.afrizar.afrizar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.afrizar.afrizar.dto.CommissionClientDto;
import sn.afrizar.afrizar.dto.SoldeCommissionDto;
import sn.afrizar.afrizar.model.CommissionClient;
import sn.afrizar.afrizar.repository.CommissionClientRepository;
import sn.afrizar.afrizar.service.CommissionClientService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommissionClientServiceImpl implements CommissionClientService {
    
    private final CommissionClientRepository commissionClientRepository;
    
    @Override
    public CommissionClientDto creerCommissionClient(CommissionClientDto commissionClientDto) {
        log.info("Création d'une nouvelle commission client: {}", commissionClientDto.getClientId());
        
        CommissionClient commissionClient = convertirDtoVersEntity(commissionClientDto);
        CommissionClient commissionClientSauvegarde = commissionClientRepository.save(commissionClient);
        
        log.info("Commission client créée avec succès avec ID: {}", commissionClientSauvegarde.getId());
        return convertirEntityVersDto(commissionClientSauvegarde);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<CommissionClientDto> obtenirCommissionClientParId(Long id) {
        return commissionClientRepository.findById(id)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CommissionClientDto> obtenirCommissionsParClient(Long clientId) {
        log.info("Récupération des commissions pour le client ID: {}", clientId);
        
        List<CommissionClient> commissions = commissionClientRepository.findByClientIdOrderByDateCreationDesc(clientId);
        return commissions.stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CommissionClientDto> obtenirCommissionsParClientEtStatut(Long clientId, String statut) {
        log.info("Récupération des commissions pour le client ID: {} avec statut: {}", clientId, statut);
        
        CommissionClient.StatutCommission statutEnum = CommissionClient.StatutCommission.valueOf(statut);
        List<CommissionClient> commissions = commissionClientRepository.findByClientIdAndStatutOrderByDateCreationDesc(clientId, statutEnum);
        
        return commissions.stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public SoldeCommissionDto obtenirSoldeCommission(Long clientId) {
        log.info("Calcul du solde de commission pour le client ID: {}", clientId);
        
        BigDecimal soldeTotal = commissionClientRepository.calculerTotalCommissionsClient(clientId);
        BigDecimal soldeEnAttente = commissionClientRepository.calculerTotalCommissionsParStatut(clientId, CommissionClient.StatutCommission.EN_ATTENTE);
        BigDecimal soldePaye = commissionClientRepository.calculerTotalCommissionsParStatut(clientId, CommissionClient.StatutCommission.PAYEE);
        Long nombreCommissions = commissionClientRepository.compterCommissionsParClient(clientId);
        
        // Solde disponible = total - en attente
        BigDecimal soldeDisponible = soldeTotal.subtract(soldeEnAttente != null ? soldeEnAttente : BigDecimal.ZERO);
        
        SoldeCommissionDto solde = new SoldeCommissionDto();
        solde.setSoldeTotal(soldeTotal != null ? soldeTotal : BigDecimal.ZERO);
        solde.setSoldeDisponible(soldeDisponible);
        solde.setSoldeEnAttente(soldeEnAttente != null ? soldeEnAttente : BigDecimal.ZERO);
        solde.setSoldePaye(soldePaye != null ? soldePaye : BigDecimal.ZERO);
        solde.setNombreCommissions(nombreCommissions);
        
        return solde;
    }
    
    @Override
    public CommissionClientDto validerCommission(Long id) {
        log.info("Validation de la commission ID: {}", id);
        
        CommissionClient commission = commissionClientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commission non trouvée avec ID: " + id));
        
        commission.setStatut(CommissionClient.StatutCommission.VALIDEE);
        commission.setDateValidation(LocalDateTime.now());
        
        CommissionClient commissionValidee = commissionClientRepository.save(commission);
        
        log.info("Commission validée avec succès");
        return convertirEntityVersDto(commissionValidee);
    }
    
    @Override
    public CommissionClientDto payerCommission(Long id) {
        log.info("Paiement de la commission ID: {}", id);
        
        CommissionClient commission = commissionClientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commission non trouvée avec ID: " + id));
        
        commission.setStatut(CommissionClient.StatutCommission.PAYEE);
        commission.setDatePaiement(LocalDateTime.now());
        
        CommissionClient commissionPayee = commissionClientRepository.save(commission);
        
        log.info("Commission payée avec succès");
        return convertirEntityVersDto(commissionPayee);
    }
    
    @Override
    public void supprimerCommissionClient(Long id) {
        log.info("Suppression de la commission client ID: {}", id);
        
        if (!commissionClientRepository.existsById(id)) {
            throw new RuntimeException("Commission non trouvée avec ID: " + id);
        }
        
        commissionClientRepository.deleteById(id);
        log.info("Commission client supprimée avec succès");
    }
    
    private CommissionClient convertirDtoVersEntity(CommissionClientDto dto) {
        CommissionClient entity = new CommissionClient();
        entity.setId(dto.getId());
        entity.setClientId(dto.getClientId());
        entity.setCommandeId(dto.getCommandeId());
        entity.setMontantCommission(dto.getMontantCommission());
        entity.setPourcentageCommission(dto.getPourcentageCommission());
        entity.setStatut(dto.getStatut() != null ? dto.getStatut() : CommissionClient.StatutCommission.EN_ATTENTE);
        entity.setDateValidation(dto.getDateValidation());
        entity.setDatePaiement(dto.getDatePaiement());
        return entity;
    }
    
    private CommissionClientDto convertirEntityVersDto(CommissionClient entity) {
        CommissionClientDto dto = new CommissionClientDto();
        dto.setId(entity.getId());
        dto.setClientId(entity.getClientId());
        dto.setCommandeId(entity.getCommandeId());
        dto.setMontantCommission(entity.getMontantCommission());
        dto.setPourcentageCommission(entity.getPourcentageCommission());
        dto.setStatut(entity.getStatut());
        dto.setDateCreation(entity.getDateCreation());
        dto.setDateValidation(entity.getDateValidation());
        dto.setDatePaiement(entity.getDatePaiement());
        
        // Informations de la commande (si disponible)
        if (entity.getCommande() != null) {
            dto.setNumeroCommande(entity.getCommande().getNumeroCommande());
            dto.setMontantTotal(entity.getCommande().getMontantTotal());
            dto.setDateCommande(entity.getCommande().getDateCreation());
        }
        
        return dto;
    }
}
