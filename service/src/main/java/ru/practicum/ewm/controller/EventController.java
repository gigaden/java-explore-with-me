package ru.practicum.ewm.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventParamRequest;
import ru.practicum.ewm.dto.event.EventResponseDto;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.service.event.EventServiceImpl;

import java.util.Collection;

@RestController
@RequestMapping("/events")
public class EventController {

    @Qualifier("eventServiceImpl")
    private final EventServiceImpl eventService;

    @Autowired
    public EventController(EventServiceImpl eventService) {
        this.eventService = eventService;
    }

    /* Получение событий с возможностью фильтрации. Обратите внимание:
        это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события
        текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
        если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события,
        которые произойдут позже текущей даты и времени информация о каждом событии должна включать
        в себя количество просмотров и количество уже одобренных заявок на участие информацию о том,
        что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
        В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список */
    @GetMapping
    public ResponseEntity<Collection<EventResponseDto>> getAllEventsPublic(@Valid @ModelAttribute EventParamRequest param,
                                                                           HttpServletRequest request) {

        Collection<EventResponseDto> eventResponseDtos = eventService.getAllEventsPublic(param, request).stream()
                .map(EventMapper::mapEventToResponseDto).toList();

        return new ResponseEntity<>(eventResponseDtos, HttpStatus.OK);
    }

    /* Получение подробной информации об опубликованном событии по его идентификатору.
    Обратите внимание:
        событие должно быть опубликовано
        информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
        информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
        В случае, если события с заданным id не найдено, возвращает статус код 404*/
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEventByIdPublic(@PathVariable long id,
                                                               HttpServletRequest request) {

        EventResponseDto eventResponseDto = EventMapper.mapEventToResponseDto(eventService.getEventByIdPublic(id, request));

        return new ResponseEntity<>(eventResponseDto, HttpStatus.OK);
    }


}
