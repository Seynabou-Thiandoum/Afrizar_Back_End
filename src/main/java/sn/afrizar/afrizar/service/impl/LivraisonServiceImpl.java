package sn.afrizar.afrizar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.afrizar.afrizar.dto.CreateLivraisonDto;
import sn.afrizar.afrizar.dto.LivraisonDto;
import sn.afrizar.afrizar.model.Commande;
import sn.afrizar.afrizar.model.Livraison;
import sn.afrizar.afrizar.repository.CommandeRepository;
import sn.afrizar.afrizar.repository.LivraisonRepository;
import sn.afrizar.afrizar.service.LivraisonService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LivraisonServiceImpl implements LivraisonService {
    
    private final LivraisonRepository livraisonRepository;
    private final CommandeRepository commandeRepository;
    
    // Tarifs de base par kg selon la destination et le type
    private static final Map<String, Map<Livraison.TypeLivraison, BigDecimal>> TARIFS_BASE = new HashMap<>();
    
    static {
        // Sénégal (local)
        Map<Livraison.TypeLivraison, BigDecimal> tarifsSenegal = new HashMap<>();
        tarifsSenegal.put(Livraison.TypeLivraison.EXPRESS, BigDecimal.valueOf(3000));     // Express: 3000 FCFA/kg
        tarifsSenegal.put(Livraison.TypeLivraison.STANDARD, BigDecimal.valueOf(2000));    // Standard: 2000 FCFA/kg
        tarifsSenegal.put(Livraison.TypeLivraison.ECONOMIQUE, BigDecimal.valueOf(1500)); // Economique: 1500 FCFA/kg
        TARIFS_BASE.put("SENEGAL", tarifsSenegal);
        TARIFS_BASE.put("SN", tarifsSenegal);
        
        // France
        Map<Livraison.TypeLivraison, BigDecimal> tarifsFrance = new HashMap<>();
        tarifsFrance.put(Livraison.TypeLivraison.EXPRESS, BigDecimal.valueOf(18000));     // Express: 18000 FCFA/kg
        tarifsFrance.put(Livraison.TypeLivraison.STANDARD, BigDecimal.valueOf(12000));    // Standard: 12000 FCFA/kg
        tarifsFrance.put(Livraison.TypeLivraison.ECONOMIQUE, BigDecimal.valueOf(8000));  // Economique: 8000 FCFA/kg
        TARIFS_BASE.put("FRANCE", tarifsFrance);
        TARIFS_BASE.put("FR", tarifsFrance);
        
        // USA
        Map<Livraison.TypeLivraison, BigDecimal> tarifsUSA = new HashMap<>();
        tarifsUSA.put(Livraison.TypeLivraison.EXPRESS, BigDecimal.valueOf(15000));       // Express: 15000 FCFA/kg
        tarifsUSA.put(Livraison.TypeLivraison.STANDARD, BigDecimal.valueOf(10000));      // Standard: 10000 FCFA/kg
        tarifsUSA.put(Livraison.TypeLivraison.ECONOMIQUE, BigDecimal.valueOf(7000));    // Economique: 7000 FCFA/kg
        TARIFS_BASE.put("USA", tarifsUSA);
        TARIFS_BASE.put("ETATS-UNIS", tarifsUSA);
        TARIFS_BASE.put("US", tarifsUSA);
        
        // Canada
        Map<Livraison.TypeLivraison, BigDecimal> tarifsCanada = new HashMap<>();
        tarifsCanada.put(Livraison.TypeLivraison.EXPRESS, BigDecimal.valueOf(16000));    // Express: 16000 FCFA/kg
        tarifsCanada.put(Livraison.TypeLivraison.STANDARD, BigDecimal.valueOf(11000));   // Standard: 11000 FCFA/kg
        tarifsCanada.put(Livraison.TypeLivraison.ECONOMIQUE, BigDecimal.valueOf(8000)); // Economique: 8000 FCFA/kg
        TARIFS_BASE.put("CANADA", tarifsCanada);
        TARIFS_BASE.put("CA", tarifsCanada);
        
        // Autres pays d'Afrique
        Map<Livraison.TypeLivraison, BigDecimal> tarifsAfrique = new HashMap<>();
        tarifsAfrique.put(Livraison.TypeLivraison.EXPRESS, BigDecimal.valueOf(8000));    // Express: 8000 FCFA/kg
        tarifsAfrique.put(Livraison.TypeLivraison.STANDARD, BigDecimal.valueOf(5000));   // Standard: 5000 FCFA/kg
        tarifsAfrique.put(Livraison.TypeLivraison.ECONOMIQUE, BigDecimal.valueOf(3500)); // Economique: 3500 FCFA/kg
        TARIFS_BASE.put("AFRIQUE", tarifsAfrique);
        
        // Europe (autres pays)
        Map<Livraison.TypeLivraison, BigDecimal> tarifsEurope = new HashMap<>();
        tarifsEurope.put(Livraison.TypeLivraison.EXPRESS, BigDecimal.valueOf(20000));    // Express: 20000 FCFA/kg
        tarifsEurope.put(Livraison.TypeLivraison.STANDARD, BigDecimal.valueOf(14000));   // Standard: 14000 FCFA/kg
        tarifsEurope.put(Livraison.TypeLivraison.ECONOMIQUE, BigDecimal.valueOf(9000)); // Economique: 9000 FCFA/kg
        TARIFS_BASE.put("EUROPE", tarifsEurope);
        
        // Reste du monde
        Map<Livraison.TypeLivraison, BigDecimal> tarifsMondeReste = new HashMap<>();
        tarifsMondeReste.put(Livraison.TypeLivraison.EXPRESS, BigDecimal.valueOf(25000));    // Express: 25000 FCFA/kg
        tarifsMondeReste.put(Livraison.TypeLivraison.STANDARD, BigDecimal.valueOf(18000));   // Standard: 18000 FCFA/kg
        tarifsMondeReste.put(Livraison.TypeLivraison.ECONOMIQUE, BigDecimal.valueOf(12000)); // Economique: 12000 FCFA/kg
        TARIFS_BASE.put("RESTE_MONDE", tarifsMondeReste);
    }
    
    @Override
    public LivraisonDto creerLivraison(CreateLivraisonDto createLivraisonDto, Long commandeId) {
        log.info("Création d'une livraison pour la commande ID: {}", commandeId);
        
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec ID: " + commandeId));
        
        Livraison livraison = convertirCreateDtoVersEntity(createLivraisonDto);
        livraison.setCommande(commande);
        livraison.setNumeroSuivi(genererNumeroSuivi());
        
        // Calculer le poids total et le coût
        BigDecimal poidsTotal = calculerPoidsTotal(commande);
        livraison.setPoidsTotal(poidsTotal);
        
        BigDecimal cout = calculerCoutLivraison(
            poidsTotal, 
            createLivraisonDto.getPays(), 
            createLivraisonDto.getVille(), 
            createLivraisonDto.getType()
        );
        livraison.setCout(cout);
        
        // Calculer la date de livraison estimée
        LocalDate dateLivraisonEstimee = calculerDateLivraisonEstimee(
            createLivraisonDto.getPays(), 
            createLivraisonDto.getType()
        );
        livraison.setDateLivraisonPrevue(dateLivraisonEstimee);
        
        Livraison livraisonSauvegardee = livraisonRepository.save(livraison);
        
        log.info("Livraison créée avec succès avec ID: {} (coût: {} FCFA, poids: {} kg)", 
                livraisonSauvegardee.getId(), cout, poidsTotal);
        
        return convertirEntityVersDto(livraisonSauvegardee);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculerCoutLivraison(BigDecimal poids, String pays, String ville, Livraison.TypeLivraison type) {
        log.debug("Calcul du coût de livraison: poids={} kg, pays={}, ville={}, type={}", 
                 poids, pays, ville, type);
        
        // Rechercher les tarifs pour le pays
        Map<Livraison.TypeLivraison, BigDecimal> tarifsPayS = obtenirTarifsPays(pays);
        BigDecimal tarifParKg = tarifsPayS.get(type);
        
        if (tarifParKg == null) {
            log.warn("Tarif non trouvé pour {} {} {}, utilisation du tarif standard", pays, type, ville);
            tarifParKg = tarifsPayS.get(Livraison.TypeLivraison.STANDARD);
        }
        
        // Calcul de base: poids × tarif par kg
        BigDecimal coutBase = poids.multiply(tarifParKg);
        
        // Appliquer des ajustements selon les règles métier
        BigDecimal coutFinal = appliquerAjustements(coutBase, poids, pays, ville, type);
        
        log.debug("Coût de livraison calculé: {} FCFA (base: {} FCFA)", coutFinal, coutBase);
        
        return coutFinal;
    }
    
    private Map<Livraison.TypeLivraison, BigDecimal> obtenirTarifsPays(String pays) {
        String paysUpper = pays.toUpperCase();
        
        // Recherche exacte
        if (TARIFS_BASE.containsKey(paysUpper)) {
            return TARIFS_BASE.get(paysUpper);
        }
        
        // Recherche par région pour les pays africains
        String[] paysAfricains = {"MALI", "BURKINA FASO", "COTE D'IVOIRE", "GHANA", "NIGERIA", 
                                 "CAMEROUN", "GABON", "MAROC", "TUNISIE", "ALGERIE"};
        for (String paysAfricain : paysAfricains) {
            if (paysUpper.contains(paysAfricain) || paysAfricain.contains(paysUpper)) {
                return TARIFS_BASE.get("AFRIQUE");
            }
        }
        
        // Recherche par région pour l'Europe
        String[] paysEuropeens = {"ALLEMAGNE", "ITALIE", "ESPAGNE", "BELGIQUE", "SUISSE", 
                                 "ROYAUME-UNI", "PAYS-BAS", "PORTUGAL"};
        for (String paysEuropeen : paysEuropeens) {
            if (paysUpper.contains(paysEuropeen) || paysEuropeen.contains(paysUpper)) {
                return TARIFS_BASE.get("EUROPE");
            }
        }
        
        // Par défaut: reste du monde
        log.info("Pays non reconnu: {}, utilisation des tarifs 'Reste du monde'", pays);
        return TARIFS_BASE.get("RESTE_MONDE");
    }
    
    private BigDecimal appliquerAjustements(BigDecimal coutBase, BigDecimal poids, String pays, String ville, Livraison.TypeLivraison type) {
        BigDecimal coutAjuste = coutBase;
        
        // Réduction pour gros colis (> 5 kg)
        if (poids.compareTo(BigDecimal.valueOf(5)) > 0) {
            BigDecimal reduction = coutBase.multiply(BigDecimal.valueOf(0.10)); // 10% de réduction
            coutAjuste = coutAjuste.subtract(reduction);
            log.debug("Réduction gros colis appliquée: {} FCFA", reduction);
        }
        
        // Supplément pour certaines villes éloignées
        if (ville != null && estVilleEloignee(ville, pays)) {
            BigDecimal supplement = coutBase.multiply(BigDecimal.valueOf(0.15)); // 15% de supplément
            coutAjuste = coutAjuste.add(supplement);
            log.debug("Supplément ville éloignée appliqué: {} FCFA", supplement);
        }
        
        // Minimum de facturation
        BigDecimal minimumFacturation = obtenirMinimumFacturation(pays, type);
        if (coutAjuste.compareTo(minimumFacturation) < 0) {
            coutAjuste = minimumFacturation;
            log.debug("Application du minimum de facturation: {} FCFA", minimumFacturation);
        }
        
        return coutAjuste;
    }
    
    private boolean estVilleEloignee(String ville, String pays) {
        // Logique pour déterminer si une ville est éloignée
        if ("SENEGAL".equalsIgnoreCase(pays) || "SN".equalsIgnoreCase(pays)) {
            String[] villesEloignees = {"KEDOUGOU", "TAMBACOUNDA", "KOLDA", "ZIGUINCHOR"};
            for (String villeEloignee : villesEloignees) {
                if (ville.toUpperCase().contains(villeEloignee)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private BigDecimal obtenirMinimumFacturation(String pays, Livraison.TypeLivraison type) {
        if ("SENEGAL".equalsIgnoreCase(pays) || "SN".equalsIgnoreCase(pays)) {
            return BigDecimal.valueOf(1000); // Minimum 1000 FCFA au Sénégal
        }
        return BigDecimal.valueOf(5000); // Minimum 5000 FCFA à l'international
    }
    
    @Override
    @Transactional(readOnly = true)
    public LocalDate calculerDateLivraisonEstimee(String pays, Livraison.TypeLivraison type) {
        LocalDate aujourdhui = LocalDate.now();
        int joursDelai;
        
        if ("SENEGAL".equalsIgnoreCase(pays) || "SN".equalsIgnoreCase(pays)) {
            // Délais pour le Sénégal
            switch (type) {
                case EXPRESS -> joursDelai = 1;
                case STANDARD -> joursDelai = 3;
                case ECONOMIQUE -> joursDelai = 5;
                default -> joursDelai = 3;
            }
        } else if (TARIFS_BASE.get("AFRIQUE").equals(obtenirTarifsPays(pays))) {
            // Autres pays d'Afrique
            switch (type) {
                case EXPRESS -> joursDelai = 5;
                case STANDARD -> joursDelai = 10;
                case ECONOMIQUE -> joursDelai = 15;
                default -> joursDelai = 10;
            }
        } else {
            // International
            switch (type) {
                case EXPRESS -> joursDelai = 7;
                case STANDARD -> joursDelai = 14;
                case ECONOMIQUE -> joursDelai = 21;
                default -> joursDelai = 14;
            }
        }
        
        return aujourdhui.plusDays(joursDelai);
    }
    
    private BigDecimal calculerPoidsTotal(Commande commande) {
        return commande.getLignesCommande().stream()
                .map(ligne -> {
                    BigDecimal poidsProduit = ligne.getProduit().getPoids() != null ? 
                        ligne.getProduit().getPoids() : BigDecimal.valueOf(0.5); // Poids par défaut: 0.5 kg
                    return poidsProduit.multiply(BigDecimal.valueOf(ligne.getQuantite()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public String genererNumeroSuivi() {
        return "AFRIZAR-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }
    
    // Autres méthodes de base...
    @Override
    @Transactional(readOnly = true)
    public Optional<LivraisonDto> obtenirLivraisonParId(Long id) {
        return livraisonRepository.findById(id)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<LivraisonDto> obtenirLivraisonParCommande(Long commandeId) {
        return livraisonRepository.findByCommandeId(commandeId)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<LivraisonDto> obtenirLivraisonParNumeroSuivi(String numeroSuivi) {
        return livraisonRepository.findByNumeroSuivi(numeroSuivi)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    public LivraisonDto mettreAJourLivraison(Long id, CreateLivraisonDto createLivraisonDto) {
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvée avec ID: " + id));
        
        // Mettre à jour les champs
        livraison.setType(createLivraisonDto.getType());
        livraison.setAdresseLivraison(createLivraisonDto.getAdresseLivraison());
        livraison.setVille(createLivraisonDto.getVille());
        livraison.setCodePostal(createLivraisonDto.getCodePostal());
        livraison.setPays(createLivraisonDto.getPays());
        livraison.setNotes(createLivraisonDto.getNotes());
        
        // Recalculer le coût si nécessaire
        BigDecimal nouveauCout = calculerCoutLivraison(
            livraison.getPoidsTotal(),
            createLivraisonDto.getPays(),
            createLivraisonDto.getVille(),
            createLivraisonDto.getType()
        );
        livraison.setCout(nouveauCout);
        
        Livraison livraisonMiseAJour = livraisonRepository.save(livraison);
        return convertirEntityVersDto(livraisonMiseAJour);
    }
    
    @Override
    public LivraisonDto changerStatutLivraison(Long id, Livraison.StatutLivraison nouveauStatut) {
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvée avec ID: " + id));
        
        livraison.setStatut(nouveauStatut);
        
        // Mettre à jour les dates selon le statut
        LocalDate aujourd = LocalDate.now();
        switch (nouveauStatut) {
            case EXPEDIE -> livraison.setDateExpedition(aujourd);
            case LIVRE -> livraison.setDateLivraisonEffective(aujourd);
        }
        
        Livraison livraisonMiseAJour = livraisonRepository.save(livraison);
        return convertirEntityVersDto(livraisonMiseAJour);
    }
    
    @Override
    public LivraisonDto expedierCommande(Long livraisonId, String numeroSuivi, String transporteur) {
        Livraison livraison = livraisonRepository.findById(livraisonId)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvée avec ID: " + livraisonId));
        
        livraison.setStatut(Livraison.StatutLivraison.EXPEDIE);
        livraison.setNumeroSuivi(numeroSuivi);
        livraison.setTransporteur(transporteur);
        livraison.setDateExpedition(LocalDate.now());
        
        Livraison livraisonMiseAJour = livraisonRepository.save(livraison);
        return convertirEntityVersDto(livraisonMiseAJour);
    }
    
    @Override
    public LivraisonDto livrerCommande(Long livraisonId) {
        return changerStatutLivraison(livraisonId, Livraison.StatutLivraison.LIVRE);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LivraisonDto> obtenirLivraisonsParStatut(Livraison.StatutLivraison statut) {
        return livraisonRepository.findByStatut(statut)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LivraisonDto> obtenirLivraisonsParPays(String pays) {
        return livraisonRepository.findByPays(pays)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LivraisonDto> obtenirExpeditionsParDate(LocalDate date) {
        return livraisonRepository.findExpeditionsParDate(date)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LivraisonDto> obtenirLivraisonsPrevuesParDate(LocalDate date) {
        return livraisonRepository.findLivraisonsPrevuesParDate(date)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LivraisonDto> obtenirLivraisonsEnRetard() {
        return livraisonRepository.findLivraisonsEnRetard(LocalDate.now())
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getNombreLivraisonsParStatut(Livraison.StatutLivraison statut) {
        return livraisonRepository.countByStatut(statut);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getStatistiquesLivraisonsParPays() {
        return livraisonRepository.getStatistiquesLivraisonsParPays();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getDelaiMoyenLivraisonParType() {
        return livraisonRepository.getDelaiMoyenLivraisonParType();
    }
    
    // Méthodes de conversion
    private LivraisonDto convertirEntityVersDto(Livraison livraison) {
        LivraisonDto dto = new LivraisonDto();
        dto.setId(livraison.getId());
        dto.setCommandeId(livraison.getCommande().getId());
        dto.setType(livraison.getType());
        dto.setAdresseLivraison(livraison.getAdresseLivraison());
        dto.setVille(livraison.getVille());
        dto.setCodePostal(livraison.getCodePostal());
        dto.setPays(livraison.getPays());
        dto.setCout(livraison.getCout());
        dto.setPoidsTotal(livraison.getPoidsTotal());
        dto.setStatut(livraison.getStatut());
        dto.setDateExpedition(livraison.getDateExpedition());
        dto.setDateLivraisonPrevue(livraison.getDateLivraisonPrevue());
        dto.setDateLivraisonEffective(livraison.getDateLivraisonEffective());
        dto.setNumeroSuivi(livraison.getNumeroSuivi());
        dto.setTransporteur(livraison.getTransporteur());
        dto.setDateCreation(livraison.getDateCreation());
        dto.setNotes(livraison.getNotes());
        return dto;
    }
    
    private Livraison convertirCreateDtoVersEntity(CreateLivraisonDto dto) {
        Livraison livraison = new Livraison();
        livraison.setType(dto.getType());
        livraison.setAdresseLivraison(dto.getAdresseLivraison());
        livraison.setVille(dto.getVille());
        livraison.setCodePostal(dto.getCodePostal());
        livraison.setPays(dto.getPays());
        livraison.setNotes(dto.getNotes());
        livraison.setStatut(Livraison.StatutLivraison.EN_PREPARATION);
        return livraison;
    }
}

