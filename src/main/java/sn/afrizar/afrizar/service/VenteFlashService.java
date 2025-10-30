package sn.afrizar.afrizar.service;

import sn.afrizar.afrizar.dto.VenteFlashDto;

import java.util.List;
import java.util.Optional;

public interface VenteFlashService {
    
    Optional<VenteFlashDto> obtenirVenteFlashActive();
    
    List<VenteFlashDto> obtenirToutesVentesFlashActives();
    
    VenteFlashDto obtenirVenteFlashAvecProduits(Long id);
}

