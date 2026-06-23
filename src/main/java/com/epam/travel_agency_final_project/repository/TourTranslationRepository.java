package com.epam.travel_agency_final_project.repository;

import com.epam.travel_agency_final_project.entity.TourTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TourTranslationRepository extends JpaRepository<TourTranslation, UUID> {
}
