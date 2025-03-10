package ru.practicum.ewm.service.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.request.RequestCreateDto;
import ru.practicum.ewm.dto.request.RequestsAfterChangesDto;
import ru.practicum.ewm.dto.request.RequestsToChangeDto;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.EventState;
import ru.practicum.ewm.entity.Request;
import ru.practicum.ewm.entity.RequestStatus;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.exception.CategoryNotFoundException;
import ru.practicum.ewm.exception.RequestNotFoundException;
import ru.practicum.ewm.exception.RequestValidationException;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.service.event.EventService;
import ru.practicum.ewm.service.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service("requestServiceImpl")
@Transactional(readOnly = true)
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventService eventService;
    private final EventRepository eventRepository;

    public RequestServiceImpl(RequestRepository requestRepository,
                              UserService userService,
                              EventService eventService, EventRepository eventRepository) {
        this.requestRepository = requestRepository;
        this.userService = userService;
        this.eventService = eventService;
        this.eventRepository = eventRepository;
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
        if (requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED).size() >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            log.warn("Превышен лимит участников события с id = {}", eventId);
            throw new RequestValidationException("Превышен лимит участников события");
        }

        checkRequest(userId, eventId, user, event);
        RequestStatus status = RequestStatus.PENDING;
        if (!event.isRequestModeration()) {
            status = RequestStatus.CONFIRMED;
        } else if (event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
        }
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
        request.setStatus(RequestStatus.CANCELED);
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
        log.info("Пользователь id = {} пытается изменить статусы запросов для события id = {}", userId, eventId);
        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);

        // Проверим, на всякий, что это событие принадлежит юзеру
        if (!event.getInitiator().equals(user)) {
            log.warn("Попытка изменить статусы запросов для чужого события");
            throw new RequestValidationException("Нельзя изменять статусы запросов чужих событий");
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

        // Увеличиваем кол-во подтверждённых запросов у события
        event.setConfirmedRequests(event.getConfirmedRequests() + confirmedRequest.size());
        eventRepository.save(event);

        log.info("Пользователь id = {} изменил статусы запросов для события id = {}", userId, eventId);
        requestRepository.saveAll(confirmedRequest);
        requestRepository.saveAll(rejectedRequest);
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


    public void
    checkRequest(long userId,
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
        if (event.getState() != EventState.PUBLISHED) {
            log.warn("Событие с id = {} не опубликовано", eventId);
            throw new RequestValidationException("Нельзя участвовать в неопубликованном событии");
        }
        // Проверяем достигнут ли лимит запросов на участие
        if (requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED).size() > event.getParticipantLimit()) {
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
        long totalRequests = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED).stream()
                .filter(re -> re.getStatus().equals(RequestStatus.CONFIRMED)).count();

        // Если нет ограничения по лимиту на событие, или отключена модерация, то подтверждение заявок не требуется
        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            confirmedRequest.addAll(dto.getRequestIds().stream().map(this::getRequestById).toList());
            resultRequests.setConfirmedRequests(confirmedRequest.stream().map(RequestMapper::mapRequestToDto).toList());
            resultRequests.setRejectedRequests(List.of());
            log.info("Подтверждение заявок не требуется");
            return;
        }

        // Проверяем, что у всех запросов статус PENDING
        boolean allIsPending = requests.stream()
                .allMatch(re -> re.getStatus().equals(RequestStatus.PENDING));
        // Нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
        if (totalRequests + dto.getRequestIds().size() > event.getParticipantLimit() && allIsPending) {
            log.warn("Лимит участников = {} превышен", event.getParticipantLimit());
            throw new RequestValidationException("Превышен лимит участников");
        }


        for (Request request : requests) {
            /* Статус можно изменить только у заявок, находящихся в состоянии ожидания
             и если не достигнут лимит события. В противном случае отправляем всё в rejected */
            if (request.getStatus() != RequestStatus.PENDING || totalRequests >= event.getParticipantLimit()) {
                rejectedRequest.add(request);
            } else {
                request.setStatus(dto.getStatus());
                requestRepository.save(request);
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

        for (Request request : requests) {
            if (request.getStatus() == RequestStatus.CONFIRMED) {
                log.warn("Нельзя отменить заявку id = {}, т.к. она уже принята status = {}", request.getId(),
                        request.getStatus());
                throw new RequestValidationException(String
                        .format("Нельзя отменить уже принятую запись id = %d на участие в событии", request.getId()));
            }
            if (request.getStatus() == RequestStatus.PENDING) {
                request.setStatus(dto.getStatus());
                rejectedRequest.add(request);
            }
        }

        resultRequests.setConfirmedRequests(confirmedRequest.stream().map(RequestMapper::mapRequestToDto).toList());
        resultRequests.setRejectedRequests(rejectedRequest.stream().map(RequestMapper::mapRequestToDto).toList());
    }

    // Проверяем участие пользователя в событии
    @Override
    public boolean userWasInTheEvent(long userId, long eventId) {
        return requestRepository.existsByRequesterIdAndEventIdAndStatus(userId, eventId, RequestStatus.CONFIRMED);
    }
}
