package ru.practicum.ewm.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class NewCompilationDto {

    private List<Long> events;

    private boolean pinned;

    @NotBlank(message = "Название подборки не может быть пустым")
    @Size(min = 1, max = 50, message = "Название подборки должно содержать не более 50 символов")
    private String title;
}
