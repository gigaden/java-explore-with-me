package ru.practicum.ewm.dto.category;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDto {
    private long id;
    @Size(max = 50, message = "Название категории не должно быть длиннее 50")
    private String name;
}
