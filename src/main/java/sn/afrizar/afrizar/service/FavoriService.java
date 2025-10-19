package sn.afrizar.afrizar.service;

import sn.afrizar.afrizar.dto.FavoriDto;

import java.util.List;

public interface FavoriService {
    FavoriDto ajouterFavori(Long clientId, Long produitId);
    void retirerFavori(Long clientId, Long favoriId);
    List<FavoriDto> getMesFavoris(Long clientId);
    boolean estFavori(Long clientId, Long produitId);
    long compterFavoris(Long clientId);
}

