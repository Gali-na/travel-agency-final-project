package com.epam.travel_agency_final_project.service;

import com.epam.travel_agency_final_project.dto.TourCreationDTO;
import com.epam.travel_agency_final_project.dto.TourFullDTO;
import com.epam.travel_agency_final_project.model.TourFilter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TourService {
    public Page<TourFullDTO> getAllToursByLanguage(String lang, int page, int size);
    Page<TourFullDTO> getTours(String lang, TourFilter filter, int page, int size);
    List<TourFullDTO> getToursForCart(Set<UUID> ids, String lang);
    TourFullDTO findById(UUID id, String lang);
    void createFullTour(TourCreationDTO dto);
}

