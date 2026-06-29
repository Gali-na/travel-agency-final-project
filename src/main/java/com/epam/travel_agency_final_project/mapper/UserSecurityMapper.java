package com.epam.travel_agency_final_project.mapper;

import com.epam.travel_agency_final_project.dto.UserSecurityDTO;
import com.epam.travel_agency_final_project.entity.RoleEntity;
import com.epam.travel_agency_final_project.entity.User;
 // Припустимо, цей клас у вас є
import com.epam.travel_agency_final_project.model.Role;
import com.epam.travel_agency_final_project.repository.RoleRepository;
import com.epam.travel_agency_final_project.repository.UserRepository;
import com.epam.travel_agency_final_project.service.UserAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class UserSecurityMapper {
    public UserSecurityDTO toSecurityDto(User user) {
        if (user == null) return null;

        return UserSecurityDTO.builder()
                .id(user.getId())
                .login(user.getEmail())
                .isLocked(user.isLocked())
                .balance(user.getBalance())
                .roles(user.getRoles() != null ?
                        user.getRoles().stream()
                                .map(roleEntity -> roleEntity.getName().name())
                                .collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }
}
