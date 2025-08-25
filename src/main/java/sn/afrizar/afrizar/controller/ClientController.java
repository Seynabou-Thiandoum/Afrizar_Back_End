package sn.afrizar.afrizar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.ClientDto;
import sn.afrizar.afrizar.service.ClientService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "API de gestion des clients")
public class ClientController {
    
    private final ClientService clientService;
    
    @PostMapping
    @Operation(summary = "Créer un nouveau client", description = "Enregistre un nouveau client dans le système")
    @ApiResponse(responseCode = "201", description = "Client créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    public ResponseEntity<ClientDto> creerClient(@Valid @RequestBody ClientDto clientDto) {
        log.info("Création d'un nouveau client avec email: {}", clientDto.getEmail());
        
        try {
            ClientDto clientCree = clientService.creerClient(clientDto);
            return new ResponseEntity<>(clientCree, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la création du client: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un client par ID", description = "Récupère les détails d'un client spécifique")
    @ApiResponse(responseCode = "200", description = "Client trouvé")
    @ApiResponse(responseCode = "404", description = "Client non trouvé")
    public ResponseEntity<ClientDto> obtenirClient(
            @Parameter(description = "ID du client") @PathVariable Long id) {
        
        return clientService.obtenirClientParId(id)
                .map(client -> ResponseEntity.ok(client))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "Obtenir un client par email", description = "Récupère un client par son adresse email")
    @ApiResponse(responseCode = "200", description = "Client trouvé")
    @ApiResponse(responseCode = "404", description = "Client non trouvé")
    public ResponseEntity<ClientDto> obtenirClientParEmail(
            @Parameter(description = "Email du client") @PathVariable String email) {
        
        return clientService.obtenirClientParEmail(email)
                .map(client -> ResponseEntity.ok(client))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "Lister tous les clients", description = "Récupère la liste de tous les clients avec pagination")
    @ApiResponse(responseCode = "200", description = "Liste des clients récupérée")
    public ResponseEntity<Page<ClientDto>> listerClients(
            @Parameter(description = "Numéro de page (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Critère de tri") @RequestParam(defaultValue = "dateCreation") String sortBy,
            @Parameter(description = "Direction du tri (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ClientDto> clients = clientService.obtenirClientsAvecPagination(pageable);
        
        return ResponseEntity.ok(clients);
    }
    
    @GetMapping("/pays/{pays}")
    @Operation(summary = "Obtenir les clients par pays", description = "Récupère tous les clients d'un pays spécifique")
    @ApiResponse(responseCode = "200", description = "Clients du pays récupérés")
    public ResponseEntity<List<ClientDto>> obtenirClientsParPays(
            @Parameter(description = "Nom du pays") @PathVariable String pays) {
        
        List<ClientDto> clients = clientService.obtenirClientsParPays(pays);
        return ResponseEntity.ok(clients);
    }
    
    @GetMapping("/ville/{ville}")
    @Operation(summary = "Obtenir les clients par ville", description = "Récupère tous les clients d'une ville spécifique")
    @ApiResponse(responseCode = "200", description = "Clients de la ville récupérés")
    public ResponseEntity<List<ClientDto>> obtenirClientsParVille(
            @Parameter(description = "Nom de la ville") @PathVariable String ville) {
        
        List<ClientDto> clients = clientService.obtenirClientsParVille(ville);
        return ResponseEntity.ok(clients);
    }
    
    @GetMapping("/points-fidelite")
    @Operation(summary = "Obtenir les clients avec points de fidélité minimums", description = "Récupère les clients ayant au moins le nombre de points spécifié")
    @ApiResponse(responseCode = "200", description = "Clients avec points de fidélité récupérés")
    public ResponseEntity<List<ClientDto>> obtenirClientsAvecPointsMinimum(
            @Parameter(description = "Nombre minimum de points") @RequestParam Integer points) {
        
        List<ClientDto> clients = clientService.obtenirClientsAvecPointsMinimum(points);
        return ResponseEntity.ok(clients);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un client", description = "Met à jour les informations d'un client existant")
    @ApiResponse(responseCode = "200", description = "Client mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Client non trouvé")
    @ApiResponse(responseCode = "409", description = "Email déjà utilisé par un autre client")
    public ResponseEntity<ClientDto> mettreAJourClient(
            @Parameter(description = "ID du client") @PathVariable Long id,
            @Valid @RequestBody ClientDto clientDto) {
        
        try {
            ClientDto clientMisAJour = clientService.mettreAJourClient(id, clientDto);
            return ResponseEntity.ok(clientMisAJour);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour du client {}: {}", id, e.getMessage());
            if (e.getMessage().contains("email existe déjà")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/points-fidelite/ajouter")
    @Operation(summary = "Ajouter des points de fidélité", description = "Ajoute des points de fidélité à un client")
    @ApiResponse(responseCode = "200", description = "Points ajoutés avec succès")
    @ApiResponse(responseCode = "404", description = "Client non trouvé")
    public ResponseEntity<ClientDto> ajouterPointsFidelite(
            @Parameter(description = "ID du client") @PathVariable Long id,
            @Parameter(description = "Nombre de points à ajouter") @RequestParam Integer points) {
        
        try {
            ClientDto clientMisAJour = clientService.ajouterPointsFidelite(id, points);
            return ResponseEntity.ok(clientMisAJour);
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'ajout de points de fidélité au client {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/points-fidelite/utiliser")
    @Operation(summary = "Utiliser des points de fidélité", description = "Utilise des points de fidélité d'un client")
    @ApiResponse(responseCode = "200", description = "Points utilisés avec succès")
    @ApiResponse(responseCode = "400", description = "Points insuffisants")
    @ApiResponse(responseCode = "404", description = "Client non trouvé")
    public ResponseEntity<ClientDto> utiliserPointsFidelite(
            @Parameter(description = "ID du client") @PathVariable Long id,
            @Parameter(description = "Nombre de points à utiliser") @RequestParam Integer points) {
        
        try {
            ClientDto clientMisAJour = clientService.utiliserPointsFidelite(id, points);
            return ResponseEntity.ok(clientMisAJour);
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'utilisation de points de fidélité du client {}: {}", id, e.getMessage());
            if (e.getMessage().contains("insuffisants")) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/desactiver")
    @Operation(summary = "Désactiver un client", description = "Désactive le compte d'un client")
    @ApiResponse(responseCode = "204", description = "Client désactivé avec succès")
    @ApiResponse(responseCode = "404", description = "Client non trouvé")
    public ResponseEntity<Void> desactiverClient(
            @Parameter(description = "ID du client") @PathVariable Long id) {
        
        try {
            clientService.desactiverClient(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la désactivation du client {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/activer")
    @Operation(summary = "Activer un client", description = "Active le compte d'un client")
    @ApiResponse(responseCode = "204", description = "Client activé avec succès")
    @ApiResponse(responseCode = "404", description = "Client non trouvé")
    public ResponseEntity<Void> activerClient(
            @Parameter(description = "ID du client") @PathVariable Long id) {
        
        try {
            clientService.activerClient(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'activation du client {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un client", description = "Supprime définitivement un client")
    @ApiResponse(responseCode = "204", description = "Client supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Client non trouvé")
    public ResponseEntity<Void> supprimerClient(
            @Parameter(description = "ID du client") @PathVariable Long id) {
        
        try {
            clientService.supprimerClient(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression du client {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/email/{email}/disponible")
    @Operation(summary = "Vérifier la disponibilité d'un email", description = "Vérifie si une adresse email est disponible")
    @ApiResponse(responseCode = "200", description = "Disponibilité vérifiée")
    public ResponseEntity<Boolean> verifierEmailDisponible(
            @Parameter(description = "Adresse email à vérifier") @PathVariable String email) {
        
        boolean disponible = clientService.verifierEmailDisponible(email);
        return ResponseEntity.ok(disponible);
    }
    
    @GetMapping("/statistiques/points-fidelite/moyenne")
    @Operation(summary = "Obtenir la moyenne des points de fidélité", description = "Récupère la moyenne des points de fidélité de tous les clients")
    @ApiResponse(responseCode = "200", description = "Moyenne calculée")
    public ResponseEntity<Double> getMoyennePointsFidelite() {
        Double moyenne = clientService.getMoyennePointsFidelite();
        return ResponseEntity.ok(moyenne != null ? moyenne : 0.0);
    }
}

