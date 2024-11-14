package ru.practicum.ewm.service;

import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.dto.CompilationResponseDto;
import ru.practicum.ewm.dto.CompilationUpdateDto;
import ru.practicum.ewm.dto.NewCompilationDto;

import java.util.Collection;

public interface CompilationService {

    CompilationResponseDto createCompilation(NewCompilationDto compilation);

    void deleteCompilationById(long compId);

    CompilationResponseDto updateCompilation(long compId, CompilationUpdateDto compilationDto);

    Collection<CompilationResponseDto> getCompilations(Boolean pinned, int from, int size);

    CompilationResponseDto getCompilationEventsById(long compId);
}
