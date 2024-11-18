package ru.practicum.ewm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.RequestStatus;
import ru.practicum.ewm.entity.User;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestCreateDto {


    @NotNull
    private Event event;

    @NotNull
    private User requester;

    private RequestStatus status;
}
