package ru.practicum.ewm.dto.reaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
