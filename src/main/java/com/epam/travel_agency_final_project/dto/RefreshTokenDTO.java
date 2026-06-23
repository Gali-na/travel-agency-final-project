package com.epam.travel_agency_final_project.dto;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenDTO {
    private UUID id;
    private UUID userId;
    private String token;
    private LocalDateTime expiryDate;
}