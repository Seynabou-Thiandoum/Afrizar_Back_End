package sn.afrizar.afrizar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.VenteFlashDto;
import sn.afrizar.afrizar.model.VenteFlash;
import sn.afrizar.afrizar.repository.VenteFlashRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/ventes-flash")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminVentesFlashController {
    
    private final VenteFlashRepository venteFlashRepository;
    
    @GetMapping
    public ResponseEntity<List<VenteFlashDto>> obtenirToutesLesVentesFlash() {
        List<VenteFlash> ventesFlash = venteFlashRepository.findAll();
        List<VenteFlashDto> dtos = ventesFlash.stream()
                .map(this::convertirEnDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<VenteFlashDto> obtenirVenteFlash(@PathVariable Long id) {
        return venteFlashRepository.findById(id)
                .map(vente -> ResponseEntity.ok(convertirEnDto(vente)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<VenteFlashDto> creerVenteFlash(@RequestBody VenteFlashDto dto) {
        VenteFlash venteFlash = new VenteFlash();
        mapperDtoVersEntity(dto, venteFlash);
        VenteFlash saved = venteFlashRepository.save(venteFlash);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirEnDto(saved));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<VenteFlashDto> modifierVenteFlash(@PathVariable Long id, @RequestBody VenteFlashDto dto) {
        return venteFlashRepository.findById(id)
                .map(venteFlash -> {
                    mapperDtoVersEntity(dto, venteFlash);
                    VenteFlash saved = venteFlashRepository.save(venteFlash);
                    return ResponseEntity.ok(convertirEnDto(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerVenteFlash(@PathVariable Long id) {
        if (venteFlashRepository.existsById(id)) {
            venteFlashRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<VenteFlashDto> basculerActif(@PathVariable Long id) {
        return venteFlashRepository.findById(id)
                .map(venteFlash -> {
                    venteFlash.setActif(!venteFlash.getActif());
                    VenteFlash saved = venteFlashRepository.save(venteFlash);
                    return ResponseEntity.ok(convertirEnDto(saved));
                })
                .orElse(ResponseEntity.notFound().build());
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
    
    private void mapperDtoVersEntity(VenteFlashDto dto, VenteFlash venteFlash) {
        venteFlash.setNom(dto.getNom());
        venteFlash.setDescription(dto.getDescription());
        if (dto.getDateDebut() != null) {
            venteFlash.setDateDebut(dto.getDateDebut());
        }
        if (dto.getDateFin() != null) {
            venteFlash.setDateFin(dto.getDateFin());
        }
        if (dto.getActif() != null) {
            venteFlash.setActif(dto.getActif());
        }
        venteFlash.setPourcentageReductionParDefaut(dto.getPourcentageReductionParDefaut());
    }
}
