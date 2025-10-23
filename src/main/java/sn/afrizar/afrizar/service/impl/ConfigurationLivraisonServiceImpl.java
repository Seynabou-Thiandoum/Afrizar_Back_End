package sn.afrizar.afrizar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.afrizar.afrizar.dto.ConfigurationLivraisonDto;
import sn.afrizar.afrizar.model.ConfigurationLivraison;
import sn.afrizar.afrizar.model.Livraison;
import sn.afrizar.afrizar.repository.ConfigurationLivraisonRepository;
import sn.afrizar.afrizar.service.ConfigurationLivraisonService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ConfigurationLivraisonServiceImpl implements ConfigurationLivraisonService {
    
    private final ConfigurationLivraisonRepository configurationLivraisonRepository;
    
    @Override
    public ConfigurationLivraisonDto creerConfiguration(ConfigurationLivraisonDto dto, String emailAdmin) {
        log.info("Création d'une nouvelle configuration de livraison: {} - {} par {}", 
                dto.getType(), dto.getPays(), emailAdmin);
        
        // Vérifier si une configuration existe déjà pour ce pays et ce type
        if (configurationLivraisonRepository.existsByPaysAndType(dto.getPays(), dto.getType())) {
            throw new RuntimeException("Une configuration existe déjà pour le pays " + dto.getPays() + 
                                     " et le type " + dto.getType());
        }
        
        ConfigurationLivraison configuration = ConfigurationLivraison.builder()
                .type(dto.getType())
                .pays(dto.getPays())
                .tarifBase(dto.getTarifBase())
                .tarifParKg(dto.getTarifParKg())
                .delaiJours(dto.getDelaiJours())
                .delaiMinJours(dto.getDelaiMinJours())
                .delaiMaxJours(dto.getDelaiMaxJours())
                .minimumFacturation(dto.getMinimumFacturation())
                .reductionGrosColis(dto.getReductionGrosColis())
                .supplementVilleEloignee(dto.getSupplementVilleEloignee())
                .actif(dto.getActif() != null ? dto.getActif() : true)
                .description(dto.getDescription())
                .notes(dto.getNotes())
                .modifiePar(emailAdmin)
                .build();
        
        ConfigurationLivraison configurationSauvegardee = configurationLivraisonRepository.save(configuration);
        
        log.info("Configuration de livraison créée avec succès: ID {}", configurationSauvegardee.getId());
        
        return convertirEntityVersDto(configurationSauvegardee);
    }
    
    @Override
    public ConfigurationLivraisonDto mettreAJourConfiguration(Long id, ConfigurationLivraisonDto dto, String emailAdmin) {
        log.info("Mise à jour de la configuration de livraison ID: {} par {}", id, emailAdmin);
        
        ConfigurationLivraison configuration = configurationLivraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuration non trouvée avec ID: " + id));
        
        // Mettre à jour les champs
        configuration.setType(dto.getType());
        configuration.setPays(dto.getPays());
        configuration.setTarifBase(dto.getTarifBase());
        configuration.setTarifParKg(dto.getTarifParKg());
        configuration.setDelaiJours(dto.getDelaiJours());
        configuration.setDelaiMinJours(dto.getDelaiMinJours());
        configuration.setDelaiMaxJours(dto.getDelaiMaxJours());
        configuration.setMinimumFacturation(dto.getMinimumFacturation());
        configuration.setReductionGrosColis(dto.getReductionGrosColis());
        configuration.setSupplementVilleEloignee(dto.getSupplementVilleEloignee());
        configuration.setActif(dto.getActif());
        configuration.setDescription(dto.getDescription());
        configuration.setNotes(dto.getNotes());
        configuration.setModifiePar(emailAdmin);
        
        ConfigurationLivraison configurationMiseAJour = configurationLivraisonRepository.save(configuration);
        
        log.info("Configuration de livraison mise à jour avec succès: ID {}", id);
        
        return convertirEntityVersDto(configurationMiseAJour);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ConfigurationLivraisonDto> obtenirConfigurationsActives() {
        return configurationLivraisonRepository.findByActifTrue()
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ConfigurationLivraisonDto> obtenirToutesConfigurations() {
        return configurationLivraisonRepository.findAll()
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ConfigurationLivraisonDto> obtenirConfigurationsParPays(String pays) {
        return configurationLivraisonRepository.findByPaysAndActifTrue(pays)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ConfigurationLivraisonDto> obtenirConfigurationsParType(Livraison.TypeLivraison type) {
        return configurationLivraisonRepository.findByTypeAndActifTrue(type)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ConfigurationLivraisonDto obtenirConfigurationParId(Long id) {
        return configurationLivraisonRepository.findById(id)
                .map(this::convertirEntityVersDto)
                .orElseThrow(() -> new RuntimeException("Configuration non trouvée avec ID: " + id));
    }
    
    @Override
    public ConfigurationLivraisonDto toggleActif(Long id, String emailAdmin) {
        log.info("Changement du statut actif pour la configuration ID: {} par {}", id, emailAdmin);
        
        ConfigurationLivraison configuration = configurationLivraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuration non trouvée avec ID: " + id));
        
        configuration.setActif(!configuration.getActif());
        configuration.setModifiePar(emailAdmin);
        
        ConfigurationLivraison configurationMiseAJour = configurationLivraisonRepository.save(configuration);
        
        log.info("Statut de la configuration ID {} changé à: {}", id, configurationMiseAJour.getActif());
        
        return convertirEntityVersDto(configurationMiseAJour);
    }
    
    @Override
    public void supprimerConfiguration(Long id) {
        log.info("Suppression de la configuration de livraison ID: {}", id);
        
        if (!configurationLivraisonRepository.existsById(id)) {
            throw new RuntimeException("Configuration non trouvée avec ID: " + id);
        }
        
        configurationLivraisonRepository.deleteById(id);
        
        log.info("Configuration de livraison supprimée avec succès: ID {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal obtenirTarifLivraison(String pays, Livraison.TypeLivraison type, BigDecimal poids) {
        log.debug("Calcul du tarif de livraison: pays={}, type={}, poids={} kg", pays, type, poids);
        
        // Chercher une configuration spécifique pour ce pays et ce type
        Optional<ConfigurationLivraison> configuration = configurationLivraisonRepository
                .findByPaysAndTypeAndActifTrue(pays, type);
        
        // Si pas de configuration spécifique, chercher une configuration générale
        if (configuration.isEmpty()) {
            configuration = configurationLivraisonRepository
                    .findByPaysAndTypeAndActifTrue("GENERAL", type);
        }
        
        if (configuration.isEmpty()) {
            log.warn("Aucune configuration trouvée pour {} {}, utilisation des tarifs par défaut", pays, type);
            return calculerTarifParDefaut(pays, type, poids);
        }
        
        ConfigurationLivraison config = configuration.get();
        BigDecimal tarifBase = config.getTarifBase();
        BigDecimal tarifParKg = config.getTarifParKg();
        
        // Calcul: tarif de base + (poids × tarif par kg)
        BigDecimal coutBase = tarifBase.add(poids.multiply(tarifParKg));
        
        // Appliquer les ajustements
        BigDecimal coutFinal = appliquerAjustements(config, coutBase, poids);
        
        // Appliquer le minimum de facturation
        if (config.getMinimumFacturation() != null && coutFinal.compareTo(config.getMinimumFacturation()) < 0) {
            coutFinal = config.getMinimumFacturation();
        }
        
        log.debug("Tarif de livraison calculé: {} FCFA (base: {} FCFA)", coutFinal, coutBase);
        
        return coutFinal;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Integer obtenirDelaiLivraison(String pays, Livraison.TypeLivraison type) {
        log.debug("Calcul du délai de livraison: pays={}, type={}", pays, type);
        
        // Chercher une configuration spécifique pour ce pays et ce type
        Optional<ConfigurationLivraison> configuration = configurationLivraisonRepository
                .findByPaysAndTypeAndActifTrue(pays, type);
        
        // Si pas de configuration spécifique, chercher une configuration générale
        if (configuration.isEmpty()) {
            configuration = configurationLivraisonRepository
                    .findByPaysAndTypeAndActifTrue("GENERAL", type);
        }
        
        if (configuration.isEmpty()) {
            log.warn("Aucune configuration trouvée pour {} {}, utilisation du délai par défaut", pays, type);
            return obtenirDelaiParDefaut(pays, type);
        }
        
        return configuration.get().getDelaiJours();
    }
    
    @Override
    public void initialiserConfigurationsParDefaut() {
        log.info("Initialisation des configurations de livraison par défaut");
        
        // Vérifier si des configurations existent déjà
        if (!configurationLivraisonRepository.findAll().isEmpty()) {
            log.info("Des configurations existent déjà, initialisation ignorée");
            return;
        }
        
        // Créer les configurations par défaut pour le Sénégal
        creerConfigurationParDefaut("SENEGAL", Livraison.TypeLivraison.EXPRESS, 
                BigDecimal.valueOf(2000), BigDecimal.valueOf(1000), 3);
        creerConfigurationParDefaut("SENEGAL", Livraison.TypeLivraison.STANDARD, 
                BigDecimal.valueOf(1000), BigDecimal.valueOf(500), 7);
        creerConfigurationParDefaut("SENEGAL", Livraison.TypeLivraison.ECONOMIQUE, 
                BigDecimal.valueOf(500), BigDecimal.valueOf(300), 15);
        
        // Créer les configurations par défaut pour l'international
        creerConfigurationParDefaut("GENERAL", Livraison.TypeLivraison.EXPRESS, 
                BigDecimal.valueOf(10000), BigDecimal.valueOf(3000), 7);
        creerConfigurationParDefaut("GENERAL", Livraison.TypeLivraison.STANDARD, 
                BigDecimal.valueOf(5000), BigDecimal.valueOf(2000), 14);
        creerConfigurationParDefaut("GENERAL", Livraison.TypeLivraison.ECONOMIQUE, 
                BigDecimal.valueOf(3000), BigDecimal.valueOf(1500), 21);
        
        log.info("Configurations de livraison par défaut créées avec succès");
    }
    
    private void creerConfigurationParDefaut(String pays, Livraison.TypeLivraison type, 
                                           BigDecimal tarifBase, BigDecimal tarifParKg, Integer delai) {
        ConfigurationLivraison configuration = ConfigurationLivraison.builder()
                .type(type)
                .pays(pays)
                .tarifBase(tarifBase)
                .tarifParKg(tarifParKg)
                .delaiJours(delai)
                .delaiMinJours(delai - 1)
                .delaiMaxJours(delai + 1)
                .minimumFacturation(pays.equals("SENEGAL") ? BigDecimal.valueOf(1000) : BigDecimal.valueOf(5000))
                .reductionGrosColis(BigDecimal.valueOf(10)) // 10% de réduction pour colis > 5kg
                .supplementVilleEloignee(BigDecimal.valueOf(15)) // 15% de supplément pour villes éloignées
                .actif(true)
                .description("Configuration par défaut pour " + pays + " - " + type)
                .modifiePar("system")
                .build();
        
        configurationLivraisonRepository.save(configuration);
    }
    
    private BigDecimal appliquerAjustements(ConfigurationLivraison config, BigDecimal coutBase, BigDecimal poids) {
        BigDecimal coutAjuste = coutBase;
        
        // Réduction pour gros colis (> 5 kg)
        if (config.getReductionGrosColis() != null && poids.compareTo(BigDecimal.valueOf(5)) > 0) {
            BigDecimal reduction = coutBase.multiply(config.getReductionGrosColis()).divide(BigDecimal.valueOf(100));
            coutAjuste = coutAjuste.subtract(reduction);
            log.debug("Réduction gros colis appliquée: {} FCFA", reduction);
        }
        
        return coutAjuste;
    }
    
    private BigDecimal calculerTarifParDefaut(String pays, Livraison.TypeLivraison type, BigDecimal poids) {
        // Tarifs par défaut si aucune configuration n'est trouvée
        BigDecimal tarifBase, tarifParKg;
        
        if ("SENEGAL".equalsIgnoreCase(pays)) {
            switch (type) {
                case EXPRESS -> { tarifBase = BigDecimal.valueOf(2000); tarifParKg = BigDecimal.valueOf(1000); }
                case STANDARD -> { tarifBase = BigDecimal.valueOf(1000); tarifParKg = BigDecimal.valueOf(500); }
                case ECONOMIQUE -> { tarifBase = BigDecimal.valueOf(500); tarifParKg = BigDecimal.valueOf(300); }
                default -> { tarifBase = BigDecimal.valueOf(1000); tarifParKg = BigDecimal.valueOf(500); }
            }
        } else {
            switch (type) {
                case EXPRESS -> { tarifBase = BigDecimal.valueOf(10000); tarifParKg = BigDecimal.valueOf(3000); }
                case STANDARD -> { tarifBase = BigDecimal.valueOf(5000); tarifParKg = BigDecimal.valueOf(2000); }
                case ECONOMIQUE -> { tarifBase = BigDecimal.valueOf(3000); tarifParKg = BigDecimal.valueOf(1500); }
                default -> { tarifBase = BigDecimal.valueOf(5000); tarifParKg = BigDecimal.valueOf(2000); }
            }
        }
        
        return tarifBase.add(poids.multiply(tarifParKg));
    }
    
    private Integer obtenirDelaiParDefaut(String pays, Livraison.TypeLivraison type) {
        if ("SENEGAL".equalsIgnoreCase(pays)) {
            return switch (type) {
                case EXPRESS -> 3;
                case STANDARD -> 7;
                case ECONOMIQUE -> 15;
                default -> 7;
            };
        } else {
            return switch (type) {
                case EXPRESS -> 7;
                case STANDARD -> 14;
                case ECONOMIQUE -> 21;
                default -> 14;
            };
        }
    }
    
    private ConfigurationLivraisonDto convertirEntityVersDto(ConfigurationLivraison configuration) {
        return ConfigurationLivraisonDto.builder()
                .id(configuration.getId())
                .type(configuration.getType())
                .pays(configuration.getPays())
                .tarifBase(configuration.getTarifBase())
                .tarifParKg(configuration.getTarifParKg())
                .delaiJours(configuration.getDelaiJours())
                .delaiMinJours(configuration.getDelaiMinJours())
                .delaiMaxJours(configuration.getDelaiMaxJours())
                .minimumFacturation(configuration.getMinimumFacturation())
                .reductionGrosColis(configuration.getReductionGrosColis())
                .supplementVilleEloignee(configuration.getSupplementVilleEloignee())
                .actif(configuration.getActif())
                .description(configuration.getDescription())
                .notes(configuration.getNotes())
                .dateCreation(configuration.getDateCreation())
                .dateModification(configuration.getDateModification())
                .modifiePar(configuration.getModifiePar())
                .build();
    }
}
