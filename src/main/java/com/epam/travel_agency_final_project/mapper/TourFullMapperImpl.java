package com.epam.travel_agency_final_project.mapper;

import com.epam.travel_agency_final_project.dto.TourFullDTO;
import com.epam.travel_agency_final_project.entity.City;
import com.epam.travel_agency_final_project.entity.Tour;
import com.epam.travel_agency_final_project.entity.TourTranslation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@RequiredArgsConstructor
@Component
public class TourFullMapperImpl implements TourMapper {
    private final ModelMapper modelMapper;

    @Override
    public TourFullDTO toDto(Tour tour, String lang) {
        if (tour == null) {
            return null;
        }

        // 1. Мапимо базові поля туру (id, price, arrivalDate, evictionDate, isHot, imagePath)
        TourFullDTO dto = modelMapper.map(tour, TourFullDTO.class);

        // 2. Безпечно витягуємо cityId з об'єкта City
        if (tour.getCity() != null) {
            dto.setCityId(tour.getCity().getId());

            // 3. НОВЕ: Витягуємо локалізовану назву міста
            if (tour.getCity().getTranslations() != null) {
                tour.getCity().getTranslations().stream()
                        .filter(ct -> ct.getId().getLang().equalsIgnoreCase(lang))
                        .findFirst()
                        .ifPresent(ct -> dto.setCityName(ct.getName())); // "Лондон" або "London"
            }
        }

        // 4. Накладаємо локалізований переклад самого туру (title, description, types)
        if (tour.getTranslations() != null) {
            TourTranslation translation = tour.getTranslations().stream()
                    .filter(t -> t.getId().getLang().equalsIgnoreCase(lang))
                    .findFirst()
                    .orElse(tour.getTranslations().stream().findFirst().orElse(null));

            if (translation != null) {
                modelMapper.map(translation, dto);
            }
        }

        return dto;
    }

    @Override
    public Tour toEntity(TourFullDTO tourDTO) {
        if (tourDTO == null) {
            return null;
        }

        // 1. Мапимо базові технічні поля у Tour
        Tour tour = modelMapper.map(tourDTO, Tour.class);

        // 2. НОВЕ: Сетаємо зв'язок з City, якщо в DTO прийшов cityId
        if (tourDTO.getCityId() != null) {
            City city = new City();
            city.setId(tourDTO.getCityId());
            tour.setCity(city);
        }

        // 3. Пакуємо текстові поля назад у переклад для збереження в базу
        if (tourDTO.getTitle() != null || tourDTO.getDescription() != null) {
            TourTranslation translation = modelMapper.map(tourDTO, TourTranslation.class);

            TourTranslation.TranslationId translationId = new TourTranslation.TranslationId();
            translationId.setToursId(tour.getId());
            translationId.setLang("uk"); // Можна зробити "uk" дефолтною, або "en" залежно від бізнес-логіки

            translation.setId(translationId);
            translation.setTour(tour);

            if (tour.getTranslations() == null) {
                tour.setTranslations(new ArrayList<>());
            }
            tour.getTranslations().add(translation);
        }

        return tour;
    }
}


