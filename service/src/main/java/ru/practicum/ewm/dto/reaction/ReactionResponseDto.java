package ru.practicum.ewm.dto.reaction;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.event.EventResponseDto;
import ru.practicum.ewm.dto.user.UserResponseDto;
import ru.practicum.ewm.entity.ReactionType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactionResponseDto {

    private long id;

    private long event;

    private long user;

    private ReactionType reaction;

    private LocalDateTime createdAt;
}
