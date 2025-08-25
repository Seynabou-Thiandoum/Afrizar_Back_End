package sn.afrizar.afrizar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.afrizar.afrizar.dto.VendeurDto;
import sn.afrizar.afrizar.model.Utilisateur;
import sn.afrizar.afrizar.model.Vendeur;
import sn.afrizar.afrizar.repository.ProduitRepository;
import sn.afrizar.afrizar.repository.VendeurRepository;
import sn.afrizar.afrizar.service.VendeurService;

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
public class VendeurServiceImpl implements VendeurService {
    
    private final VendeurRepository vendeurRepository;
    private final ProduitRepository produitRepository;
    
    @Override
    public VendeurDto creerVendeur(VendeurDto vendeurDto) {
        log.info("Création d'un nouveau vendeur avec email: {}", vendeurDto.getEmail());
        
        if (vendeurRepository.existsByEmail(vendeurDto.getEmail())) {
            throw new RuntimeException("Un vendeur avec cet email existe déjà");
        }
        
        Vendeur vendeur = convertirDtoVersEntity(vendeurDto);
        vendeur.setRole(Utilisateur.Role.VENDEUR);
        vendeur.setDateCreation(LocalDateTime.now());
        vendeur.setActif(true);
        vendeur.setRating(BigDecimal.ZERO);
        vendeur.setNombreEvaluations(0);
        vendeur.setVerifie(false);
        
        Vendeur vendeurSauvegarde = vendeurRepository.save(vendeur);
        
        log.info("Vendeur créé avec succès avec ID: {}", vendeurSauvegarde.getId());
        return convertirEntityVersDto(vendeurSauvegarde);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<VendeurDto> obtenirVendeurParId(Long id) {
        return vendeurRepository.findById(id)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<VendeurDto> obtenirVendeurParEmail(String email) {
        return vendeurRepository.findByEmail(email)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<VendeurDto> obtenirTousLesVendeurs() {
        return vendeurRepository.findAll()
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<VendeurDto> obtenirVendeursAvecPagination(Pageable pageable) {
        return vendeurRepository.findAll(pageable)
                .map(this::convertirEntityVersDto);
    }
    
    @Override
    public VendeurDto mettreAJourVendeur(Long id, VendeurDto vendeurDto) {
        log.info("Mise à jour du vendeur avec ID: {}", id);
        
        Vendeur vendeur = vendeurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendeur non trouvé avec ID: " + id));
        
        // Vérifier si l'email est déjà utilisé par un autre vendeur
        if (!vendeur.getEmail().equals(vendeurDto.getEmail()) && 
            vendeurRepository.existsByEmail(vendeurDto.getEmail())) {
            throw new RuntimeException("Un vendeur avec cet email existe déjà");
        }
        
        // Mettre à jour les champs
        vendeur.setNom(vendeurDto.getNom());
        vendeur.setPrenom(vendeurDto.getPrenom());
        vendeur.setEmail(vendeurDto.getEmail());
        vendeur.setTelephone(vendeurDto.getTelephone());
        vendeur.setNomBoutique(vendeurDto.getNomBoutique());
        vendeur.setDescription(vendeurDto.getDescription());
        vendeur.setAdresseBoutique(vendeurDto.getAdresseBoutique());
        vendeur.setSpecialites(vendeurDto.getSpecialites());
        vendeur.setTauxCommissionPersonnalise(vendeurDto.getTauxCommissionPersonnalise());
        
        Vendeur vendeurMisAJour = vendeurRepository.save(vendeur);
        
        log.info("Vendeur mis à jour avec succès");
        return convertirEntityVersDto(vendeurMisAJour);
    }
    
    @Override
    public void supprimerVendeur(Long id) {
        log.info("Suppression du vendeur avec ID: {}", id);
        
        if (!vendeurRepository.existsById(id)) {
            throw new RuntimeException("Vendeur non trouvé avec ID: " + id);
        }
        
        vendeurRepository.deleteById(id);
        log.info("Vendeur supprimé avec succès");
    }
    
    @Override
    public void desactiverVendeur(Long id) {
        log.info("Désactivation du vendeur avec ID: {}", id);
        
        Vendeur vendeur = vendeurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendeur non trouvé avec ID: " + id));
        
        vendeur.setActif(false);
        vendeurRepository.save(vendeur);
        
        log.info("Vendeur désactivé avec succès");
    }
    
    @Override
    public void activerVendeur(Long id) {
        log.info("Activation du vendeur avec ID: {}", id);
        
        Vendeur vendeur = vendeurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendeur non trouvé avec ID: " + id));
        
        vendeur.setActif(true);
        vendeurRepository.save(vendeur);
        
        log.info("Vendeur activé avec succès");
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<VendeurDto> obtenirVendeursVerifies() {
        return vendeurRepository.findVendeursVerifiesOrderByRating()
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<VendeurDto> obtenirVendeursActifs() {
        return vendeurRepository.findByActif(true)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<VendeurDto> obtenirVendeursParRatingMinimum(BigDecimal rating) {
        return vendeurRepository.findByRatingGreaterThanEqualOrderByRatingDesc(rating)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<VendeurDto> rechercherVendeurs(String terme) {
        return vendeurRepository.findByNomBoutiqueContainingOrSpecialitesContaining(terme, terme)
                .stream()
                .map(this::convertirEntityVersDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public VendeurDto verifierVendeur(Long vendeurId) {
        log.info("Vérification du vendeur avec ID: {}", vendeurId);
        
        Vendeur vendeur = vendeurRepository.findById(vendeurId)
                .orElseThrow(() -> new RuntimeException("Vendeur non trouvé avec ID: " + vendeurId));
        
        vendeur.setVerifie(true);
        Vendeur vendeurMisAJour = vendeurRepository.save(vendeur);
        
        log.info("Vendeur vérifié avec succès");
        return convertirEntityVersDto(vendeurMisAJour);
    }
    
    @Override
    public VendeurDto annulerVerificationVendeur(Long vendeurId) {
        log.info("Annulation de la vérification du vendeur avec ID: {}", vendeurId);
        
        Vendeur vendeur = vendeurRepository.findById(vendeurId)
                .orElseThrow(() -> new RuntimeException("Vendeur non trouvé avec ID: " + vendeurId));
        
        vendeur.setVerifie(false);
        Vendeur vendeurMisAJour = vendeurRepository.save(vendeur);
        
        log.info("Vérification du vendeur annulée avec succès");
        return convertirEntityVersDto(vendeurMisAJour);
    }
    
    @Override
    public VendeurDto definirCommissionPersonnalisee(Long vendeurId, BigDecimal tauxCommission) {
        log.info("Définition d'une commission personnalisée de {}% pour le vendeur ID: {}", tauxCommission, vendeurId);
        
        if (tauxCommission.compareTo(BigDecimal.ZERO) < 0 || tauxCommission.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new RuntimeException("Le taux de commission doit être compris entre 0 et 100%");
        }
        
        Vendeur vendeur = vendeurRepository.findById(vendeurId)
                .orElseThrow(() -> new RuntimeException("Vendeur non trouvé avec ID: " + vendeurId));
        
        vendeur.setTauxCommissionPersonnalise(tauxCommission);
        Vendeur vendeurMisAJour = vendeurRepository.save(vendeur);
        
        log.info("Commission personnalisée définie avec succès");
        return convertirEntityVersDto(vendeurMisAJour);
    }
    
    @Override
    public VendeurDto ajouterEvaluation(Long vendeurId, BigDecimal note) {
        log.info("Ajout d'une évaluation ({}) pour le vendeur ID: {}", note, vendeurId);
        
        if (note.compareTo(BigDecimal.ZERO) < 0 || note.compareTo(BigDecimal.valueOf(5)) > 0) {
            throw new RuntimeException("La note doit être comprise entre 0 et 5");
        }
        
        Vendeur vendeur = vendeurRepository.findById(vendeurId)
                .orElseThrow(() -> new RuntimeException("Vendeur non trouvé avec ID: " + vendeurId));
        
        // Calculer la nouvelle moyenne
        BigDecimal sommeTotale = vendeur.getRating().multiply(BigDecimal.valueOf(vendeur.getNombreEvaluations()));
        sommeTotale = sommeTotale.add(note);
        vendeur.setNombreEvaluations(vendeur.getNombreEvaluations() + 1);
        vendeur.setRating(sommeTotale.divide(BigDecimal.valueOf(vendeur.getNombreEvaluations()), 1, RoundingMode.HALF_UP));
        
        Vendeur vendeurMisAJour = vendeurRepository.save(vendeur);
        
        log.info("Évaluation ajoutée. Nouveau rating: {} ({} évaluations)", 
                vendeur.getRating(), vendeur.getNombreEvaluations());
        return convertirEntityVersDto(vendeurMisAJour);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getNombreProduitsVendeur(Long vendeurId) {
        if (!vendeurRepository.existsById(vendeurId)) {
            throw new RuntimeException("Vendeur non trouvé avec ID: " + vendeurId);
        }
        return produitRepository.countProduitsByVendeur(vendeurId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getNombreVendeursVerifies() {
        return vendeurRepository.countVendeursVerifies();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean verifierEmailDisponible(String email) {
        return !vendeurRepository.existsByEmail(email);
    }
    
    // Méthodes de conversion
    private VendeurDto convertirEntityVersDto(Vendeur vendeur) {
        VendeurDto dto = new VendeurDto();
        dto.setId(vendeur.getId());
        dto.setNom(vendeur.getNom());
        dto.setPrenom(vendeur.getPrenom());
        dto.setEmail(vendeur.getEmail());
        dto.setTelephone(vendeur.getTelephone());
        dto.setRole(vendeur.getRole());
        dto.setActif(vendeur.isActif());
        dto.setDateCreation(vendeur.getDateCreation());
        dto.setDerniereConnexion(vendeur.getDerniereConnexion());
        dto.setNomBoutique(vendeur.getNomBoutique());
        dto.setDescription(vendeur.getDescription());
        dto.setAdresseBoutique(vendeur.getAdresseBoutique());
        dto.setRating(vendeur.getRating());
        dto.setNombreEvaluations(vendeur.getNombreEvaluations());
        dto.setTauxCommissionPersonnalise(vendeur.getTauxCommissionPersonnalise());
        dto.setVerifie(vendeur.isVerifie());
        dto.setSpecialites(vendeur.getSpecialites());
        return dto;
    }
    
    private Vendeur convertirDtoVersEntity(VendeurDto dto) {
        Vendeur vendeur = new Vendeur();
        vendeur.setId(dto.getId());
        vendeur.setNom(dto.getNom());
        vendeur.setPrenom(dto.getPrenom());
        vendeur.setEmail(dto.getEmail());
        vendeur.setTelephone(dto.getTelephone());
        vendeur.setNomBoutique(dto.getNomBoutique());
        vendeur.setDescription(dto.getDescription());
        vendeur.setAdresseBoutique(dto.getAdresseBoutique());
        vendeur.setRating(dto.getRating() != null ? dto.getRating() : BigDecimal.ZERO);
        vendeur.setNombreEvaluations(dto.getNombreEvaluations() != null ? dto.getNombreEvaluations() : 0);
        vendeur.setTauxCommissionPersonnalise(dto.getTauxCommissionPersonnalise());
        vendeur.setVerifie(dto.isVerifie());
        vendeur.setSpecialites(dto.getSpecialites());
        return vendeur;
    }
}

