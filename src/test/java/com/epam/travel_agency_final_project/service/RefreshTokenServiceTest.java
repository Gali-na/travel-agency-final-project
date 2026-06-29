package com.epam.travel_agency_final_project.service;
import com.epam.travel_agency_final_project.dto.RefreshTokenDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.entity.RefreshToken;
import com.epam.travel_agency_final_project.entity.User;
import com.epam.travel_agency_final_project.mapper.UserSecurityMapper;
import com.epam.travel_agency_final_project.repository.RefreshTokenRepository;
import com.epam.travel_agency_final_project.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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
    @Spy
    @InjectMocks
    private RefreshTokenService refreshTokenService;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    private UserSecurityMapper authService;
    @Mock
    UserAuthenticationService userAuthenticationService;
    private void mockUserRepositoryFindById(User user) {
        lenient().when(userRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(user));
    }

    @Test
    void deleteRefreshToken_ShouldReturnTrue_WhenTokenExists() {
        String token = "valid-token";
        RefreshToken refreshToken = new RefreshToken();
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        boolean result = refreshTokenService.deleteRefreshToken(token);

        assertTrue(result);
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void deleteRefreshToken_ShouldReturnFalse_WhenTokenDoesNotExist() {
        String token = "invalid-token";
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        boolean result = refreshTokenService.deleteRefreshToken(token);

        assertFalse(result);
        verify(refreshTokenRepository, never()).delete(any());
    }
    @Test
    void rotateRefreshToken_ShouldReturnNull_WhenTokenIsNull() {
        String result = refreshTokenService.rotateRefreshToken(null);
        assertNull(result, "The result should be null if the input token is null");
    }
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
    void deleteRefreshToken_ShouldReturnFalse_WhenTokenNotFound() {
        String token = "non-existent-token";

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
    void rotateRefreshToken_ShouldReturnNullWhenTokenNotFound() {
        when(refreshTokenRepository.findByToken("invalid")).thenReturn(Optional.empty());

        String result = refreshTokenService.rotateRefreshToken("invalid");

        assertNull(result);
    }

    @Test
    void rotateRefreshToken_ShouldReturnNullWhenTokenExpired() {
        RefreshToken expiredToken = new RefreshToken();
        expiredToken.setExpiryDate(LocalDateTime.now().minusDays(1));
        when(refreshTokenRepository.findByToken("expired")).thenReturn(Optional.of(expiredToken));

        String result = refreshTokenService.rotateRefreshToken("expired");

        assertNull(result);
    }

    @Test
    void rotateRefreshToken_ShouldDeleteTokenAndReturnNullWhenUserBlocked() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(1));

        when(refreshTokenRepository.findByToken("token")).thenReturn(Optional.of(refreshToken));
        when(userService.isBlockUser(userId)).thenReturn(true);

        String result = refreshTokenService.rotateRefreshToken("token");

        assertNull(result);
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void rotateRefreshToken_ShouldReturnNewTokenWhenValid() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        UserSecurityDTO userDto = new UserSecurityDTO();
        userDto.setId(userId);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(1));

        when(refreshTokenRepository.findByToken("valid")).thenReturn(Optional.of(refreshToken));
        when(userService.isBlockUser(userId)).thenReturn(false);
        when(userSecurityMapper.toSecurityDto(user)).thenReturn(userDto);
        String result =refreshTokenService.rotateRefreshToken("valid");
        assertNotNull(result);
        verify(refreshTokenRepository).delete(refreshToken);
        verify(userSecurityMapper).toSecurityDto(user);
    }
    @Test
    void rotateRefreshToken_FullScenario() {
        String oldToken = "test-token";
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(1));

        UserSecurityDTO securityDTO = new UserSecurityDTO();
        securityDTO.setId(userId);
        lenient().when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.of(refreshToken));
        lenient().when(userService.isBlockUser(any(UUID.class))).thenReturn(false);
        lenient().when(userSecurityMapper.toSecurityDto(any(User.class))).thenReturn(securityDTO);

        lenient().when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        lenient().when(refreshTokenRepository.findByUserId(any(UUID.class))).thenReturn(Optional.empty());
        String result = refreshTokenService.rotateRefreshToken(oldToken);
        assertNotNull(result);
        verify(refreshTokenRepository).delete(refreshToken);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }
    @Test
    void rotateRefreshToken_ShouldReturnNewToken_WhenTokenIsValid() {
        String oldToken = "old-valid-token";
        String newTokenValue = "new-uuid-value";
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(1));

        UserSecurityDTO securityDTO = new UserSecurityDTO();

        when(refreshTokenRepository.findByToken(oldToken)).thenReturn(Optional.of(refreshToken));
        when(userService.isBlockUser(userId)).thenReturn(false);
        when(userSecurityMapper.toSecurityDto(user)).thenReturn(securityDTO);
        doReturn(true).when(refreshTokenService).createRefreshToken(any(UserSecurityDTO.class), anyString());
        String result = refreshTokenService.rotateRefreshToken(oldToken);
        assertNotNull(result);
        verify(refreshTokenRepository).delete(refreshToken);
        verify(refreshTokenService).createRefreshToken(eq(securityDTO), anyString());
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

    @Test
    void createRefreshToken_ShouldUpdateExistingToken_WhenTokenExists() {
        UserSecurityDTO userDto = new UserSecurityDTO();
        userDto.setId(UUID.randomUUID());
        String newToken = "new-token";
        RefreshToken existingToken = new RefreshToken();

        when(refreshTokenRepository.findByUserId(userDto.getId())).thenReturn(Optional.of(existingToken));

        boolean result = refreshTokenService.createRefreshToken(userDto, newToken);

        assertTrue(result);
        assertEquals(newToken, existingToken.getToken());
        verify(refreshTokenRepository).save(existingToken);
    }

    @Test
    void createRefreshToken_ShouldCreateNewToken_WhenTokenDoesNotExistAndUserExists() {
        UserSecurityDTO userDto = new UserSecurityDTO();
        userDto.setId(UUID.randomUUID());
        User user = new User();
        user.setId(userDto.getId());
        String newToken = "new-token";

        when(refreshTokenRepository.findByUserId(userDto.getId())).thenReturn(Optional.empty());
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));

        boolean result = refreshTokenService.createRefreshToken(userDto, newToken);

        assertTrue(result);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void createRefreshToken_ShouldReturnFalse_WhenUserNotFound() {
        UserSecurityDTO userDto = new UserSecurityDTO();
        userDto.setId(UUID.randomUUID());

        when(refreshTokenRepository.findByUserId(userDto.getId())).thenReturn(Optional.empty());
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.empty());

        boolean result = refreshTokenService.createRefreshToken(userDto, "token");

        assertFalse(result);
        verify(refreshTokenRepository, never()).save(any());
    }
    @Test
    void createRefreshToken_ShouldCreateNewToken_WhenExistingTokenNotFoundButUserExists() {
        UserSecurityDTO userDto = new UserSecurityDTO();
        userDto.setId(UUID.randomUUID());

        User user = new User();
        user.setId(userDto.getId());

        String token = "new-token";
        when(refreshTokenRepository.findByUserId(userDto.getId())).thenReturn(Optional.empty());
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));
        boolean result = refreshTokenService.createRefreshToken(userDto, token);

        assertTrue(result);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }
}

