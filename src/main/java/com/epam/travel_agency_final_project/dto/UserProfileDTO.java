package com.epam.travel_agency_final_project.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserProfileDTO {
    private UUID userId;
    private String email;
    private BigDecimal balance;

    private String firstName;
    private String lastName;

    private boolean isLocked;

    private List<UserTourDTO> userTours;
}

