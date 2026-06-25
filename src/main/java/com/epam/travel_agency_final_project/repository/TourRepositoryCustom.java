package com.epam.travel_agency_final_project.repository;

import com.epam.travel_agency_final_project.dto.TourFullDTO;
import com.epam.travel_agency_final_project.model.TourFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TourRepositoryCustom {
    Page<TourFullDTO> findToursWithJdbc(String lang, TourFilter filter, Pageable pageable);
    List<TourFullDTO> findToursByIdsAndInLanguage(Set<UUID> ids, String lang);
}
