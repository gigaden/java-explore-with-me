package ru.practicum.ewm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@DynamicInsert
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "annotation")
    @NotBlank
    @NotNull
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "initiator")
    private User initiator;

    @Column(name = "description")
    @NotBlank
    @NotNull
    private String description;

    @Column(name = "event_date")
    @NotBlank
    @NotNull
    private LocalDateTime eventDate;

    @Column(name = "created_on")
    @NotBlank
    @NotNull
    private LocalDateTime createdOn = LocalDateTime.now();

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "location_lat")
    @NotBlank
    @NotNull
    private String locationLat;

    @Column(name = "location_lon")
    @NotBlank
    @NotNull
    private String locationLon;

    @Column(name = "paid")
    @NotBlank
    @NotNull
    private boolean paid;

    @Column(name = "participant_limit")
    @NotBlank
    @NotNull
    private int participantLimit;

    @Column(name = "request_moderation")
    @NotBlank
    @NotNull
    private boolean requestModeration;

    @Column(name = "title")
    @NotBlank
    @NotNull
    private String title;

    @Column(name = "state")
    @NotBlank
    @NotNull
    @Enumerated(EnumType.STRING)
    private EventState state = EventState.PENDING;

    @Column(name = "views")
    private Long views;

    @Column(name = "confirmed_requests")
    private Integer confirmedRequests = 0;

}
