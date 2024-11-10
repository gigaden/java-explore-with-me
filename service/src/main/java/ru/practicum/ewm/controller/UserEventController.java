package ru.practicum.ewm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.EventRequestDto;
import ru.practicum.ewm.dto.EventResponseDto;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.service.EventService;

import java.util.Collection;

@RestController
@RequestMapping("/users/{userId}/events")
public class UserEventController {

    @Qualifier("eventServiceImpl")
    EventService eventService;

    @Autowired
    public UserEventController(EventService eventService){
        this.eventService = eventService;
    }

    /* Получение событий, добавленных текущим пользователем.
     В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список */
    @GetMapping
    public ResponseEntity<Collection<EventResponseDto>> getEvents(@PathVariable long userId,
                                                                   @RequestParam(defaultValue = "0") int from,
                                                                   @RequestParam(defaultValue = "10") int size) {
        Collection<EventResponseDto> events = eventService.getAllUsersEvents(userId, from, size).stream()
                .map(EventMapper::mapEventToResponseDto).toList();

        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    /* Добавление нового событиф. Обратите внимание: дата и время на которые намечено событие не может быть раньше,
     чем через два часа от текущего момента*/
    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(@PathVariable long userId,
                                                        @RequestBody EventRequestDto eventDto) {
        EventResponseDto event = EventMapper.mapEventToResponseDto(eventService.createEvent(userId, eventDto));

        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }

    /* Получение полной информации о событии, добавленным текущем пользователем.
     В случае, если события с заданным id не найдено, возвращает статус код 404*/
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> getUserEventsById(@PathVariable long userId,
                                                              @PathVariable long eventId) {
        EventResponseDto event = EventMapper.mapEventToResponseDto(eventService.getUserEventsById(userId, eventId));

        return new ResponseEntity<>(event, HttpStatus.OK);
    }


}
