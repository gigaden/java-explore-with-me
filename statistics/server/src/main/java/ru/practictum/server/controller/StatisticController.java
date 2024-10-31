package ru.practictum.server.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practictum.server.service.StatisticService;
import ru.practicum.dto.StatisticDtoCreate;
import ru.practicum.dto.StatisticDtoResponse;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
public class StatisticController {

    @Qualifier("statisticServiceImpl")
    private final StatisticService service;

    @Autowired
    public StatisticController(StatisticService service) {
        this.service = service;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatisticDtoCreate create(@Valid @RequestBody StatisticDtoCreate statistic) {
        return service.create(statistic);
    }


    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public Collection<StatisticDtoResponse> getAll(@RequestParam LocalDateTime start,
                                                   @RequestParam LocalDateTime end,
                                                   @RequestParam List<String> uris,
                                                   @RequestParam(defaultValue = "false") boolean unique) {
        return service.getAll(start, end, uris, unique);
    }
}
