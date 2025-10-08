package sn.afrizar.afrizar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.afrizar.afrizar.dto.ClientDto;
import sn.afrizar.afrizar.model.Client;
import sn.afrizar.afrizar.model.Utilisateur;
import sn.afrizar.afrizar.repository.ClientRepository;
import sn.afrizar.afrizar.service.ClientService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {
    
    private final ClientRepository clientRepository;
    
    @Override
    public ClientDto creerClient(ClientDto clientDto) {
        log.info("Création d'un nouveau client avec email: {}", clientDto.getEmail());
        
        if (clientRepository.existsByEmail(clientDto.getEmail())) {
            throw new RuntimeException("Un client avec cet email existe déjà");
        }
        
        Client client = convertirDtoVersEntity(clientDto);
        client.setRole(Utilisateur.Role.CLIENT);
        client.setDateCreation(LocalDateTime.now());
        client.setActif(true);
        client.setPointsFidelite(0);
        
        Client clientSauvegarde = clientRepository.save(client);
        
        log.info("Client créé avec succès avec ID: {}", clientSauvegarde.getId());
        return convertirEntityVersDto(clientSauvegarde);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ClientDto> obtenirClientParId(Long id) {
        return clientRepository.findById(id)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ClientDto> obtenirClientParEmail(String email) {
        return clientRepository.findByEmail(email)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClientDto> obtenirTousLesClients() {
        return clientRepository.findAll()
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ClientDto> obtenirClientsAvecPagination(Pageable pageable) {
        return clientRepository.findAll(pageable)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    public ClientDto mettreAJourClient(Long id, ClientDto clientDto) {
        log.info("Mise à jour du client avec ID: {}", id);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec ID: " + id));
        
        // Vérifier si l'email est déjà utilisé par un autre client
        if (!client.getEmail().equals(clientDto.getEmail()) && 
            clientRepository.existsByEmail(clientDto.getEmail())) {
            throw new RuntimeException("Un client avec cet email existe déjà");
        }
        
        // Mettre à jour les champs
        client.setNom(clientDto.getNom());
        client.setPrenom(clientDto.getPrenom());
        client.setEmail(clientDto.getEmail());
        client.setTelephone(clientDto.getTelephone());
        client.setAdresse(clientDto.getAdresse());
        client.setVille(clientDto.getVille());
        client.setCodePostal(clientDto.getCodePostal());
        client.setPays(clientDto.getPays());
        client.setTypeLivraisonPrefere(clientDto.getTypeLivraisonPrefere());
        
        Client clientMisAJour = clientRepository.save(client);
        
        log.info("Client mis à jour avec succès");
        return convertirEntityVersDto(clientMisAJour);
    }
    
    @Override
    public void supprimerClient(Long id) {
        log.info("Suppression du client avec ID: {}", id);
        
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Client non trouvé avec ID: " + id);
        }
        
        clientRepository.deleteById(id);
        log.info("Client supprimé avec succès");
    }
    
    @Override
    public void desactiverClient(Long id) {
        log.info("Désactivation du client avec ID: {}", id);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec ID: " + id));
        
        client.setActif(false);
        clientRepository.save(client);
        
        log.info("Client désactivé avec succès");
    }
    
    @Override
    public void activerClient(Long id) {
        log.info("Activation du client avec ID: {}", id);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec ID: " + id));
        
        client.setActif(true);
        clientRepository.save(client);
        
        log.info("Client activé avec succès");
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClientDto> obtenirClientsParPays(String pays) {
        return clientRepository.findByPays(pays)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClientDto> obtenirClientsParVille(String ville) {
        return clientRepository.findByVille(ville)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public ClientDto ajouterPointsFidelite(Long clientId, Integer points) {
        log.info("Ajout de {} points de fidélité au client ID: {}", points, clientId);
        
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec ID: " + clientId));
        
        client.setPointsFidelite(client.getPointsFidelite() + points);
        Client clientMisAJour = clientRepository.save(client);
        
        log.info("Points de fidélité ajoutés avec succès. Nouveau total: {}", client.getPointsFidelite());
        return convertirEntityVersDto(clientMisAJour);
    }
    
    @Override
    public ClientDto utiliserPointsFidelite(Long clientId, Integer points) {
        log.info("Utilisation de {} points de fidélité pour le client ID: {}", points, clientId);
        
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec ID: " + clientId));
        
        if (client.getPointsFidelite() < points) {
            throw new RuntimeException("Points de fidélité insuffisants. Disponible: " + 
                                     client.getPointsFidelite() + ", Demandé: " + points);
        }
        
        client.setPointsFidelite(client.getPointsFidelite() - points);
        Client clientMisAJour = clientRepository.save(client);
        
        log.info("Points de fidélité utilisés avec succès. Nouveau total: {}", client.getPointsFidelite());
        return convertirEntityVersDto(clientMisAJour);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClientDto> obtenirClientsAvecPointsMinimum(Integer points) {
        return clientRepository.findClientsAvecPointsMinimum(points)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double getMoyennePointsFidelite() {
        return clientRepository.getMoyennePointsFidelite();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean verifierEmailDisponible(String email) {
        return !clientRepository.existsByEmail(email);
    }
    
    // Méthodes de conversion
    private ClientDto convertirEntityVersDto(Client client) {
        ClientDto dto = new ClientDto();
        dto.setId(client.getId());
        dto.setNom(client.getNom());
        dto.setPrenom(client.getPrenom());
        dto.setEmail(client.getEmail());
        dto.setTelephone(client.getTelephone());
        dto.setRole(client.getRole());
        dto.setActif(client.isActif());
        dto.setDateCreation(client.getDateCreation());
        dto.setDerniereConnexion(client.getDerniereConnexion());
        dto.setAdresse(client.getAdresse());
        dto.setVille(client.getVille());
        dto.setCodePostal(client.getCodePostal());
        dto.setPays(client.getPays());
        dto.setPointsFidelite(client.getPointsFidelite());
        dto.setTypeLivraisonPrefere(client.getTypeLivraisonPrefere());
        return dto;
    }
    
    private Client convertirDtoVersEntity(ClientDto dto) {
        Client client = new Client();
        client.setId(dto.getId());
        client.setNom(dto.getNom());
        client.setPrenom(dto.getPrenom());
        client.setEmail(dto.getEmail());
        client.setTelephone(dto.getTelephone());
        client.setAdresse(dto.getAdresse());
        client.setVille(dto.getVille());
        client.setCodePostal(dto.getCodePostal());
        client.setPays(dto.getPays());
        client.setPointsFidelite(dto.getPointsFidelite() != null ? dto.getPointsFidelite() : 0);
        client.setTypeLivraisonPrefere(dto.getTypeLivraisonPrefere());
        return client;
    }
    
    // ===================== MÉTHODES D'ADMINISTRATION =====================
    
    @Override
    @Transactional(readOnly = true)
    public long compterClients() {
        return clientRepository.count();
    }
}

