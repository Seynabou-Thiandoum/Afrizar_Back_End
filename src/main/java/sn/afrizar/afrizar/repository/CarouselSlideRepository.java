package sn.afrizar.afrizar.repository;

import sn.afrizar.afrizar.model.CarouselSlide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarouselSlideRepository extends JpaRepository<CarouselSlide, Long> {
    
    List<CarouselSlide> findByActifTrueOrderByOrdreAffichageAsc();
    
    List<CarouselSlide> findAllByOrderByOrdreAffichageAsc();
}
