package sn.afrizar.afrizar.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sn.afrizar.afrizar.dto.VenteFlashDto;
import sn.afrizar.afrizar.model.Produit;
import sn.afrizar.afrizar.model.ProduitVenteFlash;
import sn.afrizar.afrizar.model.VenteFlash;
import sn.afrizar.afrizar.repository.VenteFlashRepository;
import sn.afrizar.afrizar.service.VenteFlashService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VenteFlashServiceImpl implements VenteFlashService {
    
    private final VenteFlashRepository venteFlashRepository;
    
    @Override
    public Optional<VenteFlashDto> obtenirVenteFlashActive() {
        Optional<VenteFlash> venteFlash = venteFlashRepository.findVenteFlashActivePrincipale(LocalDateTime.now());
        
        return venteFlash.map(this::convertirEnDto);
    }
    
    @Override
    public List<VenteFlashDto> obtenirToutesVentesFlashActives() {
        List<VenteFlash> ventesFlash = venteFlashRepository.findVentesFlashEnCours(LocalDateTime.now());
        
        return ventesFlash.stream()
                .map(this::convertirEnDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public VenteFlashDto obtenirVenteFlashAvecProduits(Long id) {
        Optional<VenteFlash> venteFlash = venteFlashRepository.findById(id);
        return venteFlash.map(this::convertirEnDtoAvecProduits).orElse(null);
    }
    
    private VenteFlashDto convertirEnDto(VenteFlash venteFlash) {
        VenteFlashDto dto = new VenteFlashDto();
        dto.setId(venteFlash.getId());
        dto.setNom(venteFlash.getNom());
        dto.setDescription(venteFlash.getDescription());
        dto.setDateDebut(venteFlash.getDateDebut());
        dto.setDateFin(venteFlash.getDateFin());
        dto.setActif(venteFlash.getActif());
        dto.setPourcentageReductionParDefaut(venteFlash.getPourcentageReductionParDefaut());
        dto.setEstEnCours(venteFlash.estEnCours());
        dto.setTempsRestantMillisecondes(venteFlash.getTempsRestantMillisecondes());
        return dto;
    }
    
    private VenteFlashDto convertirEnDtoAvecProduits(VenteFlash venteFlash) {
        VenteFlashDto dto = convertirEnDto(venteFlash);
        
        List<VenteFlashDto.ProduitVenteFlashDto> produitsDto = venteFlash.getProduitsVenteFlash().stream()
                .map(pvf -> convertirProduitEnDto(pvf))
                .collect(Collectors.toList());
        
        dto.setProduits(produitsDto);
        return dto;
    }
    
    private VenteFlashDto.ProduitVenteFlashDto convertirProduitEnDto(ProduitVenteFlash produitVenteFlash) {
        Produit produit = produitVenteFlash.getProduit();
        
        VenteFlashDto.ProduitVenteFlashDto dto = new VenteFlashDto.ProduitVenteFlashDto();
        dto.setProduitId(produit.getId());
        dto.setNomProduit(produit.getNom());
        dto.setImageUrl(produit.getPhotos() != null && !produit.getPhotos().isEmpty() ? produit.getPhotos().get(0) : null);
        dto.setPrixOriginal(produit.getPrix());
        dto.setPrixPromotionnel(produitVenteFlash.getPrixPromotionnel());
        dto.setPourcentageReduction(produitVenteFlash.getPourcentageReduction());
        dto.setQuantiteStock(produitVenteFlash.getQuantiteStock());
        // TODO: Calculer quantiteVendue à partir des commandes
        dto.setQuantiteVendue(0);
        
        return dto;
    }
}
