package com.epam.travel_agency_final_project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "user_translations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTranslation {
    @EmbeddedId
    private UserTranslationId id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class UserTranslationId implements Serializable {
        private UUID userId;
        private String lang;
    }
}