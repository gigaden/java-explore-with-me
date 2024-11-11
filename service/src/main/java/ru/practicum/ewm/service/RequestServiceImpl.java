package ru.practicum.ewm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.RequestCreateDto;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.Request;
import ru.practicum.ewm.entity.RequestStatus;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.exception.RequestNotFoundException;
import ru.practicum.ewm.exception.RequestValidationException;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.repository.RequestRepository;

import java.util.Collection;
import java.util.List;

@Service("requestServiceImpl")
@Transactional(readOnly = true)
@Slf4j
public class RequestServiceImpl implements RequestService {

    RequestRepository requestRepository;
    UserService userService;
    EventService eventService;

    public RequestServiceImpl(RequestRepository requestRepository,
                              UserService userService,
                              EventService eventService) {
        this.requestRepository = requestRepository;
        this.userService = userService;
        this.eventService = eventService;
    }

    @Override
    public Collection<Request> getAllRequests(long userId) {
        log.info("Пытаюсь получить запросы пользователя с id = {}", userId);
        userService.checkUserIsExist(userId);
        Collection<Request> requests = requestRepository.findAllByRequesterId(userId);

        if (requests.isEmpty()) {
            log.info("У пользователя с id = {} нет запросов, отдаю пустой список", userId);
            return List.of();
        }

        log.info("Запросы пользователя с id = {} получены", userId);
        return requests;
    }

    @Override
    @Transactional
    public Request createRequest(long userId, long eventId) {
        log.info("Пытаюсь добавить запрос пользователя с id = {} на событие с id = {}", userId, eventId);
        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);
        checkRequest(userId, eventId, user, event);
        RequestStatus status = event.isRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED;
        RequestCreateDto dto = RequestCreateDto.builder()
                .requester(user)
                .event(event)
                .status(status)
                .build();
        Request request = requestRepository.save(RequestMapper.mapRequest(dto));
        log.info("Создан запрос пользователя с id = {} на событие с id = {}", userId, eventId);

        return request;

    }

    @Override
    @Transactional
    public Request cancelOwnRequest(long userId, long requestId) {
        log.info("Попытка пользователя с id = {} отменить свою заявку на запрос с id = {}", userId, requestId);
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> {
                    log.warn("Запрос с id = {} от пользователя с id = {} не найден", requestId, userId);
                    return new RequestNotFoundException(String
                            .format("Запрос с id = %d от пользователя с id = %d не найден",
                                    requestId,
                                    userId));
                });
        request.setStatus(RequestStatus.PENDING);
        requestRepository.save(request);
        log.info("Пользователь с id = {} отменил свою заявку на запрос с id = {}", userId, requestId);

        return request;
    }


    public void checkRequest(long userId,
                             long eventId,
                             User user,
                             Event event) {
        // Проверяем существует ли уже такой запрос
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            log.warn("Запрос с id = {} от пользователя с id = {} уже существует", eventId, userId);
            throw new RequestValidationException("Нельзя добавить повторный запрос");
        }
        // Проверяем является ли инициатор собственником события
        if (event.getInitiator().equals(user)) {
            log.warn("Пользователь {} не может добавить запрос на участие в своём событии {}", user, event);
            throw new RequestValidationException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        // нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
        // нужно разобраться где в событии фигурирует статус публикации

        // Проверяем достигнут ли лимит запросов на участие
        if (requestRepository.findAllById(eventId).size() == event.getParticipantLimit()) {
            log.warn("Достигнут лимит запросов на участие в событии {}", event);
            throw new RequestValidationException("У события достигнут лимит запросов на участие");
        }


    }
}
