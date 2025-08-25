package sn.afrizar.afrizar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service d'initialisation des données par défaut au démarrage de l'application
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InitialisationService implements CommandLineRunner {
    
    private final CommissionService commissionService;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Initialisation des données par défaut...");
        
        try {
            // Initialiser les tranches de commission par défaut
            commissionService.initialiserCommissionsParDefaut();
            
            log.info("Initialisation des données terminée avec succès");
            
        } catch (Exception e) {
            log.error("Erreur lors de l'initialisation des données: {}", e.getMessage(), e);
        }
    }
}

