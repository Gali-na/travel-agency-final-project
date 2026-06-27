package com.epam.travel_agency_final_project.mapper;

import com.epam.travel_agency_final_project.entity.RefreshToken;
import com.epam.travel_agency_final_project.entity.User;
import com.epam.travel_agency_final_project.dto.RefreshTokenDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenMapper {
    private final ModelMapper modelMapper;
    public RefreshTokenDTO toDto(RefreshToken refreshToken) {
        if (refreshToken == null) return null;
        RefreshTokenDTO dto = modelMapper.map(refreshToken, RefreshTokenDTO.class);
        if (refreshToken.getUser() != null) {
            dto.setUserId(refreshToken.getUser().getId());
        }
        return dto;
    }
    public RefreshToken toEntity(RefreshTokenDTO dto) {
        if (dto == null) return null;
        RefreshToken entity = modelMapper.map(dto, RefreshToken.class);
        if (dto.getUserId() != null) {
            User user = new User();
            user.setId(dto.getUserId());
            entity.setUser(user);
        }
        return entity;
    }
}
