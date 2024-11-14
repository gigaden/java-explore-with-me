package ru.practicum.ewm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.EventResponseDto;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.service.EventService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
public class AdminEventController {

    @Qualifier("eventServiceImpl")
    EventService eventService;

    @Autowired
    public AdminEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<Collection<EventResponseDto>> getAllEventsByParam(@RequestParam List<Integer> users,
                                                                            @RequestParam List<String> states,
                                                                            @RequestParam List<Integer> categories,
                                                                            @RequestParam String rangeStart,
                                                                            @RequestParam String rangeEnd,
                                                                            @RequestParam(defaultValue = "0") int from,
                                                                            @RequestParam(defaultValue = "10") int size) {

        // Декодируем строки и переводим их в даты
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime decodedStart = LocalDateTime.parse(URLDecoder.decode(rangeStart, StandardCharsets.UTF_8), formatter);
        LocalDateTime decodedEnd = LocalDateTime.parse(URLDecoder.decode(rangeEnd, StandardCharsets.UTF_8), formatter);

        Collection<EventResponseDto> events = eventService.getAllEventsByParam(users, states, categories,
                        decodedStart, decodedEnd, from, size).stream()
                .map(EventMapper::mapEventToResponseDto).toList();

        return new ResponseEntity<>(events, HttpStatus.OK);

    }
}
