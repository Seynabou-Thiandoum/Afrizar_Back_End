package sn.afrizar.afrizar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.afrizar.afrizar.dto.CreateProduitDto;
import sn.afrizar.afrizar.dto.ProduitDto;
import sn.afrizar.afrizar.model.Categorie;
import sn.afrizar.afrizar.model.Produit;
import sn.afrizar.afrizar.model.Vendeur;
import sn.afrizar.afrizar.repository.CategorieRepository;
import sn.afrizar.afrizar.repository.ProduitRepository;
import sn.afrizar.afrizar.repository.VendeurRepository;
import sn.afrizar.afrizar.service.ProduitService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProduitServiceImpl implements ProduitService {
    
    private final ProduitRepository produitRepository;
    private final VendeurRepository vendeurRepository;
    private final CategorieRepository categorieRepository;
    
    @Override
    public ProduitDto creerProduit(CreateProduitDto createProduitDto) {
        log.info("Création d'un nouveau produit: {}", createProduitDto.getNom());
        
        // Vérifier que le vendeur existe
        Vendeur vendeur = vendeurRepository.findById(createProduitDto.getVendeurId())
                .orElseThrow(() -> new RuntimeException("Vendeur non trouvé avec ID: " + createProduitDto.getVendeurId()));
        
        // Vérifier que la catégorie existe (si fournie)
        Categorie categorie = null;
        if (createProduitDto.getCategorieId() != null) {
            categorie = categorieRepository.findById(createProduitDto.getCategorieId())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec ID: " + createProduitDto.getCategorieId()));
        }
        
        Produit produit = convertirCreateDtoVersEntity(createProduitDto);
        produit.setVendeur(vendeur);
        produit.setCategorie(categorie);
        produit.setDateCreation(LocalDateTime.now());
        produit.setStatut(Produit.StatutProduit.ACTIF);
        
        // Déterminer la disponibilité en fonction du stock
        if (produit.getStock() != null && produit.getStock() > 0) {
            produit.setDisponibilite(Produit.Disponibilite.EN_STOCK);
        } else if (produit.getDelaiProduction() != null && produit.getDelaiProduction() > 0) {
            produit.setDisponibilite(Produit.Disponibilite.SUR_COMMANDE);
        } else {
            produit.setDisponibilite(Produit.Disponibilite.RUPTURE_STOCK);
        }
        
        Produit produitSauvegarde = produitRepository.save(produit);
        
        log.info("Produit créé avec succès avec ID: {}", produitSauvegarde.getId());
        return convertirEntityVersDto(produitSauvegarde);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ProduitDto> obtenirProduitParId(Long id) {
        return produitRepository.findById(id)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProduitDto> obtenirTousLesProduitsActifs() {
        return produitRepository.findByStatut(Produit.StatutProduit.ACTIF)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProduitDto> obtenirProduitsAvecPagination(Pageable pageable) {
        return produitRepository.findAll(pageable)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    public ProduitDto mettreAJourProduit(Long id, CreateProduitDto createProduitDto) {
        log.info("Mise à jour du produit avec ID: {}", id);
        
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec ID: " + id));
        
        // Vérifier que le vendeur existe
        if (!createProduitDto.getVendeurId().equals(produit.getVendeur().getId())) {
            Vendeur nouveauVendeur = vendeurRepository.findById(createProduitDto.getVendeurId())
                    .orElseThrow(() -> new RuntimeException("Vendeur non trouvé avec ID: " + createProduitDto.getVendeurId()));
            produit.setVendeur(nouveauVendeur);
        }
        
        // Vérifier la catégorie (si fournie)
        if (createProduitDto.getCategorieId() != null) {
            Categorie categorie = categorieRepository.findById(createProduitDto.getCategorieId())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec ID: " + createProduitDto.getCategorieId()));
            produit.setCategorie(categorie);
        }
        
        // Mettre à jour les champs
        produit.setNom(createProduitDto.getNom());
        produit.setDescription(createProduitDto.getDescription());
        produit.setPhotos(createProduitDto.getPhotos());
        produit.setPrix(createProduitDto.getPrix());
        produit.setStock(createProduitDto.getStock());
        produit.setDelaiProduction(createProduitDto.getDelaiProduction());
        produit.setPoids(createProduitDto.getPoids());
        produit.setTaillesDisponibles(createProduitDto.getTaillesDisponibles());
        
        // Mettre à jour les détails spécifiques du produit
        produit.setTaille(createProduitDto.getTaille());
        produit.setCouleur(createProduitDto.getCouleur());
        produit.setMatiere(createProduitDto.getMatiere());
        
        produit.setQualite(createProduitDto.getQualite());
        produit.setPersonnalisable(createProduitDto.isPersonnalisable());
        produit.setOptionsPersonnalisation(createProduitDto.getOptionsPersonnalisation());
        produit.setDateModification(LocalDateTime.now());
        
        // Mettre à jour la disponibilité
        mettreAJourDisponibilite(produit);
        
        Produit produitMisAJour = produitRepository.save(produit);
        
        log.info("Produit mis à jour avec succès");
        return convertirEntityVersDto(produitMisAJour);
    }
    
    @Override
    public void supprimerProduit(Long id) {
        log.info("Suppression du produit avec ID: {}", id);
        
        if (!produitRepository.existsById(id)) {
            throw new RuntimeException("Produit non trouvé avec ID: " + id);
        }
        
        produitRepository.deleteById(id);
        log.info("Produit supprimé avec succès");
    }
    
    @Override
    public void archiverProduit(Long id) {
        log.info("Archivage du produit avec ID: {}", id);
        
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec ID: " + id));
        
        produit.setStatut(Produit.StatutProduit.ARCHIVE);
        produitRepository.save(produit);
        
        log.info("Produit archivé avec succès");
    }
    
    @Override
    public void activerProduit(Long id) {
        log.info("Activation du produit avec ID: {}", id);
        
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec ID: " + id));
        
        produit.setStatut(Produit.StatutProduit.ACTIF);
        mettreAJourDisponibilite(produit);
        produitRepository.save(produit);
        
        log.info("Produit activé avec succès");
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProduitDto> obtenirProduitsParVendeur(Long vendeurId) {
        return produitRepository.findByVendeurId(vendeurId)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProduitDto> obtenirProduitsParCategorie(Long categorieId) {
        return produitRepository.findByCategorieId(categorieId)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProduitDto> rechercherProduitsParNom(String nom) {
        return produitRepository.findByNomContainingIgnoreCase(nom)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProduitDto> rechercherProduitsAvecFiltres(String nom, Long categorieId, Long vendeurId, 
                                                         BigDecimal prixMin, BigDecimal prixMax, 
                                                         Produit.Qualite qualite, Pageable pageable) {
        return produitRepository.findProduitsAvecFiltres(nom, categorieId, vendeurId, prixMin, prixMax, qualite, pageable)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProduitDto> obtenirProduitsEnStock() {
        return produitRepository.findProduitsEnStock()
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProduitDto> obtenirProduitsSurCommande() {
        return produitRepository.findProduitsSurCommande()
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProduitDto> obtenirProduitsMieuxNotes(Pageable pageable) {
        return produitRepository.findProduitsMieuxNotes(pageable)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProduitDto> obtenirProduitsPlusVus(Pageable pageable) {
        return produitRepository.findProduitsPlusVus(pageable)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProduitDto> obtenirProduitsParTaille(Produit.Taille taille) {
        return produitRepository.findByTailleDisponible(taille)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public ProduitDto incrementerVues(Long produitId) {
        log.debug("Incrémentation du nombre de vues pour le produit ID: {}", produitId);
        
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec ID: " + produitId));
        
        produit.setNombreVues(produit.getNombreVues() + 1);
        Produit produitMisAJour = produitRepository.save(produit);
        
        return convertirEntityVersDto(produitMisAJour);
    }
    
    @Override
    public ProduitDto ajouterEvaluation(Long produitId, BigDecimal note) {
        log.info("Ajout d'une évaluation ({}) pour le produit ID: {}", note, produitId);
        
        if (note.compareTo(BigDecimal.ZERO) < 0 || note.compareTo(BigDecimal.valueOf(5)) > 0) {
            throw new RuntimeException("La note doit être comprise entre 0 et 5");
        }
        
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec ID: " + produitId));
        
        // Calculer la nouvelle moyenne
        BigDecimal sommeTotale = produit.getNoteMoyenne().multiply(BigDecimal.valueOf(produit.getNombreEvaluations()));
        sommeTotale = sommeTotale.add(note);
        produit.setNombreEvaluations(produit.getNombreEvaluations() + 1);
        produit.setNoteMoyenne(sommeTotale.divide(BigDecimal.valueOf(produit.getNombreEvaluations()), 1, RoundingMode.HALF_UP));
        
        Produit produitMisAJour = produitRepository.save(produit);
        
        log.info("Évaluation ajoutée. Nouvelle moyenne: {} ({} évaluations)", 
                produit.getNoteMoyenne(), produit.getNombreEvaluations());
        return convertirEntityVersDto(produitMisAJour);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean verifierDisponibiliteStock(Long produitId, Integer quantite) {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec ID: " + produitId));
        
        return produit.getStock() != null && produit.getStock() >= quantite;
    }
    
    @Override
    public ProduitDto mettreAJourStock(Long produitId, Integer nouvelleQuantite) {
        log.info("Mise à jour du stock pour le produit ID: {} -> {}", produitId, nouvelleQuantite);
        
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec ID: " + produitId));
        
        produit.setStock(nouvelleQuantite);
        mettreAJourDisponibilite(produit);
        
        Produit produitMisAJour = produitRepository.save(produit);
        
        log.info("Stock mis à jour avec succès");
        return convertirEntityVersDto(produitMisAJour);
    }
    
    @Override
    public ProduitDto reduireStock(Long produitId, Integer quantite) {
        log.info("Réduction du stock pour le produit ID: {} de {}", produitId, quantite);
        
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec ID: " + produitId));
        
        if (produit.getStock() == null || produit.getStock() < quantite) {
            throw new RuntimeException("Stock insuffisant. Disponible: " + produit.getStock() + ", Demandé: " + quantite);
        }
        
        produit.setStock(produit.getStock() - quantite);
        mettreAJourDisponibilite(produit);
        
        Produit produitMisAJour = produitRepository.save(produit);
        
        log.info("Stock réduit avec succès. Nouveau stock: {}", produit.getStock());
        return convertirEntityVersDto(produitMisAJour);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getNombreProduitsParVendeur(Long vendeurId) {
        return produitRepository.countProduitsByVendeur(vendeurId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getMoyennePrix() {
        return produitRepository.getMoyennePrix();
    }
    
    // ===================== MÉTHODES D'ADMINISTRATION =====================
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProduitDto> obtenirProduitsEnAttente(Pageable pageable) {
        log.info("Récupération des produits en attente de validation");
        return produitRepository.findByStatut(Produit.StatutProduit.EN_ATTENTE_VALIDATION, pageable)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProduitDto> obtenirTousLesProduitsAdmin(Pageable pageable) {
        log.info("Récupération de tous les produits pour l'admin");
        return produitRepository.findAll(pageable)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProduitDto> obtenirProduitsParStatut(Produit.StatutProduit statut, Pageable pageable) {
        log.info("Récupération des produits avec le statut: {}", statut);
        return produitRepository.findByStatut(statut, pageable)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    public ProduitDto validerProduit(Long produitId) {
        log.info("Validation du produit avec ID: {}", produitId);
        
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec ID: " + produitId));
        
        if (produit.getStatut() != Produit.StatutProduit.EN_ATTENTE_VALIDATION) {
            log.warn("Le produit {} n'est pas en attente de validation. Statut actuel: {}", 
                    produitId, produit.getStatut());
        }
        
        produit.setStatut(Produit.StatutProduit.ACTIF);
        mettreAJourDisponibilite(produit);
        
        Produit produitValide = produitRepository.save(produit);
        
        log.info("Produit {} validé et publié avec succès", produitId);
        return convertirEntityVersDto(produitValide);
    }
    
    @Override
    public void rejeterProduit(Long produitId, String motif) {
        log.info("Rejet du produit avec ID: {} - Motif: {}", produitId, motif);
        
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec ID: " + produitId));
        
        produit.setStatut(Produit.StatutProduit.INACTIF);
        
        // On pourrait stocker le motif dans un champ dédié si nécessaire
        if (motif != null && !motif.isEmpty()) {
            String descriptionAvecMotif = produit.getDescription() + 
                    "\n\n[REJETÉ PAR ADMIN] Motif: " + motif;
            produit.setDescription(descriptionAvecMotif);
        }
        
        produitRepository.save(produit);
        
        log.info("Produit {} rejeté avec succès", produitId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long compterProduits() {
        return produitRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long compterProduitsActifs() {
        return produitRepository.countByStatut(Produit.StatutProduit.ACTIF);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long compterProduitsEnAttente() {
        return produitRepository.countByStatut(Produit.StatutProduit.EN_ATTENTE_VALIDATION);
    }
    
    // Méthodes utilitaires
    private void mettreAJourDisponibilite(Produit produit) {
        if (produit.getStock() != null && produit.getStock() > 0) {
            produit.setDisponibilite(Produit.Disponibilite.EN_STOCK);
        } else if (produit.getDelaiProduction() != null && produit.getDelaiProduction() > 0) {
            produit.setDisponibilite(Produit.Disponibilite.SUR_COMMANDE);
        } else {
            produit.setDisponibilite(Produit.Disponibilite.RUPTURE_STOCK);
        }
    }
    
    // Méthodes de conversion
    private ProduitDto convertirEntityVersDto(Produit produit) {
        ProduitDto dto = new ProduitDto();
        dto.setId(produit.getId());
        dto.setNom(produit.getNom());
        dto.setDescription(produit.getDescription());
        dto.setPhotos(produit.getPhotos());
        dto.setPrix(produit.getPrix());
        dto.setStock(produit.getStock());
        dto.setDelaiProduction(produit.getDelaiProduction());
        dto.setPoids(produit.getPoids());
        dto.setTaillesDisponibles(produit.getTaillesDisponibles());
        
        // Détails spécifiques du produit
        dto.setTaille(produit.getTaille());
        dto.setCouleur(produit.getCouleur());
        dto.setMatiere(produit.getMatiere());
        
        dto.setQualite(produit.getQualite());
        dto.setPersonnalisable(produit.isPersonnalisable());
        dto.setOptionsPersonnalisation(produit.getOptionsPersonnalisation());
        dto.setStatut(produit.getStatut());
        dto.setDisponibilite(produit.getDisponibilite());
        dto.setNombreVues(produit.getNombreVues());
        dto.setNoteMoyenne(produit.getNoteMoyenne());
        dto.setNombreEvaluations(produit.getNombreEvaluations());
        dto.setDateCreation(produit.getDateCreation());
        dto.setDateModification(produit.getDateModification());
        
        // Relations
        if (produit.getVendeur() != null) {
            dto.setVendeurId(produit.getVendeur().getId());
            dto.setNomVendeur(produit.getVendeur().getNom() + " " + produit.getVendeur().getPrenom());
            dto.setNomBoutique(produit.getVendeur().getNomBoutique());
        }
        
        if (produit.getCategorie() != null) {
            dto.setCategorieId(produit.getCategorie().getId());
            dto.setNomCategorie(produit.getCategorie().getNom());
        }
        
        return dto;
    }
    
    private Produit convertirCreateDtoVersEntity(CreateProduitDto dto) {
        Produit produit = new Produit();
        produit.setNom(dto.getNom());
        produit.setDescription(dto.getDescription());
        produit.setPhotos(dto.getPhotos());
        produit.setPrix(dto.getPrix());
        produit.setStock(dto.getStock() != null ? dto.getStock() : 0);
        produit.setDelaiProduction(dto.getDelaiProduction() != null ? dto.getDelaiProduction() : 0);
        produit.setPoids(dto.getPoids());
        produit.setTaillesDisponibles(dto.getTaillesDisponibles());
        
        // Détails spécifiques du produit
        produit.setTaille(dto.getTaille());
        produit.setCouleur(dto.getCouleur());
        produit.setMatiere(dto.getMatiere());
        
        produit.setQualite(dto.getQualite() != null ? dto.getQualite() : Produit.Qualite.STANDARD);
        produit.setPersonnalisable(dto.isPersonnalisable());
        produit.setOptionsPersonnalisation(dto.getOptionsPersonnalisation());
        produit.setNombreVues(0L);
        produit.setNoteMoyenne(BigDecimal.ZERO);
        produit.setNombreEvaluations(0);
        return produit;
    }
}

