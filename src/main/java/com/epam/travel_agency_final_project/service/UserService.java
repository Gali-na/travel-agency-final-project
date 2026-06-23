package com.epam.travel_agency_final_project.service;

import com.epam.travel_agency_final_project.dto.TourFullDTO;
import com.epam.travel_agency_final_project.dto.UserProfileDTO;
import com.epam.travel_agency_final_project.dto.UserRegistrationDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface UserService {
    public void registerUser(UserRegistrationDTO dto, String lang);

    public boolean authenticate(String email, String rawPassword);

    //User findByEmail(String email);
    void blockUser(String email);

    boolean isBlockUser(UUID id);

    boolean isExistUser(String email);

    UserSecurityDTO findByEmail(String email);

    UserSecurityDTO findById(UUID id);

    UUID registerNewUser(UserRegistrationDTO dto);

    UserProfileDTO getProfileData(UUID userId, String lang);

    BigDecimal increaseBalance(UUID userId, BigDecimal amount);

    public void finalizePurchase(UserSecurityDTO userSecurityDTO, TourFullDTO tourDTO);

    Page<UserProfileDTO> findByEmailExact(String email, Pageable pageable);

    Page<UserProfileDTO> findAll(Pageable pageable);

    public void lockUser(UUID id);
}