package ru.practicum.ewm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.dto.request.RequestResponseDto;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.service.request.RequestService;

import java.util.Collection;

@Controller
@RequestMapping("/users/{userId}/requests")
public class RequestController {

    @Qualifier("requestServiceImpl")
    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    /* Получение информации о заявках текущего пользователя на участие в чужих событиях.
     В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список*/
    @GetMapping
    public ResponseEntity<Collection<RequestResponseDto>> getAllRequests(@PathVariable long userId) {
        Collection<RequestResponseDto> requests = requestService.getAllRequests(userId).stream()
                .map(RequestMapper::mapRequestToDto).toList();

        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    /* Добавление запроса от текущего пользователя на участие в событии.
     Обратите внимание:
        нельзя добавить повторный запрос (Ожидается код ошибки 409)
        инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
        нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
        если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
        если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного
     */
    @PostMapping
    public ResponseEntity<RequestResponseDto> createRequest(@PathVariable long userId,
                                                            @RequestParam long eventId) {
        RequestResponseDto request = RequestMapper.mapRequestToDto(requestService.createRequest(userId, eventId));

        return new ResponseEntity<>(request, HttpStatus.CREATED);

    }

    /* Отмена своего запроса на участие в событии */
    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<RequestResponseDto> cancelOwnRequest(@PathVariable long userId,
                                                               @PathVariable long requestId) {
        RequestResponseDto request = RequestMapper.mapRequestToDto(requestService.cancelOwnRequest(userId, requestId));

        return new ResponseEntity<>(request, HttpStatus.OK);

    }

}
