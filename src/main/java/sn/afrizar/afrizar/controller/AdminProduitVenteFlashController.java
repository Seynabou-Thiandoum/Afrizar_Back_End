package sn.afrizar.afrizar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.VenteFlashDto;
import sn.afrizar.afrizar.model.Produit;
import sn.afrizar.afrizar.model.ProduitVenteFlash;
import sn.afrizar.afrizar.model.VenteFlash;
import sn.afrizar.afrizar.repository.ProduitRepository;
import sn.afrizar.afrizar.repository.ProduitVenteFlashRepository;
import sn.afrizar.afrizar.repository.VenteFlashRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/ventes-flash/{venteFlashId}/produits")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProduitVenteFlashController {
    
    private final VenteFlashRepository venteFlashRepository;
    private final ProduitRepository produitRepository;
    private final ProduitVenteFlashRepository produitVenteFlashRepository;
    
    @GetMapping
    public ResponseEntity<List<VenteFlashDto.ProduitVenteFlashDto>> obtenirProduits(@PathVariable Long venteFlashId) {
        List<ProduitVenteFlash> produits = produitVenteFlashRepository.findByVenteFlashId(venteFlashId);
        List<VenteFlashDto.ProduitVenteFlashDto> dtos = produits.stream()
                .map(this::convertirEnDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @PostMapping("/{produitId}")
    public ResponseEntity<VenteFlashDto.ProduitVenteFlashDto> ajouterProduit(
            @PathVariable Long venteFlashId,
            @PathVariable Long produitId,
            @RequestBody AjouterProduitDto dto) {
        
        VenteFlash venteFlash = venteFlashRepository.findById(venteFlashId)
                .orElseThrow(() -> new RuntimeException("Vente flash non trouvée"));
        
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        
        // Vérifier si le produit est déjà dans cette vente flash
        if (produitVenteFlashRepository.findByVenteFlashIdAndProduitId(venteFlashId, produitId).isPresent()) {
            throw new RuntimeException("Ce produit est déjà dans cette vente flash");
        }
        
        ProduitVenteFlash produitVenteFlash = new ProduitVenteFlash();
        produitVenteFlash.setVenteFlash(venteFlash);
        produitVenteFlash.setProduit(produit);
        produitVenteFlash.setPrixPromotionnel(dto.getPrixPromotionnel());
        produitVenteFlash.setPourcentageReduction(dto.getPourcentageReduction());
        produitVenteFlash.setQuantiteStock(dto.getQuantiteStock() != null ? dto.getQuantiteStock() : produit.getStock());
        produitVenteFlash.setImageUrl(dto.getImageUrl());
        
        ProduitVenteFlash saved = produitVenteFlashRepository.save(produitVenteFlash);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirEnDto(saved));
    }
    
    @PutMapping("/{produitId}")
    public ResponseEntity<VenteFlashDto.ProduitVenteFlashDto> modifierProduit(
            @PathVariable Long venteFlashId,
            @PathVariable Long produitId,
            @RequestBody AjouterProduitDto dto) {
        
        ProduitVenteFlash produitVenteFlash = produitVenteFlashRepository
                .findByVenteFlashIdAndProduitId(venteFlashId, produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé dans cette vente flash"));
        
        produitVenteFlash.setPrixPromotionnel(dto.getPrixPromotionnel());
        produitVenteFlash.setPourcentageReduction(dto.getPourcentageReduction());
        if (dto.getQuantiteStock() != null) {
            produitVenteFlash.setQuantiteStock(dto.getQuantiteStock());
        }
        if (dto.getImageUrl() != null) {
            produitVenteFlash.setImageUrl(dto.getImageUrl());
        }
        
        ProduitVenteFlash saved = produitVenteFlashRepository.save(produitVenteFlash);
        
        return ResponseEntity.ok(convertirEnDto(saved));
    }
    
    @DeleteMapping("/{produitId}")
    public ResponseEntity<Void> retirerProduit(
            @PathVariable Long venteFlashId,
            @PathVariable Long produitId) {
        
        ProduitVenteFlash produitVenteFlash = produitVenteFlashRepository
                .findByVenteFlashIdAndProduitId(venteFlashId, produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé dans cette vente flash"));
        
        produitVenteFlashRepository.delete(produitVenteFlash);
        
        return ResponseEntity.noContent().build();
    }
    
    private VenteFlashDto.ProduitVenteFlashDto convertirEnDto(ProduitVenteFlash produitVenteFlash) {
        Produit produit = produitVenteFlash.getProduit();
        
        VenteFlashDto.ProduitVenteFlashDto dto = new VenteFlashDto.ProduitVenteFlashDto();
        dto.setProduitId(produit.getId());
        dto.setNomProduit(produit.getNom());
        dto.setImageUrl(produit.getPhotos() != null && !produit.getPhotos().isEmpty() ? produit.getPhotos().get(0) : null);
        dto.setPrixOriginal(produit.getPrix());
        dto.setPrixPromotionnel(produitVenteFlash.getPrixPromotionnel());
        dto.setPourcentageReduction(produitVenteFlash.getPourcentageReduction());
        dto.setQuantiteStock(produitVenteFlash.getQuantiteStock());
        dto.setQuantiteVendue(0); // À implémenter si nécessaire
        
        return dto;
    }
    
    // Classe interne pour la requête
    public static class AjouterProduitDto {
        private BigDecimal prixPromotionnel;
        private Integer pourcentageReduction;
        private Integer quantiteStock;
        private String imageUrl;
        
        public BigDecimal getPrixPromotionnel() { return prixPromotionnel; }
        public void setPrixPromotionnel(BigDecimal prixPromotionnel) { this.prixPromotionnel = prixPromotionnel; }
        
        public Integer getPourcentageReduction() { return pourcentageReduction; }
        public void setPourcentageReduction(Integer pourcentageReduction) { this.pourcentageReduction = pourcentageReduction; }
        
        public Integer getQuantiteStock() { return quantiteStock; }
        public void setQuantiteStock(Integer quantiteStock) { this.quantiteStock = quantiteStock; }
        
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
}
