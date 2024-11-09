package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.entity.Location;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDto {

    private String annotation;

    private long category;

    private String description;

    private LocalDateTime eventDate;

    private Location location;

    private boolean paid;

    private int participantLimit;

    private boolean requestModeration;

    private String title;
}
