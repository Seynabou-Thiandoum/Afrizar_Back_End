package sn.afrizar.afrizar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.afrizar.afrizar.dto.CarouselSlideDto;
import sn.afrizar.afrizar.service.CarouselSlideService;

import java.util.List;

@RestController
@RequestMapping("/api/public/carousel")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PublicCarouselController {
    
    private final CarouselSlideService carouselSlideService;
    
    @GetMapping("/slides")
    public ResponseEntity<List<CarouselSlideDto>> obtenirSlidesActifs() {
        List<CarouselSlideDto> slides = carouselSlideService.obtenirSlidesActifs();
        return ResponseEntity.ok(slides);
    }
}
