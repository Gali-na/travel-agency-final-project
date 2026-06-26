package com.epam.travel_agency_final_project.servise;
import com.epam.travel_agency_final_project.dto.RefreshTokenDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.entity.RefreshToken;
import com.epam.travel_agency_final_project.entity.User;
import com.epam.travel_agency_final_project.mapper.UserSecurityMapper;
import com.epam.travel_agency_final_project.repository.RefreshTokenRepository;
import com.epam.travel_agency_final_project.service.RefreshTokenService;
import com.epam.travel_agency_final_project.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserSecurityMapper userSecurityMapper;
    @InjectMocks
    private RefreshTokenService refreshTokenService;
    @Test
    void getRefreshTokenByUserId_ShouldReturnDto_WhenTokenExists() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        RefreshToken token = new RefreshToken();
        token.setId(UUID.randomUUID());
        token.setToken("token-value");
        token.setUser(user);
        when(refreshTokenRepository.findByUserId(userId)).thenReturn(Optional.of(token));
        RefreshTokenDTO result = refreshTokenService.getRefreshTokenByUserId(userId);
        assertNotNull(result);
        assertEquals(token.getToken(), result.getToken());
    }

    @Test
    void getRefreshTokenByUserId_ShouldReturnNull_WhenTokenDoesNotExist() {
        UUID userId = UUID.randomUUID();

        when(refreshTokenRepository.findByUserId(userId)).thenReturn(Optional.empty());

        RefreshTokenDTO result = refreshTokenService.getRefreshTokenByUserId(userId);

        assertNull(result);
    }
    @Test
    @DisplayName("Should update existing refresh token")
    void createRefreshToken_ShouldUpdateExistingToken_WhenTokenExists() {
        UUID userId = UUID.randomUUID();
        UserSecurityDTO userDto = new UserSecurityDTO();
        userDto.setId(userId);
        userDto.setLogin("test@example.com");
        String newToken = "new-token-value";
        RefreshToken existingToken = new RefreshToken();

        when(refreshTokenRepository.findByUserId(userId)).thenReturn(Optional.of(existingToken));

        refreshTokenService.createRefreshToken(userDto, newToken);

        verify(refreshTokenRepository, times(1)).save(existingToken);
        assert(existingToken.getToken().equals(newToken));
    }
    @Test
    @DisplayName("Should create new refresh token")
    void createRefreshToken_ShouldCreateNewToken_WhenTokenDoesNotExist() {
        UUID userId = UUID.randomUUID();
        UserSecurityDTO userDto = new UserSecurityDTO();
        userDto.setId(userId);
        userDto.setLogin("test@example.com");
        String token = "new-token-value";

        when(refreshTokenRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userSecurityMapper.toEntity(userDto)).thenReturn(new User());

        refreshTokenService.createRefreshToken(userDto, token);

        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
        verify(userSecurityMapper, times(1)).toEntity(userDto);
    }

    @Test
    void deleteRefreshToken_WhenTokenExists() {
        String token = "test-token";
        RefreshToken refreshToken = new RefreshToken();

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        boolean result = refreshTokenService.deleteRefreshToken(token);

        assertTrue(result);
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void deleteRefreshToken_WhenTokenDoesNotExist() {
        String token = "invalid-token";

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        boolean result = refreshTokenService.deleteRefreshToken(token);

        assertFalse(result);
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    void rotateRefreshToken_WhenTokenNotFound() {
        when(refreshTokenRepository.findByToken("invalid")).thenReturn(Optional.empty());

        String result = refreshTokenService.rotateRefreshToken("invalid");

        assertNull(result);
    }

    @Test
    void rotateRefreshToken_WhenUserIsBlocked() {
        String token = "token";
        User user = new User();
        user.setId(UUID.randomUUID());
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(1));

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));
        when(userService.isBlockUser(user.getId())).thenReturn(true);

        String result = refreshTokenService.rotateRefreshToken(token);

        assertNull(result);
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void rotateRefreshToken_WhenTokenIsValid() {
        String oldToken = "old";
        User user = new User();
        user.setId(UUID.randomUUID());
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(1));
        when(refreshTokenRepository.findByToken(oldToken)).thenReturn(Optional.of(refreshToken));
        when(userService.isBlockUser(user.getId())).thenReturn(false);
        when(userSecurityMapper.toSecurityDto(user)).thenReturn(new UserSecurityDTO());
        String result = refreshTokenService.rotateRefreshToken(oldToken);
        assertNotNull(result);
        verify(refreshTokenRepository).delete(refreshToken);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }
    @Test
    void getRefreshToken_WhenTokenExists() {
        String token = "valid-token";
        RefreshToken refreshToken = new RefreshToken();
        User user = new User();
        user.setId(UUID.randomUUID());
        refreshToken.setUser(user);

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        RefreshTokenDTO result = refreshTokenService.getRefreshToken(token);

        assertNotNull(result);
        verify(refreshTokenRepository).findByToken(token);
    }

    @Test
    void getRefreshToken_WhenTokenDoesNotExist() {
        String token = "invalid-token";

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        RefreshTokenDTO result = refreshTokenService.getRefreshToken(token);

        assertNull(result);
    }
}

