package ru.practicum.server.controller;

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
import ru.practicum.dto.StatisticDtoCreate;
import ru.practicum.dto.StatisticDtoResponse;
import ru.practicum.server.service.StatisticService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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


    // Создание новой статистики
    @PostMapping("/hit")
    public ResponseEntity<StatisticDtoCreate> create(@Valid @RequestBody StatisticDtoCreate statisticDtoCreate) {
        StatisticDtoCreate statistic = service.create(statisticDtoCreate);
        return new ResponseEntity<>(statistic, HttpStatus.CREATED);
    }


    // Получаем статистику
    @GetMapping("/stats")
    public ResponseEntity<Collection<StatisticDtoResponse>> getAll(@RequestParam String start,
                                                                   @RequestParam String end,
                                                                   @RequestParam(defaultValue = "") List<String> uris,
                                                                   @RequestParam(defaultValue = "false") boolean unique) {

        // Декодируем строки и переводим их в даты
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime decodedStart = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), formatter);
        LocalDateTime decodedEnd = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), formatter);

        Collection<StatisticDtoResponse> statistics = service.getAll(
                decodedStart,
                decodedEnd,
                uris,
                unique);
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }
}
