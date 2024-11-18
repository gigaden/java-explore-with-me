package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.compilation.CompilationResponseDto;
import ru.practicum.ewm.dto.compilation.CompilationUpdateDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.service.compilation.CompilationService;

@RestController
@RequestMapping("/admin/compilations")
public class AdminCompilationController {

    @Qualifier("compilationServiceImpl")
    private final CompilationService compilationService;

    @Autowired
    public AdminCompilationController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @PostMapping
    public ResponseEntity<CompilationResponseDto> createCompilation(@Valid @RequestBody NewCompilationDto compilation) {
        CompilationResponseDto compilationResponseDto = compilationService.createCompilation(compilation);

        return new ResponseEntity<>(compilationResponseDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<String> deleteCompilation(@PathVariable long compId) {

        compilationService.deleteCompilationById(compId);

        return new ResponseEntity<>("Подборка удалена", HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationResponseDto> updateCompilation(@PathVariable long compId,
                                                                    @Valid @RequestBody CompilationUpdateDto compilationDto) {

        CompilationResponseDto compilations = compilationService.updateCompilation(compId, compilationDto);

        return new ResponseEntity<>(compilations, HttpStatus.OK);
    }
}
