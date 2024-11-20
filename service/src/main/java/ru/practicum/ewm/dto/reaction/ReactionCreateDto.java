package ru.practicum.ewm.dto.reaction;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.entity.ReactionType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactionCreateDto {

    @NotNull
    private Long eventId;

    @NotNull
    private Long userId;

    @NotNull
    private ReactionType reaction;
}
