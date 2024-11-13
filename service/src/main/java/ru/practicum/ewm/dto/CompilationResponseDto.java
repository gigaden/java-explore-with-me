package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationResponseDto {

    private Collection<EventResponseDto> events;

    private long id;

    private boolean pinned;

    private String title;
}
