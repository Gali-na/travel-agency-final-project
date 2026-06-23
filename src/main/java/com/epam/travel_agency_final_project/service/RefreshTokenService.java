package com.epam.travel_agency_final_project.service;
import com.epam.travel_agency_final_project.dto.RefreshTokenDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.entity.RefreshToken;
import com.epam.travel_agency_final_project.entity.User;
import com.epam.travel_agency_final_project.mapper.UserSecurityMapper;
import com.epam.travel_agency_final_project.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final UserSecurityMapper userSecurityMapper;

    @Transactional
    public void createRefreshToken(UserSecurityDTO userDto, String token) {
        // 1. Шукаємо існуючий токен за user_id
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserId(userDto.getId());

        if (existingToken.isPresent()) {
            // 2. Якщо є — оновлюємо значення
            RefreshToken tokenEntity = existingToken.get();
            tokenEntity.setToken(token);
            tokenEntity.setExpiryDate(LocalDateTime.now().plusDays(30));
            refreshTokenRepository.save(tokenEntity);
        } else {
            // 3. Якщо немає — створюємо новий
            RefreshToken newToken = new RefreshToken();
            newToken.setUser(userSecurityMapper.toEntity(userDto));
            newToken.setToken(token);
            newToken.setExpiryDate(LocalDateTime.now().plusDays(30));
            refreshTokenRepository.save(newToken);
        }
    }
//    @Transactional
//    public RefreshTokenDTO createRefreshToken(UserSecurityDTO user, String tokenValue) {
//        RefreshToken token = RefreshToken.builder()
//                .user(userSecurityMapper.toEntity(user))
//                .token(tokenValue)
//                .expiryDate(LocalDateTime.now().plusDays(30))
//                .build();
//        RefreshToken saved = refreshTokenRepository.save(token);
//        return mapToDto(saved);
//    }

    @Transactional
    public boolean deleteRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token).map(t -> {
            refreshTokenRepository.delete(t);
            return true;
        }).orElse(false);
    }

    @Transactional
    public String rotateRefreshToken(String oldToken) {
        // 1. Знаходимо токен
        RefreshToken refreshToken = refreshTokenRepository.findByToken(oldToken)
                .orElse(null);

        // 2. Перевірка на валідність та термін дії
        if (refreshToken == null || refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            if (refreshToken != null) refreshTokenRepository.delete(refreshToken);
            return null;
        }

        // 3. Перевірка на блокування користувача
        if (userService.isBlockUser(refreshToken.getUser().getId())) {
            refreshTokenRepository.delete(refreshToken); // Видаляємо токен заблокованого юзера
            return null;
        }

        // 4. Ротація: видаляємо старий, генеруємо новий
        refreshTokenRepository.delete(refreshToken);

        String newTokenValue = UUID.randomUUID().toString();
        createRefreshToken(userSecurityMapper.toSecurityDto(refreshToken.getUser()), newTokenValue);

        // Повертаємо новий токен, щоб контролер міг відправити його в куках
        return newTokenValue;
    }

    public RefreshTokenDTO getRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(this::mapToDto)
                .orElse(null);
    }

    private RefreshTokenDTO mapToDto(RefreshToken entity) {
        return RefreshTokenDTO.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .token(entity.getToken())
                .expiryDate(entity.getExpiryDate())
                .build();
    }
}
