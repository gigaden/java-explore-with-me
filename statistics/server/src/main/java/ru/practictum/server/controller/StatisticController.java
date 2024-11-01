package ru.practictum.server.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<StatisticDtoCreate> create(@Valid @RequestBody StatisticDtoCreate statisticDtoCreate) {
        StatisticDtoCreate statistic = service.create(statisticDtoCreate);
        return new ResponseEntity<>(statistic, HttpStatus.CREATED);
    }


    @GetMapping("/stats")
    public ResponseEntity<Collection<StatisticDtoResponse>> getAll(@RequestParam LocalDateTime start,
                                                                   @RequestParam LocalDateTime end,
                                                                   @RequestParam List<String> uris,
                                                                   @RequestParam(defaultValue = "false") boolean unique) {
        Collection<StatisticDtoResponse> statistics = service.getAll(start, end, uris, unique);
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }
}
