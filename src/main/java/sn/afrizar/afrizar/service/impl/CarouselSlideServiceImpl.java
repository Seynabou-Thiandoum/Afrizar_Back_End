package sn.afrizar.afrizar.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sn.afrizar.afrizar.dto.CarouselSlideDto;
import sn.afrizar.afrizar.model.CarouselSlide;
import sn.afrizar.afrizar.repository.CarouselSlideRepository;
import sn.afrizar.afrizar.service.CarouselSlideService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarouselSlideServiceImpl implements CarouselSlideService {
    
    private final CarouselSlideRepository carouselSlideRepository;
    
    @Override
    public List<CarouselSlideDto> obtenirSlidesActifs() {
        List<CarouselSlide> slides = carouselSlideRepository.findByActifTrueOrderByOrdreAffichageAsc();
        
        return slides.stream()
                .map(this::convertirEnDto)
                .collect(Collectors.toList());
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
        return dto;
    }
}
