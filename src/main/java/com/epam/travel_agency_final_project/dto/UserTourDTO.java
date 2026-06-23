package com.epam.travel_agency_final_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTourDTO {
    private UUID bookingId;
    private String status;
    private LocalDateTime createdAt;

    private TourFullDTO tour;
}
