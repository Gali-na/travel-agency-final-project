package com.epam.travel_agency_final_project.service;

import com.epam.travel_agency_final_project.dto.*;
import com.epam.travel_agency_final_project.entity.User;
import com.epam.travel_agency_final_project.entity.UserTour;
import com.epam.travel_agency_final_project.entity.UserTranslation;
import com.epam.travel_agency_final_project.exeption.UserAlreadyExistsException;
import com.epam.travel_agency_final_project.mapper.TourMapper;
import com.epam.travel_agency_final_project.mapper.UserProfileMapper;
import com.epam.travel_agency_final_project.mapper.UserSecurityMapper;
import com.epam.travel_agency_final_project.model.StatusTour;
import com.epam.travel_agency_final_project.repository.UserRepository;
import com.epam.travel_agency_final_project.repository.UserTourRepository;
import com.epam.travel_agency_final_project.repository.UserTranslationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSecurityMapper userSecurityMapper;
    private final UserTranslationRepository userTranslationRepository;
    private final TourMapper tourMapper;
    private final UserTourRepository userTourRepository;
    private final UserProfileMapper userProfileMapper;

    @Transactional

    public void lockUser(UUID id) {
        userRepository.lockUserById(id);
    }


    public Page<UserProfileDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(userProfileMapper::toDTO);
    }

    public Page<UserProfileDTO> findByEmailExact(String email, Pageable pageable) {
        return userRepository.findByEmailExact(email, pageable).map(userProfileMapper::toDTO);
    }

    public boolean authenticate(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()))
                .orElse(false);
    }

    @Transactional
    public BigDecimal increaseBalance(UUID userId, BigDecimal amount) {
        int updatedRows = userRepository.depositBalanceById(userId, amount);
        return userRepository.getBalanceById(userId);
    }

    public UserSecurityDTO findById(UUID id) {
        return userSecurityMapper.toSecurityDto(userRepository.findById(id).orElse(null));
    }

    public boolean isExistUser(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean isBlockUser(UUID id) {
        return userRepository.findById(id)
                .map(User::isLocked)
                .orElse(true); // Якщо користувача немає, вважаємо заблокованим
    }

    @Transactional
    public void blockUser(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setLocked(true);
            userRepository.save(user);
        });
    }

    public UserSecurityDTO findByEmail(String email) {
        return userSecurityMapper.toSecurityDto(userRepository.findByEmail(email).orElse(null));
    }

    @Transactional
    public void finalizePurchase(UserSecurityDTO userSecurityDTO, TourFullDTO tourDTO) {
        userSecurityDTO.setBalance(userSecurityDTO.getBalance().subtract(tourDTO.getPrice()));
        userRepository.save(userSecurityMapper.toEntity(userSecurityDTO));
        UserTour userTour = new UserTour();
        userTour.setUser(userSecurityMapper.toEntity(userSecurityDTO));
        userTour.setTour(tourMapper.toEntity(tourDTO));
        userTour.setStatus(String.valueOf(StatusTour.PAID));
        userTourRepository.save(userTour);
    }

    @Transactional
    public UUID registerNewUser(UserRegistrationDTO dto) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setLocked(false);
        user.setBalance(BigDecimal.ZERO);

        User savedUser = userRepository.save(user);
        UserTranslation translation = new UserTranslation();
        UserTranslation.UserTranslationId id = new UserTranslation.UserTranslationId(user.getId(), "uk");
        translation.setId(id);
        translation.setUser(user);
        translation.setFirstName(dto.getFirstName());
        translation.setLastName(dto.getLastName());
        userTranslationRepository.save(translation);
        return savedUser.getId();
    }

    @Transactional
    public void registerUser(UserRegistrationDTO dto, String lang) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("error.user.exists");
        }
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setBalance(BigDecimal.ZERO);
        user.setLocked(false);

        UserTranslation translation = new UserTranslation();
        translation.setFirstName(dto.getFirstName());
        translation.setLastName(dto.getLastName());
        translation.setUser(user);

        UserTranslation.UserTranslationId id = new UserTranslation.UserTranslationId();
        id.setUserId(userId);
        id.setLang(lang);
        translation.setId(id);

        user.setTranslations(List.of(translation));
        userRepository.save(user);
    }

    public UserProfileDTO getProfileData(UUID userId, String lang) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Користувача з ID " + userId + " не знайдено"));
        UserTranslation trans = user.getTranslations().stream()
                .filter(t -> t.getId() != null && t.getId().getLang().equalsIgnoreCase(lang))
                .findFirst()
                .orElse(new UserTranslation());

        List<UserTourDTO> tourDTOs = user.getUserTours().stream()
                .map(ut -> new UserTourDTO(ut.getId(), ut.getStatus(), ut.getCreatedAt(), tourMapper.toDto(ut.getTour(), lang)
                ))
                .collect(Collectors.toList());

        return new UserProfileDTO(user.getId(), user.getEmail(), user.getBalance(), trans.getFirstName(), trans.getLastName(), user.isLocked(), tourDTOs
        );
    }
}

