package ru.practicum.ewm.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationUpdateDto {

    private List<Long> events;

    private Boolean pinned;

    @Size(min = 1, max = 50, message = "Название подборки должно содержать не более 50 символов")
    private String title;
}
