package sn.afrizar.afrizar.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.afrizar.afrizar.dto.ClientDto;
import sn.afrizar.afrizar.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    
    ClientDto creerClient(ClientDto clientDto);
    
    Optional<ClientDto> obtenirClientParId(Long id);
    
    Optional<ClientDto> obtenirClientParEmail(String email);
    
    List<ClientDto> obtenirTousLesClients();
    
    Page<ClientDto> obtenirClientsAvecPagination(Pageable pageable);
    
    ClientDto mettreAJourClient(Long id, ClientDto clientDto);
    
    void supprimerClient(Long id);
    
    void desactiverClient(Long id);
    
    void activerClient(Long id);
    
    List<ClientDto> obtenirClientsParPays(String pays);
    
    List<ClientDto> obtenirClientsParVille(String ville);
    
    ClientDto ajouterPointsFidelite(Long clientId, Integer points);
    
    ClientDto utiliserPointsFidelite(Long clientId, Integer points);
    
    List<ClientDto> obtenirClientsAvecPointsMinimum(Integer points);
    
    Double getMoyennePointsFidelite();
    
    boolean verifierEmailDisponible(String email);
}

