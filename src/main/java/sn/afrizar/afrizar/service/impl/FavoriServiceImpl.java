package sn.afrizar.afrizar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.afrizar.afrizar.dto.FavoriDto;
import sn.afrizar.afrizar.model.Favori;
import sn.afrizar.afrizar.model.Produit;
import sn.afrizar.afrizar.model.Utilisateur;
import sn.afrizar.afrizar.repository.FavoriRepository;
import sn.afrizar.afrizar.repository.ProduitRepository;
import sn.afrizar.afrizar.repository.UtilisateurRepository;
import sn.afrizar.afrizar.service.FavoriService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FavoriServiceImpl implements FavoriService {

    private final FavoriRepository favoriRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ProduitRepository produitRepository;

    @Override
    public FavoriDto ajouterFavori(Long clientId, Long produitId) {
        log.info("Ajout du produit {} aux favoris du client {}", produitId, clientId);

        Utilisateur client = utilisateurRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        // Vérifier si déjà en favori
        if (favoriRepository.existsByClientAndProduit(client, produit)) {
            throw new RuntimeException("Ce produit est déjà dans vos favoris");
        }

        Favori favori = new Favori();
        favori.setClient(client);
        favori.setProduit(produit);

        Favori savedFavori = favoriRepository.save(favori);
        log.info("✅ Favori ajouté avec succès - ID: {}", savedFavori.getId());

        return convertToDto(savedFavori);
    }

    @Override
    public void retirerFavori(Long clientId, Long favoriId) {
        log.info("Retrait du favori {} pour le client {}", favoriId, clientId);

        Favori favori = favoriRepository.findById(favoriId)
                .orElseThrow(() -> new RuntimeException("Favori non trouvé"));

        // Vérifier que le favori appartient bien au client
        if (!favori.getClient().getId().equals(clientId)) {
            throw new RuntimeException("Ce favori ne vous appartient pas");
        }

        favoriRepository.delete(favori);
        log.info("✅ Favori retiré avec succès");
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriDto> getMesFavoris(Long clientId) {
        log.info("Récupération des favoris du client {}", clientId);

        Utilisateur client = utilisateurRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));

        List<Favori> favoris = favoriRepository.findByClientOrderByDateAjoutDesc(client);
        log.info("✅ {} favoris trouvés", favoris.size());

        return favoris.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estFavori(Long clientId, Long produitId) {
        Utilisateur client = utilisateurRepository.findById(clientId)
                .orElse(null);
        Produit produit = produitRepository.findById(produitId)
                .orElse(null);

        if (client == null || produit == null) {
            return false;
        }

        return favoriRepository.existsByClientAndProduit(client, produit);
    }

    @Override
    @Transactional(readOnly = true)
    public long compterFavoris(Long clientId) {
        Utilisateur client = utilisateurRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));

        return favoriRepository.countByClient(client);
    }

    private FavoriDto convertToDto(Favori favori) {
        FavoriDto dto = new FavoriDto();
        dto.setId(favori.getId());
        dto.setClientId(favori.getClient().getId());
        dto.setProduitId(favori.getProduit().getId());
        dto.setProduitNom(favori.getProduit().getNom());
        dto.setProduitPrix(favori.getProduit().getPrix() != null ? favori.getProduit().getPrix().doubleValue() : null);
        dto.setDateAjout(favori.getDateAjout());

        // Image du produit
        if (favori.getProduit().getPhotos() != null && !favori.getProduit().getPhotos().isEmpty()) {
            dto.setProduitImageUrl(favori.getProduit().getPhotos().get(0));
        }

        // Nom du vendeur
        if (favori.getProduit().getVendeur() != null) {
            dto.setVendeurNom(favori.getProduit().getVendeur().getNomBoutique());
        }

        return dto;
    }
}




