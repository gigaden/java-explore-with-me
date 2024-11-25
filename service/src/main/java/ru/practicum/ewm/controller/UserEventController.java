package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventAdminUpdateDto;
import ru.practicum.ewm.dto.event.EventRequestDto;
import ru.practicum.ewm.dto.event.EventResponseDto;
import ru.practicum.ewm.dto.request.RequestResponseDto;
import ru.practicum.ewm.dto.request.RequestsAfterChangesDto;
import ru.practicum.ewm.dto.request.RequestsToChangeDto;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.service.event.EventService;
import ru.practicum.ewm.service.request.RequestService;

import java.util.Collection;

@RestController
@RequestMapping("/users/{userId}/events")
public class UserEventController {

    @Qualifier("eventServiceImpl")
    private final EventService eventService;

    @Qualifier("requestServiceImpl")
    private final RequestService requestService;

    @Autowired
    public UserEventController(EventService eventService,
                               RequestService requestService) {
        this.eventService = eventService;
        this.requestService = requestService;
    }

    /* Получение событий, добавленных текущим пользователем.
     В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список */
    @GetMapping
    public ResponseEntity<Collection<EventResponseDto>> getEvents(@PathVariable long userId,
                                                                  @RequestParam(defaultValue = "0") int from,
                                                                  @RequestParam(defaultValue = "10") int size) {
        Collection<EventResponseDto> events = eventService.getAllUsersEvents(userId, from, size);


        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    /* Добавление нового события. Обратите внимание: дата и время на которые намечено событие не может быть раньше,
     чем через два часа от текущего момента*/
    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(@PathVariable long userId,
                                                        @Valid @RequestBody EventRequestDto eventDto) {
        EventResponseDto event = eventService.createEvent(userId, eventDto);

        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }

    /* Получение полной информации о событии, добавленным текущем пользователем.
     В случае, если события с заданным id не найдено, возвращает статус код 404*/
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> getUserEventsById(@PathVariable long userId,
                                                              @PathVariable long eventId) {
        EventResponseDto event = eventService.getUserEventsById(userId, eventId);

        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    /*
     Получаем список запросов на участие в указанном событии, если это пользователь его создал.*/
    @GetMapping("/{eventId}/requests")
    public ResponseEntity<Collection<RequestResponseDto>> getAllRequestToUsersEvent(@PathVariable long userId,
                                                                                    @PathVariable long eventId) {
        Collection<RequestResponseDto> requests = requestService.getAllRequestToUsersEvent(userId, eventId).stream()
                .map(RequestMapper::mapRequestToDto).toList();

        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    /* Изменение статуса заявок(подтверждена, отменена) на участие в событии текущего пользователя.
    Обратите внимание:
        если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
        нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
        статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
        если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
     */
    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<RequestsAfterChangesDto> changeRequestsStatus(@PathVariable long userId,
                                                                        @PathVariable long eventId,
                                                                        @Valid @RequestBody RequestsToChangeDto requestsToChangeDto) {
        RequestsAfterChangesDto requestsAfterChangesDto = requestService.changeRequestsStatus(userId, eventId, requestsToChangeDto);

        return new ResponseEntity<>(requestsAfterChangesDto, HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> changeEventByUser(@PathVariable long userId,
                                                              @PathVariable long eventId,
                                                              @Valid @RequestBody EventAdminUpdateDto dto) {
        EventResponseDto event = eventService
                .updateEventByCurrentUser(userId, eventId, dto);

        return new ResponseEntity<>(event, HttpStatus.OK);
    }


}
