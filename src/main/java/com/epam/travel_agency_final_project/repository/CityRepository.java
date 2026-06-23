package com.epam.travel_agency_final_project.repository;

import com.epam.travel_agency_final_project.entity.City;
import com.epam.travel_agency_final_project.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CityRepository extends JpaRepository<City, UUID> {
}
