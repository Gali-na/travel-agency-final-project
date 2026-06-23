package com.epam.travel_agency_final_project.entity;

import jakarta.persistence.GeneratedValue;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tours")

public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private BigDecimal price;

    // ЗМІНЕНО: тепер це повноцінний зв'язок JPA
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id")
    private City city;

    @Column(name = "arrival_date")
    private LocalDateTime arrivalDate;

    @Column(name = "eviction_date")
    private LocalDateTime evictionDate;

    @Column(name = "is_hot")
    private boolean isHot;

    @Column(name = "image_path")
    private String imagePath;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<TourTranslation> translations;
}