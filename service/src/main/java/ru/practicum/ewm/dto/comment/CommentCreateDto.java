package ru.practicum.ewm.dto.comment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateDto {

    @NotNull
    private Long eventId;

    @NotNull
    private Long userId;

    @NotNull String text;
}
