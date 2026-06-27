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
public class TourMapper  {
    private final ModelMapper modelMapper;
    public TourFullDTO toDto(Tour tour, String lang) {
        if (tour == null) {
            return null;
        }

        TourFullDTO dto = modelMapper.map(tour, TourFullDTO.class);
        if (tour.getCity() != null) {
            dto.setCityId(tour.getCity().getId());
            if (tour.getCity().getTranslations() != null) {
                tour.getCity().getTranslations().stream()
                        .filter(ct -> ct.getId().getLang().equalsIgnoreCase(lang))
                        .findFirst()
                        .ifPresent(ct -> dto.setCityName(ct.getName()));
            }
        }

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
    public Tour toEntity(TourFullDTO tourDTO) {
        if (tourDTO == null) {
            return null;
        }

        Tour tour = modelMapper.map(tourDTO, Tour.class);

        if (tourDTO.getCityId() != null) {
            City city = new City();
            city.setId(tourDTO.getCityId());
            tour.setCity(city);
        }

        if (tourDTO.getTitle() != null || tourDTO.getDescription() != null) {
            TourTranslation translation = modelMapper.map(tourDTO, TourTranslation.class);

            TourTranslation.TranslationId translationId = new TourTranslation.TranslationId();
            translationId.setToursId(tour.getId());
            translationId.setLang("uk");
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


