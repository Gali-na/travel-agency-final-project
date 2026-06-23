package com.epam.travel_agency_final_project.entity;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.UUID;
@Entity
@Table(name = "CITY_TRANSLATIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CitiesTranslation {

    @EmbeddedId
    private CityTranslationId id;

    @Column(nullable = false)
    private String name; // Тут зберігається "Лондон" або "London"

    @ManyToOne
    @MapsId("cityId")
    @JoinColumn(name = "city_id")
    private City city;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CityTranslationId implements Serializable {
        @Column(name = "city_id")
        private UUID cityId;
        private String lang;
    }
}
