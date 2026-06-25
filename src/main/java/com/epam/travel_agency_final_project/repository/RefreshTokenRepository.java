package com.epam.travel_agency_final_project.repository;

import com.epam.travel_agency_final_project.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser_Id(UUID userId);
    void deleteByUser_Id(UUID userId);
    Optional<RefreshToken> findByUserId(UUID userId);
}