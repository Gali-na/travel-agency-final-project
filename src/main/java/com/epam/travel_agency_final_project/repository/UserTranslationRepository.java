package com.epam.travel_agency_final_project.repository;

import com.epam.travel_agency_final_project.entity.UserTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserTranslationRepository extends JpaRepository<UserTranslation, UUID> {
    // Якщо у вас складний Primary Key (user_id + lang),
    // можливо, доведеться вказати його клас окремо, але для початку спробуйте так.
}