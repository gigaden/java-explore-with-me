package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.EventAdminRequestDto;
import ru.practicum.ewm.dto.EventResponseDto;
import ru.practicum.ewm.entity.EventState;
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
    private final EventService eventService;

    @Autowired
    public AdminEventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Эндпоинт возвращает полную информацию обо всех событиях подходящих под переданные условия
    //В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
    @GetMapping
    public ResponseEntity<Collection<EventResponseDto>> getAllEventsByParam(@RequestParam(required = false) List<Long> users,
                                                                            @RequestParam(required = false)  List<EventState> states,
                                                                            @RequestParam(required = false) List<Long> categories,
                                                                            @RequestParam(required = false) String rangeStart,
                                                                            @RequestParam(required = false) String rangeEnd,
                                                                            @RequestParam(defaultValue = "0") int from,
                                                                            @RequestParam(defaultValue = "10") int size) {

        // Декодируем строки и переводим их в даты
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Сделал так, что даты не обязательны, поэтому проверяем на null, чтобы в .decode не вылетела ошибка
        LocalDateTime decodedStart = rangeStart != null ? LocalDateTime.parse(URLDecoder.decode(rangeStart, StandardCharsets.UTF_8), formatter) : null;
        LocalDateTime decodedEnd = rangeEnd != null ? LocalDateTime.parse(URLDecoder.decode(rangeEnd, StandardCharsets.UTF_8), formatter) : null;

        Collection<EventResponseDto> events = eventService.getAllEventsByParam(users, states, categories,
                        decodedStart, decodedEnd, from, size).stream()
                .map(EventMapper::mapEventToResponseDto).toList();

        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    /* Редактирование данных любого события администратором. Валидация данных не требуется. Обратите внимание:
        дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
        событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
        событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)
        */
    @PatchMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> updateEventById(@PathVariable long eventId,
                                                            @Valid @RequestBody EventAdminRequestDto dto) {
        EventResponseDto event = EventMapper.mapEventToResponseDto(eventService.updateEventById(eventId, dto));

        return new ResponseEntity<>(event, HttpStatus.OK);

    }

}
