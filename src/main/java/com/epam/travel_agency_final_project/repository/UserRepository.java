package com.epam.travel_agency_final_project.repository;

import com.epam.travel_agency_final_project.entity.RefreshToken;
import com.epam.travel_agency_final_project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Modifying
    @Query("UPDATE User u SET u.balance = u.balance + :amount WHERE u.id = :userId")
    int depositBalanceById(@Param("userId") UUID userId, @Param("amount") BigDecimal amount);
    @Query("SELECT u.balance FROM User u WHERE u.id = :userId")
    BigDecimal getBalanceById(@Param("userId") UUID userId);
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Page<User> findByEmailExact(@Param("email") String email, Pageable pageable);
    @Modifying
    @Query("UPDATE User u SET u.isLocked = true WHERE u.id = :id")
    void lockUserById(@Param("id") UUID id);
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
}
