package ru.practicum.ewm.dto.event;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventParamRequest {

    @Size(min = 3)
    String text;

    List<Long> categories;

    Boolean paid;

    String rangeStart;

    String rangeEnd;

    Boolean onlyAvailable;

    String sort;

    @Builder.Default
    Integer from = 0;

    @Builder.Default
    Integer size = 10;

    public LocalDateTime getRangeStart() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Сделал так, что даты не обязательны, поэтому проверяем на null, чтобы в .decode не вылетела ошибка
        return this.rangeStart != null ? LocalDateTime.parse(URLDecoder.decode(this.rangeStart, StandardCharsets.UTF_8), formatter) : null;

    }

    public LocalDateTime getRangeEnd() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Сделал так, что даты не обязательны, поэтому проверяем на null, чтобы в .decode не вылетела ошибка
        return this.rangeEnd != null ? LocalDateTime.parse(URLDecoder.decode(this.rangeEnd, StandardCharsets.UTF_8), formatter) : null;

    }
}
