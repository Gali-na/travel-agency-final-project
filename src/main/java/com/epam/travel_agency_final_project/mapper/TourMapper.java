package com.epam.travel_agency_final_project.mapper;

import com.epam.travel_agency_final_project.dto.TourFullDTO;
import com.epam.travel_agency_final_project.entity.Tour;

public interface TourMapper {
    TourFullDTO toDto(Tour tour, String lang);
    Tour toEntity(TourFullDTO tourDto);
}
