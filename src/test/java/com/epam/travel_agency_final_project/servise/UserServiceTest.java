package com.epam.travel_agency_final_project.servise;

import com.epam.travel_agency_final_project.dto.UserProfileDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.entity.User;
import com.epam.travel_agency_final_project.mapper.UserProfileMapper;
import com.epam.travel_agency_final_project.mapper.UserSecurityMapper;
import com.epam.travel_agency_final_project.repository.UserRepository;
import com.epam.travel_agency_final_project.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserSecurityMapper userSecurityMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserProfileMapper userProfileMapper;
    @InjectMocks
    private UserService userService;
    @Test
    void lockUser_ShouldCallRepository() {
        UUID id = UUID.randomUUID();
        userService.lockUser(id);
        verify(userRepository, times(1)).lockUserById(id);
    }
    @Test
    void findAll_ShouldReturnPageOfUserProfiles() {
        Pageable pageable = PageRequest.of(0, 10);
        User user = new User();
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userProfileMapper.toDTO(any(User.class))).thenReturn(userProfileDTO);

        Page<UserProfileDTO> result = userService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(userProfileDTO, result.getContent().get(0));
        verify(userRepository).findAll(pageable);
    }
    @Test
    void findByEmailExact_ShouldReturnPageOfUserProfiles() {
        String email = "test@example.com";
        Pageable pageable = PageRequest.of(0, 10);
        User user = new User();
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findByEmailExact(email, pageable)).thenReturn(userPage);
        when(userProfileMapper.toDTO(any(User.class))).thenReturn(userProfileDTO);

        Page<UserProfileDTO> result = userService.findByEmailExact(email, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(userProfileDTO, result.getContent().get(0));

        verify(userRepository).findByEmailExact(email, pageable);
    }

    @Test
    void authenticate_ShouldReturnTrue_WhenCredentialsAreValid() {
        String email = "test@example.com";
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";
        User user = new User();
        user.setPasswordHash(encodedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        boolean result = userService.authenticate(email, rawPassword);
        assertTrue(result);
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    void authenticate_ShouldReturnFalse_WhenPasswordDoesNotMatch() {
        String email = "test@example.com";
        String rawPassword = "wrongPassword";
        String encodedPassword = "encodedPassword";
        User user = new User();
        user.setPasswordHash(encodedPassword);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        boolean result = userService.authenticate(email, rawPassword);

        assertFalse(result);
    }

    @Test
    void authenticate_ShouldReturnFalse_WhenUserDoesNotExist() {
        String email = "nonexistent@example.com";
        String rawPassword = "anyPassword";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        boolean result = userService.authenticate(email, rawPassword);
        assertFalse(result);
    }

    @Test
    void increaseBalance_ShouldUpdateAndReturnNewBalance() {
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal expectedBalance = new BigDecimal("250.00");
        when(userRepository.depositBalanceById(userId, amount)).thenReturn(1);
        when(userRepository.getBalanceById(userId)).thenReturn(expectedBalance);
        BigDecimal result = userService.increaseBalance(userId, amount);
        assertEquals(expectedBalance, result);
        verify(userRepository).depositBalanceById(userId, amount);
        verify(userRepository).getBalanceById(userId);
    }

    @Test
    void findById_ShouldReturnSecurityDto_WhenUserExists() {
        UUID id = UUID.randomUUID();
        User user = new User();
        UserSecurityDTO expectedDto = new UserSecurityDTO();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userSecurityMapper.toSecurityDto(user)).thenReturn(expectedDto);
        UserSecurityDTO result = userService.findById(id);
        assertEquals(expectedDto, result);
        verify(userRepository).findById(id);
        verify(userSecurityMapper).toSecurityDto(user);
    }

    @Test
    void isExistUser_ShouldReturnTrue_WhenUserExists() {
        String email = "test@example.com";
        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        assertTrue(userService.isExistUser(email));
    }

    @Test
    void isExistUser_ShouldReturnFalse_WhenUserDoesNotExist() {
        String email = "unknown@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertFalse(userService.isExistUser(email));
    }

    @Test
    void isExistUser_ShouldReturnFalse_WhenEmailIsEmpty() {
        String email = "";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertFalse(userService.isExistUser(email));
    }

    @Test
    void isExistUser_ShouldReturnFalse_WhenEmailIsNull() {
        String email = null;
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());
        assertFalse(userService.isExistUser(email));
    }
    @Test
    void findById_ShouldReturnNull_WhenUserDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        when(userSecurityMapper.toSecurityDto(null)).thenReturn(null);
        UserSecurityDTO result = userService.findById(id);
        assertNull(result);
        verify(userRepository).findById(id);
        verify(userSecurityMapper).toSecurityDto(null);
    }
    @Test
    void isBlockUser_ShouldReturnTrue_WhenUserIsLocked() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setLocked(true);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        boolean result = userService.isBlockUser(id);
        assertTrue(result);
        verify(userRepository).findById(id);
    }

    @Test
    void isBlockUser_ShouldReturnFalse_WhenUserIsNotLocked() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setLocked(false);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        boolean result = userService.isBlockUser(id);
        assertFalse(result);
        verify(userRepository).findById(id);
    }

    @Test
    void isBlockUser_ShouldReturnTrue_WhenUserDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        boolean result = userService.isBlockUser(id);
        assertTrue(result);
        verify(userRepository).findById(id);
    }
}
