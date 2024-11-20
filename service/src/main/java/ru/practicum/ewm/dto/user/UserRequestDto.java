package ru.practicum.ewm.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @Email
    @NotBlank
    @Size(min = 6, max = 254, message = "Длина email должна быть от 6 до 254")
    private String email;

    @NotBlank
    @Size(min = 2, max = 250, message = "Длина name должна быть от 2 до 254")
    private String name;
}
