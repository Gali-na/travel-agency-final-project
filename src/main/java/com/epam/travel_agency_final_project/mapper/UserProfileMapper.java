package com.epam.travel_agency_final_project.mapper;

import com.epam.travel_agency_final_project.dto.UserProfileDTO;
import com.epam.travel_agency_final_project.entity.User;

public interface UserProfileMapper {
    UserProfileDTO toDTO(User user);
}
