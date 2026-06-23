package com.epam.travel_agency_final_project.mapper;

import com.epam.travel_agency_final_project.dto.UserProfileDTO;
import com.epam.travel_agency_final_project.entity.User;
import com.epam.travel_agency_final_project.entity.UserTranslation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserProfileMapperImpl implements UserProfileMapper {
    private final ModelMapper modelMapper;
    public UserProfileDTO toDTO(User user) {
        UserProfileDTO dto = modelMapper.map(user, UserProfileDTO.class);

        // Маппінг для складних полів
        if (user.getTranslations() != null && !user.getTranslations().isEmpty()) {
            UserTranslation translation = user.getTranslations().get(0);
            dto.setFirstName(translation.getFirstName());
            dto.setLastName(translation.getLastName());
        }

        // Встановлюємо ID, якщо ModelMapper не підхопив через різницю імен (userId vs id)
        dto.setUserId(user.getId());

        return dto;
    }
}
