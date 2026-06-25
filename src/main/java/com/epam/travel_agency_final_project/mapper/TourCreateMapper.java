package com.epam.travel_agency_final_project.mapper;

import com.epam.travel_agency_final_project.dto.TourCreationDTO;
import com.epam.travel_agency_final_project.dto.TourDTO;
import com.epam.travel_agency_final_project.dto.TourTranslationDTO;
import com.epam.travel_agency_final_project.entity.City;
import com.epam.travel_agency_final_project.entity.Tour;
import com.epam.travel_agency_final_project.entity.TourTranslation;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TourCreateMapper {
    public TourDTO toTourDTO(TourCreationDTO dto) {
        return TourDTO.builder()
                .price(dto.getPrice())
                .cityId(dto.getCityId())
                .arrivalDate(dto.getArrivalDate())
                .evictionDate(dto.getEvictionDate())
                .isHot(dto.getIsHot())
                .imagePath(dto.getImagePath())
                .build();
    }

    private void fillCommonFields(TourTranslationDTO target, TourCreationDTO source) {
        target.setTourType(source.getTourType());
        target.setTransferType(source.getTransferType());
        target.setHotelType(source.getHotelType());
    }

    public TourTranslationDTO toUkTranslation(TourCreationDTO dto) {
        TourTranslationDTO uk = new TourTranslationDTO();
        uk.setLang("uk");
        uk.setTitle(dto.getTitleUa());
        uk.setDescription(dto.getDescriptionUa());
        fillCommonFields(uk, dto);
        return uk;
    }

    public TourTranslationDTO toEnTranslation(TourCreationDTO dto) {
        TourTranslationDTO en = new TourTranslationDTO();
        en.setLang("en");
        en.setTitle(dto.getTitleEn());
        en.setDescription(dto.getDescriptionEn());
        fillCommonFields(en, dto);
        return en;
    }

    public Tour toEntity(TourDTO dto, City city) {
        Tour tour = new Tour();
        tour.setPrice(dto.getPrice());
        tour.setArrivalDate(dto.getArrivalDate());
        tour.setEvictionDate(dto.getEvictionDate());
        tour.setHot(dto.getIsHot());
        tour.setImagePath(dto.getImagePath());
        tour.setCity(city);
        return tour;
    }

    public TourTranslation toTranslationEntity(TourTranslationDTO dto, Tour tour) {
        TourTranslation.TranslationId id = new TourTranslation.TranslationId();
        id.setToursId(tour.getId());
        id.setLang(dto.getLang());
        TourTranslation translation = new TourTranslation();
        translation.setId(id);
        translation.setTour(tour);
        translation.setTitle(dto.getTitle());
        translation.setDescription(dto.getDescription());
        translation.setTourType(dto.getTourType());
        translation.setTransferType(dto.getTransferType());
        translation.setHotelType(dto.getHotelType());
        return translation;
    }
}