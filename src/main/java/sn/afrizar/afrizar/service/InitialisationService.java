package sn.afrizar.afrizar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.afrizar.afrizar.model.Utilisateur;
import sn.afrizar.afrizar.repository.UtilisateurRepository;

import java.time.LocalDateTime;

/**
 * Service d'initialisation des données par défaut au démarrage de l'application
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InitialisationService implements CommandLineRunner {
    
    private final CommissionService commissionService;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Initialisation des données par défaut...");
        
        try {
            // Créer le compte admin par défaut
            creerCompteAdminParDefaut();
            
            // Créer le compte support par défaut
            creerCompteSupportParDefaut();
            
            // Initialiser les tranches de commission par défaut
            commissionService.initialiserCommissionsParDefaut();
            
            log.info("Initialisation des données terminée avec succès");
            
        } catch (Exception e) {
            log.error("Erreur lors de l'initialisation des données: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Crée un compte administrateur par défaut si aucun admin n'existe
     */
    private void creerCompteAdminParDefaut() {
        // Vérifier si un admin existe déjà
        long nombreAdmins = utilisateurRepository.countByRoleAndActif(Utilisateur.Role.ADMIN);
        
        if (nombreAdmins == 0) {
            log.info("Aucun compte admin trouvé. Création du compte admin par défaut...");
            
            Utilisateur admin = new Utilisateur();
            admin.setNom("ROLE_ADMIN");
            admin.setPrenom("Afrizar");
            admin.setEmail("admin@afrizar.sn");
            admin.setMotDePasse(passwordEncoder.encode("Admin@123"));
            admin.setTelephone("+221770000000");
            admin.setRole(Utilisateur.Role.ADMIN);
            admin.setActif(true);
            admin.setDateCreation(LocalDateTime.now());
            
            utilisateurRepository.save(admin);
            
            log.info("✅ Compte admin créé avec succès !");
            log.info("   Email: admin@afrizar.sn");
            log.info("   Mot de passe: Admin@123");
            log.info("   ⚠️  IMPORTANT: Changez ce mot de passe dès la première connexion !");
            
        } else {
            log.info("Compte(s) admin existant(s) détecté(s). Nombre: {}", nombreAdmins);
        }
    }
    
    /**
     * Crée un compte support par défaut si aucun support n'existe
     */
    private void creerCompteSupportParDefaut() {
        // Vérifier si un compte support existe déjà
        long nombreSupports = utilisateurRepository.countByRoleAndActif(Utilisateur.Role.SUPPORT);
        
        if (nombreSupports == 0) {
            log.info("Aucun compte support trouvé. Création du compte support par défaut...");
            
            Utilisateur support = new Utilisateur();
            support.setNom("Support");
            support.setPrenom("Afrizar");
            support.setEmail("support@afrizar.sn");
            support.setMotDePasse(passwordEncoder.encode("Support@123"));
            support.setTelephone("+221770000001");
            support.setRole(Utilisateur.Role.SUPPORT);
            support.setActif(true);
            support.setDateCreation(LocalDateTime.now());
            
            utilisateurRepository.save(support);
            
            log.info("✅ Compte support créé avec succès !");
            log.info("   Email: support@afrizar.sn");
            log.info("   Mot de passe: Support@123");
            
        } else {
            log.info("Compte(s) support existant(s) détecté(s). Nombre: {}", nombreSupports);
        }
    }
}

