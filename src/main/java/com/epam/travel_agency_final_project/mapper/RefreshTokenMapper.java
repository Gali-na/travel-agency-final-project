package com.epam.travel_agency_final_project.mapper;

import com.epam.travel_agency_final_project.dto.RefreshTokenDTO;
import com.epam.travel_agency_final_project.entity.RefreshToken;

public interface RefreshTokenMapper {
    RefreshTokenDTO toDto(RefreshToken refreshToken);
    RefreshToken toEntity(RefreshTokenDTO refreshTokenDTO);
}
