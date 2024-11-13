package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.CompilationResponseDto;
import ru.practicum.ewm.dto.NewCompilationDto;

public interface CompilationService {

    CompilationResponseDto createCompilation(NewCompilationDto compilation);
}
