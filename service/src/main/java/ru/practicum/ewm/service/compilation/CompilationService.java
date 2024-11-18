package ru.practicum.ewm.service.compilation;

import ru.practicum.ewm.dto.compilation.CompilationResponseDto;
import ru.practicum.ewm.dto.compilation.CompilationUpdateDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;

import java.util.Collection;

public interface CompilationService {

    CompilationResponseDto createCompilation(NewCompilationDto compilation);

    void deleteCompilationById(long compId);

    CompilationResponseDto updateCompilation(long compId, CompilationUpdateDto compilationDto);

    Collection<CompilationResponseDto> getCompilations(Boolean pinned, int from, int size);

    CompilationResponseDto getCompilationEventsById(long compId);
}
