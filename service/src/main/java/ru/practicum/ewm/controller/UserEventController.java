package ru.practicum.ewm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.EventRequestDto;
import ru.practicum.ewm.dto.EventResponseDto;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.service.EventService;

@RestController
@RequestMapping("/users/{userId}/events")
public class UserEventController {

    @Qualifier("eventServiceImpl")
    EventService eventService;

    @Autowired
    public UserEventController(EventService eventService){
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(@PathVariable long userId,
                                                        @RequestBody EventRequestDto eventDto) {
        EventResponseDto event = EventMapper.mapEventToResponseDto(eventService.createEvent(userId, eventDto));

        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }

}
