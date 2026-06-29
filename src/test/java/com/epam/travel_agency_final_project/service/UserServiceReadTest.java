package com.epam.travel_agency_final_project.service;

import com.epam.travel_agency_final_project.dto.UserProfileDTO;
import com.epam.travel_agency_final_project.dto.UserRegistrationDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.entity.*;
import com.epam.travel_agency_final_project.exeption.UserAlreadyExistsException;
import com.epam.travel_agency_final_project.mapper.TourMapper;
import com.epam.travel_agency_final_project.mapper.UserSecurityMapper;
import com.epam.travel_agency_final_project.repository.UserRepository;
import com.epam.travel_agency_final_project.repository.UserTourRepository;
import com.epam.travel_agency_final_project.repository.UserTranslationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceReadTest {
    @Mock
    private TourMapper tourMapper;
    @Mock
    private UserTranslationRepository userTranslationRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserTourRepository userTourRepository;
    @Mock
    private UserSecurityMapper userSecurityMapper;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    @Test
    @ExtendWith(MockitoExtension.class)
    void blockUser_ShouldUpdateUserToLocked_WhenUserExists() {
        String email = "test@example.com";
        User user = new User();
        user.setLocked(false);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        userService.blockUser(email);
        assertTrue(user.isLocked());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void blockUser_ShouldNotSave_WhenUserDoesNotExist() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        userService.blockUser(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findByEmail_ShouldReturnSecurityDto_WhenUserExists() {
        String email = "test@example.com";
        User user = new User();
        UserSecurityDTO expectedDto = new UserSecurityDTO();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userSecurityMapper.toSecurityDto(user)).thenReturn(expectedDto);

        UserSecurityDTO result = userService.findByEmail(email);

        assertEquals(expectedDto, result);
        verify(userRepository).findByEmail(email);
        verify(userSecurityMapper).toSecurityDto(user);
    }

    @Test
    void findByEmail_ShouldReturnNull_WhenUserDoesNotExist() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userSecurityMapper.toSecurityDto(null)).thenReturn(null);

        UserSecurityDTO result = userService.findByEmail(email);

        assertNull(result);
        verify(userRepository).findByEmail(email);
        verify(userSecurityMapper).toSecurityDto(null);
    }
    @Test
    void registerUser_ShouldSaveUser_WhenEmailIsUnique() {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("password");
        dto.setFirstName("John");
        dto.setLastName("Doe");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded");

        userService.registerUser(dto, "uk");

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password");
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailAlreadyExists() {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setEmail("existing@example.com");
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(new User()));
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUser(dto, "uk");
        });
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void getProfileData_ShouldReturnNull_WhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        UserProfileDTO result = userService.getProfileData(userId, "en");
        assertNull(result);
    }

    @Test
    void getProfileData_ShouldReturnDto_WhenUserExistsAndTranslationFound() {
        UUID userId = UUID.randomUUID();
        User user = createMockUser(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserProfileDTO result = userService.getProfileData(userId, "en");
        assertNotNull(result);
        assertEquals("John", result.getFirstName());

        assertNotNull(result.getUserTours());
        assertEquals(1, result.getUserTours().size());
    }
    @Test
    void getProfileData_ShouldReturnEmptyFields_WhenTranslationNotFound() {
        UUID userId = UUID.randomUUID();
        User user = createMockUser(userId);
        user.setTranslations(Collections.emptyList());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user), Optional.of(user));
        UserProfileDTO result = userService.getProfileData(userId, "fr");
        assertNull(result.getFirstName());
        assertNull(result.getLastName());
    }
    private User createMockUser(UUID userId) {
        User user = new User();
        user.setId(userId);
        user.setEmail("test@test.com");
        UserTranslation.UserTranslationId translationId = new UserTranslation.UserTranslationId(userId, "en");
        UserTranslation trans = new UserTranslation();
        trans.setId(translationId);
        trans.setFirstName("John");
        trans.setLastName("Doe");
        trans.setUser(user);
        user.setTranslations(List.of(trans));
        UserTour tour = new UserTour();
        user.setUserTours(List.of(tour));
        return user;
    }
}
