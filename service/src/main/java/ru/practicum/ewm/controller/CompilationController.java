package ru.practicum.ewm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.compilation.CompilationResponseDto;
import ru.practicum.ewm.service.compilation.CompilationService;

import java.util.Collection;

@RestController
@RequestMapping("/compilations")
public class CompilationController {

    @Qualifier("compilationServiceImpl")
    private final CompilationService compilationService;

    @Autowired
    public CompilationController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @GetMapping
    public ResponseEntity<Collection<CompilationResponseDto>> getAllCompilations(@RequestParam(defaultValue = "false") Boolean pinned,
                                                                                 @RequestParam(defaultValue = "0") int from,
                                                                                 @RequestParam(defaultValue = "10") int size) {
        Collection<CompilationResponseDto> compilationResponseDtos = compilationService.getCompilations(pinned, from, size);

        return new ResponseEntity<>(compilationResponseDtos, HttpStatus.OK);

    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationResponseDto> getCompilationById(@PathVariable long compId) {

        CompilationResponseDto compilationResponseDto = compilationService.getCompilationEventsById(compId);

        return new ResponseEntity<>(compilationResponseDto, HttpStatus.OK);
    }
}
