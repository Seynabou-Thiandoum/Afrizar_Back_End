package sn.afrizar.afrizar.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.afrizar.afrizar.dto.CreateProduitDto;
import sn.afrizar.afrizar.dto.ProduitDto;
import sn.afrizar.afrizar.model.Produit;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProduitService {
    
    ProduitDto creerProduit(CreateProduitDto createProduitDto);
    
    Optional<ProduitDto> obtenirProduitParId(Long id);
    
    List<ProduitDto> obtenirTousLesProduitsActifs();
    
    Page<ProduitDto> obtenirProduitsAvecPagination(Pageable pageable);
    
    ProduitDto mettreAJourProduit(Long id, CreateProduitDto createProduitDto);
    
    void supprimerProduit(Long id);
    
    void archiverProduit(Long id);
    
    void activerProduit(Long id);
    
    List<ProduitDto> obtenirProduitsParVendeur(Long vendeurId);
    
    List<ProduitDto> obtenirProduitsParCategorie(Long categorieId);
    
    List<ProduitDto> rechercherProduitsParNom(String nom);
    
    Page<ProduitDto> rechercherProduitsAvecFiltres(
        String nom, 
        Long categorieId, 
        Long vendeurId, 
        BigDecimal prixMin, 
        BigDecimal prixMax, 
        Produit.Qualite qualite, 
        Pageable pageable
    );
    
    List<ProduitDto> obtenirProduitsEnStock();
    
    List<ProduitDto> obtenirProduitsSurCommande();
    
    Page<ProduitDto> obtenirProduitsMieuxNotes(Pageable pageable);
    
    Page<ProduitDto> obtenirProduitsPlusVus(Pageable pageable);
    
    List<ProduitDto> obtenirProduitsParTaille(Produit.Taille taille);
    
    ProduitDto incrementerVues(Long produitId);
    
    ProduitDto ajouterEvaluation(Long produitId, BigDecimal note);
    
    boolean verifierDisponibiliteStock(Long produitId, Integer quantite);
    
    ProduitDto mettreAJourStock(Long produitId, Integer nouvelleQuantite);
    
    ProduitDto reduireStock(Long produitId, Integer quantite);
    
    Long getNombreProduitsParVendeur(Long vendeurId);
    
    BigDecimal getMoyennePrix();
    
    // Méthodes d'administration
    Page<ProduitDto> obtenirProduitsEnAttente(Pageable pageable);
    
    Page<ProduitDto> obtenirTousLesProduitsAdmin(Pageable pageable);
    
    Page<ProduitDto> obtenirProduitsParStatut(Produit.StatutProduit statut, Pageable pageable);
    
    ProduitDto validerProduit(Long produitId);
    
    void rejeterProduit(Long produitId, String motif);
    
    long compterProduits();
    
    long compterProduitsActifs();
    
    long compterProduitsEnAttente();
}

