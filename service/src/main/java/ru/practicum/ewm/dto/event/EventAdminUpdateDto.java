package ru.practicum.ewm.dto.event;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.entity.EventState;
import ru.practicum.ewm.entity.Location;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventAdminUpdateDto {

    @Size(min = 20, max = 2000, message = "Длина должна быть от 20 до 2000")
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000, message = "Длина должна быть от 20 до 7000")
    private String description;

    @Future
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    @Min(1)
    private Integer participantLimit;

    private Boolean requestModeration;

    private EventState stateAction;

    @Size(min = 3, max = 120, message = "Длина должна быт не менее 20")
    private String title;
}
