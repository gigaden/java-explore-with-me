package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.compilation.CompilationResponseDto;
import ru.practicum.ewm.entity.Compilation;
import ru.practicum.ewm.entity.Event;

import java.util.Collection;

public class CompilationMapper {

    public static CompilationResponseDto mapToCompilationResponseDto(Compilation compilation,
                                                                     Collection<Event> events) {
        return CompilationResponseDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(events.stream().map(EventMapper::mapEventToResponseDto).toList())
                .build();
    }
}
