package com.epam.travel_agency_final_project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_tours")
@Getter
@Setter
public class UserTour {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tour_id")
    private Tour tour;

    private String status;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

//    @CreationTimestamp // Автоматично встановить час при створенні об'єкта
//    @Column(name = "created_at", updatable = false)
//    private LocalDateTime createdAt;
}