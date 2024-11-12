package ru.practicum.ewm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.RequestCreateDto;
import ru.practicum.ewm.dto.RequestsAfterChangesDto;
import ru.practicum.ewm.dto.RequestsToChangeDto;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.Request;
import ru.practicum.ewm.entity.RequestStatus;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.exception.CategoryNotFoundException;
import ru.practicum.ewm.exception.RequestNotFoundException;
import ru.practicum.ewm.exception.RequestValidationException;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.repository.RequestRepository;

import java.util.ArrayList;
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

    @Override
    public Collection<Request> getAllRequestToUsersEvent(long userId, long eventId) {
        log.info("Пытаюсь получить запросы на участие в событии id = {} у пользователя id = {}", eventId, userId);
        Collection<Request> requests = requestRepository.getAllUserEventsRequests(userId, eventId);
        if (requests.isEmpty()) {
            log.info("У пользователя с id = {} нет запросов на событие с id = {}, отдаю пустой список", userId, eventId);
            return List.of();
        }
        log.info("Получены запросы на событие с id = {}, созданного пользователем с id = {}", eventId, userId);

        return requests;
    }

    @Override
    @Transactional
    public RequestsAfterChangesDto changeRequestsStatus(long userId, long eventId, RequestsToChangeDto dto) {
        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);

        // Проверим, на всякий, что это событие принадлежит юзеру
        if (!event.getInitiator().equals(user)) {
            throw new RequestValidationException("Нельзя изменять статусы заявок чужих событий");
        }

        // Подготовим объект ответа и листы, которые в него запишем
        RequestsAfterChangesDto resultRequests = new RequestsAfterChangesDto();
        List<Request> confirmedRequest = new ArrayList<>();
        List<Request> rejectedRequest = new ArrayList<>();
        List<Request> requests = dto.getRequestIds().stream()
                .map(this::getRequestById).toList();

        switch (dto.getStatus()) {
            case RequestStatus.CONFIRMED -> makeRequestsConfirmed(userId,
                        eventId, resultRequests, event, confirmedRequest, rejectedRequest, dto, requests);
            case RequestStatus.REJECTED -> makeRequestsRejected(requests, confirmedRequest, rejectedRequest,
                    dto, resultRequests);
        }

        return resultRequests;

    }

    public Request getRequestById(long requestId) {
        log.info("Пытаюсь получить  запрос с id = {}", requestId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Запрос с id = {} не найден", requestId);
                    return new CategoryNotFoundException(String.format("Запрос с id = %d не найден", requestId));
                });
        log.info("запрос с id = {} получен", requestId);

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
            log.warn("Пользователь id = {} не может добавить запрос на участие в своём событии id = {}", userId, eventId);
            throw new RequestValidationException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        // нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
        // нужно разобраться где в событии фигурирует статус публикации

        // Проверяем достигнут ли лимит запросов на участие
        if (requestRepository.findAllByEventId(eventId).size() == event.getParticipantLimit()) {
            log.warn("Достигнут лимит запросов на участие в событии с id = {}", eventId);
            throw new RequestValidationException("У события достигнут лимит запросов на участие");
        }


    }

    private void makeRequestsConfirmed(long userId,
                                       long eventId,
                                       RequestsAfterChangesDto resultRequests,
                                       Event event,
                                       List<Request> confirmedRequest,
                                       List<Request> rejectedRequest,
                                       RequestsToChangeDto dto, List<Request> requests) {
        /* Дёрнем из бд один раз кол-во запросов по событию, а затем будем ++ эту переменную.
         Берём в учёт только подтверждённые запросы */
        long totalRequests = requestRepository.findAllByEventId(eventId).stream()
                .filter(re -> re.getStatus().equals(RequestStatus.CONFIRMED)).count();

        // Если нет ограничения по лимиту на событие, или отключена модерация, то подтверждение заявок не требуется
        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            confirmedRequest.addAll(dto.getRequestIds().stream().map(this::getRequestById).toList());
            resultRequests.setConfirmedRequests(confirmedRequest.stream().map(RequestMapper::mapRequestToDto).toList());
            resultRequests.setRejectedRequests(List.of());
            return;
        }

        // Нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)

        // Проверяем, что у всех запросов статус PENDING
        boolean allIsPending = requests.stream()
                .allMatch(re -> re.getStatus().equals(RequestStatus.PENDING));
        if (totalRequests + dto.getRequestIds().size() > event.getParticipantLimit() && allIsPending) {
            throw new RequestValidationException("Превышен лимит участников");
        }


        for (Request request: requests) {
            /* Статус можно изменить только у заявок, находящихся в состоянии ожидания
             и если не достигнут лимит события. В противном случае отправляем всё в rejected */
            if (request.getStatus() != RequestStatus.PENDING || totalRequests >= event.getParticipantLimit()) {
                rejectedRequest.add(request);
            } else {
                request.setStatus(dto.getStatus());
                confirmedRequest.add(request);
                totalRequests++;
            }
        }

        resultRequests.setConfirmedRequests(confirmedRequest.stream().map(RequestMapper::mapRequestToDto).toList());
        resultRequests.setRejectedRequests(rejectedRequest.stream().map(RequestMapper::mapRequestToDto).toList());

    }

    private void makeRequestsRejected(List<Request> requests,
                                      List<Request> confirmedRequest,
                                      List<Request> rejectedRequest,
                                      RequestsToChangeDto dto,
                                      RequestsAfterChangesDto resultRequests) {
        for (Request request: requests) {
            /* Статус можно изменить только у заявок, находящихся в состоянии ожидания
             и если не достигнут лимит события. В противном случае отправляем всё в rejected */
            if (request.getStatus() != RequestStatus.PENDING) {
                rejectedRequest.add(request);
            } else {
                request.setStatus(dto.getStatus());
                confirmedRequest.add(request);
            }
        }

        resultRequests.setConfirmedRequests(confirmedRequest.stream().map(RequestMapper::mapRequestToDto).toList());
        resultRequests.setRejectedRequests(rejectedRequest.stream().map(RequestMapper::mapRequestToDto).toList());
    }
}
