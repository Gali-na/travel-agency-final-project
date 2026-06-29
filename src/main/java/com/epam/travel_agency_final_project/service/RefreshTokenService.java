package com.epam.travel_agency_final_project.service;

import com.epam.travel_agency_final_project.dto.RefreshTokenDTO;
import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.entity.RefreshToken;
import com.epam.travel_agency_final_project.entity.User;
import com.epam.travel_agency_final_project.mapper.UserSecurityMapper;
import com.epam.travel_agency_final_project.repository.RefreshTokenRepository;
import com.epam.travel_agency_final_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final UserSecurityMapper userSecurityMapper;
    private final UserRepository userRepository;
    @Transactional
    public boolean deleteRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token).map(t -> {
            refreshTokenRepository.delete(t);
            return true;
        }).orElse(false);
    }
    @Transactional
    public String rotateRefreshToken(String oldToken) {
        if (oldToken==null){
            return null;
        }
        RefreshToken refreshToken = refreshTokenRepository.findByToken(oldToken)
                .orElse(null);

        if (refreshToken == null || refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return null;
        }

        if (userService.isBlockUser(refreshToken.getUser().getId())) {
            refreshTokenRepository.delete(refreshToken);
            return null;
        }
        refreshTokenRepository.delete(refreshToken);
        String newTokenValue = UUID.randomUUID().toString();
        createRefreshToken(userSecurityMapper.toSecurityDto(refreshToken.getUser()), newTokenValue);
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
    public RefreshTokenDTO getRefreshTokenByUserId(UUID userId) {
        return refreshTokenRepository.findByUserId(userId)
                .map(this::mapToDto)
                .orElse(null);
    }

    @Transactional
    public boolean createRefreshToken(UserSecurityDTO userDto, String token) {
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserId(userDto.getId());
        Optional<User> userOptional;
        if (existingToken.isPresent()) {
            RefreshToken tokenEntity = existingToken.get();
            tokenEntity.setToken(token);
            tokenEntity.setExpiryDate(LocalDateTime.now().plusDays(30));
            refreshTokenRepository.save(tokenEntity);
            return true;
        } else if((userOptional = userRepository.findById(userDto.getId())).isPresent()){
            RefreshToken newToken = new RefreshToken();
            newToken.setUser(userOptional.get());
            newToken.setToken(token);
            newToken.setExpiryDate(LocalDateTime.now().plusDays(30));
            refreshTokenRepository.save(newToken);
            return true;
        }
        return false;
    }
}
