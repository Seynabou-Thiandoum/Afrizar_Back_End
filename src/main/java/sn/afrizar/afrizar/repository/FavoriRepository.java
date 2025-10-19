package sn.afrizar.afrizar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.afrizar.afrizar.model.Favori;
import sn.afrizar.afrizar.model.Utilisateur;
import sn.afrizar.afrizar.model.Produit;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriRepository extends JpaRepository<Favori, Long> {
    List<Favori> findByClientOrderByDateAjoutDesc(Utilisateur client);
    Optional<Favori> findByClientAndProduit(Utilisateur client, Produit produit);
    boolean existsByClientAndProduit(Utilisateur client, Produit produit);
    long countByClient(Utilisateur client);
}

