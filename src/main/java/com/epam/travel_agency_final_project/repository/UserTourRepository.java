package com.epam.travel_agency_final_project.repository;

import com.epam.travel_agency_final_project.entity.UserTour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserTourRepository extends JpaRepository<UserTour, UUID> {
}
