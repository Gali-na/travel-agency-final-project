package com.epam.travel_agency_final_project.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserSecurityDTO {
    private UUID id;
    private String login;
    private List<String> roles = new ArrayList<>();
    private boolean isLocked;
    private BigDecimal balance;
}