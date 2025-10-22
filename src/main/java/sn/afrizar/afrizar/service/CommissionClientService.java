package sn.afrizar.afrizar.service;

import sn.afrizar.afrizar.dto.CommissionClientDto;
import sn.afrizar.afrizar.dto.SoldeCommissionDto;

import java.util.List;
import java.util.Optional;

public interface CommissionClientService {
    
    CommissionClientDto creerCommissionClient(CommissionClientDto commissionClientDto);
    
    Optional<CommissionClientDto> obtenirCommissionClientParId(Long id);
    
    List<CommissionClientDto> obtenirCommissionsParClient(Long clientId);
    
    List<CommissionClientDto> obtenirCommissionsParClientEtStatut(Long clientId, String statut);
    
    SoldeCommissionDto obtenirSoldeCommission(Long clientId);
    
    CommissionClientDto validerCommission(Long id);
    
    CommissionClientDto payerCommission(Long id);
    
    void supprimerCommissionClient(Long id);
}
