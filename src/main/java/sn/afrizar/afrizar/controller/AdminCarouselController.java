package sn.afrizar.afrizar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.CarouselSlideDto;
import sn.afrizar.afrizar.model.CarouselSlide;
import sn.afrizar.afrizar.repository.CarouselSlideRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/carousel")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCarouselController {
    
    private final CarouselSlideRepository carouselSlideRepository;
    
    @GetMapping
    public ResponseEntity<List<CarouselSlideDto>> obtenirTousLesSlides() {
        List<CarouselSlide> slides = carouselSlideRepository.findAllByOrderByOrdreAffichageAsc();
        List<CarouselSlideDto> dtos = slides.stream()
                .map(this::convertirEnDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CarouselSlideDto> obtenirSlide(@PathVariable Long id) {
        return carouselSlideRepository.findById(id)
                .map(slide -> ResponseEntity.ok(convertirEnDto(slide)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<CarouselSlideDto> creerSlide(@RequestBody CarouselSlideDto dto) {
        CarouselSlide slide = new CarouselSlide();
        mapperDtoVersEntity(dto, slide);
        CarouselSlide saved = carouselSlideRepository.save(slide);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirEnDto(saved));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CarouselSlideDto> modifierSlide(@PathVariable Long id, @RequestBody CarouselSlideDto dto) {
        return carouselSlideRepository.findById(id)
                .map(slide -> {
                    mapperDtoVersEntity(dto, slide);
                    CarouselSlide saved = carouselSlideRepository.save(slide);
                    return ResponseEntity.ok(convertirEnDto(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerSlide(@PathVariable Long id) {
        if (carouselSlideRepository.existsById(id)) {
            carouselSlideRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<CarouselSlideDto> basculerActif(@PathVariable Long id) {
        return carouselSlideRepository.findById(id)
                .map(slide -> {
                    slide.setActif(!slide.getActif());
                    CarouselSlide saved = carouselSlideRepository.save(slide);
                    return ResponseEntity.ok(convertirEnDto(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    private CarouselSlideDto convertirEnDto(CarouselSlide slide) {
        CarouselSlideDto dto = new CarouselSlideDto();
        dto.setId(slide.getId());
        dto.setTitre(slide.getTitre());
        dto.setSousTitre(slide.getSousTitre());
        dto.setImageUrl(slide.getImageUrl());
        dto.setBadge(slide.getBadge());
        dto.setBoutonTexte(slide.getBoutonTexte());
        dto.setBoutonLien(slide.getBoutonLien());
        dto.setOrdreAffichage(slide.getOrdreAffichage());
        dto.setActif(slide.getActif());
        return dto;
    }
    
    private void mapperDtoVersEntity(CarouselSlideDto dto, CarouselSlide slide) {
        slide.setTitre(dto.getTitre());
        slide.setSousTitre(dto.getSousTitre());
        slide.setImageUrl(dto.getImageUrl());
        slide.setBadge(dto.getBadge());
        slide.setBoutonTexte(dto.getBoutonTexte());
        slide.setBoutonLien(dto.getBoutonLien());
        slide.setOrdreAffichage(dto.getOrdreAffichage());
    }
}
