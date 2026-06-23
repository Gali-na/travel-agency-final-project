package com.epam.travel_agency_final_project.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;
@Entity
@Table(name = "tours_translations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourTranslation {

    @EmbeddedId
    private TranslationId id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "tour_type")
    private String tourType;

    @Column(name = "transfer_type")
    private String transferType;

    @Column(name = "hotel_type")
    private String hotelType;

    @ManyToOne
    @MapsId("toursId")
    @JoinColumn(name = "tours_id")
    private Tour tour;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TranslationId implements Serializable {
        @Column(name = "tours_id")
        private UUID toursId;
        private String lang;
    }
}