package com.epam.travel_agency_final_project.repository;

import com.epam.travel_agency_final_project.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    // Знайти токен за його текстовим значенням
    Optional<RefreshToken> findByToken(String token);

    // Знайти токен за користувачем
    Optional<RefreshToken> findByUser_Id(UUID userId);

    // Видалити токен користувача (перед створенням нового або при логауті)
    void deleteByUser_Id(UUID userId);
    Optional<RefreshToken> findByUserId(UUID userId);
}