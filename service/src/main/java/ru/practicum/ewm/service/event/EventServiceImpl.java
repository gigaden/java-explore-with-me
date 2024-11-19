package ru.practicum.ewm.service.event;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.StatisticDtoCreate;
import ru.practicum.dto.StatisticDtoResponse;
import ru.practicum.ewm.dto.event.EventAdminUpdateDto;
import ru.practicum.ewm.dto.event.EventParamRequest;
import ru.practicum.ewm.dto.event.EventRequestDto;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.EventState;
import ru.practicum.ewm.entity.QEvent;
import ru.practicum.ewm.entity.QRequest;
import ru.practicum.ewm.entity.RequestStatus;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.exception.EventNotFoundException;
import ru.practicum.ewm.exception.EventValidationException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.category.CategoryService;
import ru.practicum.ewm.service.user.UserService;
import ru.practicum.statistics.StatisticClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service("eventServiceImpl")
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final StatisticClient statisticClient;
    private final String appName;

    // Ограничение для публикации события
    // Дата начала изменяемого события должна быть не ранее чем за час от даты публикации при изменении админом
    // и два часа при изменении пользователем
    int secondsBeforePublish = 3600;
    int secondsBeforeUpdateByUser = 7200;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository,
                            UserService userService,
                            CategoryService categoryService,
                            StatisticClient statisticClient,
                            @Value("${application.name}") String appName) {
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.categoryService = categoryService;
        this.statisticClient = statisticClient;
        this.appName = appName;
    }


    @Override
    public Collection<Event> getAllUsersEvents(long userId, int from, int size) {
        log.info("Пытаюсь получить все события пользователя {}", userId);
        userService.checkUserIsExist(userId);
        Collection<Event> events = eventRepository.findUserEventsBetweenFromAndSize(from, size, userId);
        log.info("Получены все события пользователя {}", userId);

        return events;
    }

    @Override
    @Transactional
    public Event createEvent(long userId, EventRequestDto eventDto) {
        log.info("Пытаюсь создать новое событие {}", eventDto);

        if (eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            log.warn("дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
            throw new EventValidationException(String
                    .format("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: %s",
                            eventDto.getEventDate()));
        }

        Event event = eventRepository
                .save(EventMapper.mapRequestDtoToEvent(eventDto,
                        userService.getUserById(userId),
                        categoryService.getById(eventDto.getCategory())));


        log.info("Новое событие создано {}", event);
        event.setConfirmedRequests(0);
        event.setViews(0L);

        return event;
    }

    @Override
    public Event getUserEventsById(long userId, long eventId) {
        log.info("Пытаюсь получить событие с id = {} пользователя с id = {}", eventId, userId);
        userService.checkUserIsExist(userId);
        Event event = eventRepository.findEventByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> {
                    log.warn("Событие с id = {} пользователя с id = {} не найдено", eventId, userId);
                    return new EventNotFoundException("");
                });
        log.info("Событие с id = {} пользователя с id = {} получено", eventId, userId);
        return event;
    }

    @Override
    public Event getEventById(long eventId) {
        log.info("Пытаюсь получить событие с id = {}", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Событие с id = {} не найдено", eventId);
                    return new EventNotFoundException(String.format("Событие с id = %d не найдено", eventId));
                });
        log.info("Событие с id = {} получено", eventId);
        return event;
    }

    @Override
    public List<Event> getAllEventsByCompilationId(long compId) {
        log.info("Пытаюсь получить все события из подборки с id = {}", compId);
        List<Event> events = eventRepository.getAllEventsByCompilationId(compId);
        log.info("Получены все события из подборки с id = {}", compId);

        return events;
    }

    @Override
    public Collection<Event> getAllEventsByParam(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        log.info("Пытаюсь получить коллекцию событий с параметрами");
        QEvent event = QEvent.event;
        BooleanBuilder builder = new BooleanBuilder();

        if (users != null && !users.isEmpty()) {
            builder.and(event.initiator.id.in(users));
        }

        if (states != null && !states.isEmpty()) {
            builder.and(event.state.in(states));
        }

        if (categories != null && !categories.isEmpty()) {
            builder.and(event.category.id.in(categories));
        }

        if (rangeStart != null && rangeEnd != null) {
            builder.and(event.eventDate.between(rangeStart, rangeEnd));
        }

        JPAQuery<Event> query = new JPAQuery<>(entityManager);
        Collection<Event> events = query.select(event)
                .from(event)
                .where(builder)
                .offset(from)
                .limit(size)
                .fetch();

        log.info("Коллекция событий с параметрами получена");

        return events;
    }

    @Override
    @Transactional
    public Event updateEventById(long eventId, EventAdminUpdateDto dto) {
        log.info("Пытаюсь обновить событие с id = {}", eventId);
        Event event = getEventById(eventId);

        checkEventBeforeUpdate(event, dto);
        updateEventsFieldFromDto(event, dto);

        eventRepository.save(event);
        log.info("Обновлено событие с id = {}", eventId);

        return event;
    }

    @Override
    @Transactional
    public Event updateEventByCurrentUser(long userId, long eventId, EventAdminUpdateDto dto) {
        log.info("Попытка пользователя с id = {} обновить событие с id = {}", userId, eventId);
        Event event = getEventById(eventId);
        User user = userService.getUserById(userId);

        checkUsersEventBeforeUpdate(user, event, dto);
        updateEventsFieldFromDto(event, dto);

        eventRepository.save(event);
        log.info("Пользователь с id = {} обновил событие с id = {}", userId, eventId);

        return event;
    }

    // Получаем все опубликованные события, исходя из параметров
    @Override
    public Collection<Event> getAllEventsPublic(EventParamRequest param, HttpServletRequest statRequest) {
        log.info("Пытаюсь получить публичную коллекцию событий с параметрами {}", param);
        QEvent event = QEvent.event;
        QRequest request = QRequest.request;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(event.state.eq(EventState.PUBLISHED));

        if (param.getText() != null && !param.getText().isEmpty()) {
            builder.and(event.description.lower().contains(param.getText().toLowerCase())
                    .or(event.annotation.lower().contains(param.getText().toLowerCase())));
        }
        if (param.getCategories() != null && !param.getCategories().isEmpty()) {
            builder.and(event.category.id.in(param.getCategories()));
        }
        if (param.getPaid() != null) {
            builder.and(event.paid.eq(param.getPaid()));
        }
        if (param.getRangeStart() != null && param.getRangeEnd() != null) {
            builder.and(event.eventDate.between(param.getRangeStart(), param.getRangeEnd()));
        } else if (param.getRangeStart() == null || param.getRangeEnd() == null) {
            builder.and(event.eventDate.after(LocalDateTime.now()));
        }
        if (param.getOnlyAvailable() != null && param.getOnlyAvailable()) {
            builder.and(
                    event.participantLimit.gt(
                            JPAExpressions
                                    .select(request.count())
                                    .from(request)
                                    .where(
                                            request.event.id.eq(event.id)
                                                    .and(request.status.eq(RequestStatus.CONFIRMED))
                                    )
                    )
            );
        }


        JPAQuery<Event> query = new JPAQuery<>(entityManager);
        query.from(event);
        query.where(builder);
        if (param.getSort() != null) {
            switch (param.getSort()) {
                case "EVENT_DATE":
                    query.orderBy(event.eventDate.asc());
                    break;
                case "VIEWS":
                    query.orderBy(event.views.desc());
                    break; // Добавить поле с кол-вом просмотров
            }
        }

        query.offset(param.getFrom());
        query.limit(param.getSize());

        Collection<Event> events = query.fetch();

        log.info("Публичная коллекция событий с параметрами получена");
        sendStatisticToTheServer(statRequest);

        return events;
    }


    // Получаем опубликованное событие по id
    @Override
    @Transactional
    public Event getEventByIdPublic(long id, HttpServletRequest request) {
        log.info("Пытаюсь получить опубликованное событие с id = {}", id);
        Event event = eventRepository.findEventByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> {
                    log.warn("Событие c id = {} не найдено или недоступно", id);
                    return new EventNotFoundException("Событие не найдено или недоступно");
                });
        log.info("Опубликованное событие с id = {} получено", id);

        sendStatisticToTheServer(request);

        Collection<StatisticDtoResponse> stats = statisticClient.getStatistics(event.getCreatedOn(),
                LocalDateTime.now().plusMinutes(10), List.of(request.getRequestURI()), true);
        long uniqueViews = stats.stream().mapToLong(StatisticDtoResponse::getHits).sum();
        event.setViews(uniqueViews);

        eventRepository.save(event);

        return event;
    }

    // Отправляем статистику на сервер
    private void sendStatisticToTheServer(HttpServletRequest request) {
        StatisticDtoCreate statisticDtoCreate = StatisticDtoCreate.builder()
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .app(appName)
                .build();
        log.info("Пытаюсь отправить статистику на сервер {}", statisticDtoCreate);
        statisticClient.createStatistic(statisticDtoCreate);
        log.info("Статистика отправлена");
    }


    // Проверяем событие и дто перед обновлением
    public void checkEventBeforeUpdate(Event event, EventAdminUpdateDto dto) {
        if (Duration.between(LocalDateTime.now(), event.getEventDate()).toSeconds() <= secondsBeforePublish) {
            log.warn("дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
            throw new EventValidationException("Событие не удовлетворяет правилам редактирования.");
        }
        if (dto.getStateAction() == EventState.REJECT_EVENT && event.getState() == EventState.PUBLISHED) {
            log.warn("Событие можно отклонить, только если оно еще не опубликовано");
            throw new EventValidationException("Событие не удовлетворяет правилам редактирования.");
        }
        if (dto.getStateAction() == EventState.PUBLISH_EVENT && !event.getState().equals(EventState.PENDING)) {
            log.warn("Событие можно публиковать, только если оно в состоянии ожидания публикации");
            throw new EventValidationException("Событие не удовлетворяет правилам редактирования.");
        }
    }


    // Проверяем событие, пользователя и дто перед обновлением
    public void checkUsersEventBeforeUpdate(User user, Event event, EventAdminUpdateDto dto) {
        if (!event.getInitiator().equals(user)) {
            log.warn("Изменить можно только своё событие");
            throw new EventValidationException("Событие не удовлетворяет правилам редактирования.");
        }
        if (Duration.between(LocalDateTime.now(), event.getEventDate()).toSeconds() <= secondsBeforeUpdateByUser) {
            log.warn("дата начала изменяемого события должна быть не ранее чем за два часа от даты публикации");
            throw new EventValidationException("Событие не удовлетворяет правилам редактирования.");
        }
        if (event.getState() != EventState.CANCELED && event.getState() != EventState.PENDING) {
            log.warn("Изменить можно только отмененные события или события в состоянии ожидания модерации");
            throw new EventValidationException("Событие не удовлетворяет правилам редактирования" +
                    " - Изменить можно только отмененные события или события в состоянии ожидания модерации");
        }
    }

    private void updateEventsFieldFromDto(Event event, EventAdminUpdateDto dto) {
        if (dto.getAnnotation() != null && !dto.getAnnotation().isBlank()) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            event.setCategory(categoryService.getById(dto.getCategory()));
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null) {
            event.setLocationLat(dto.getLocation().getLat());
            event.setLocationLon(dto.getLocation().getLon());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getStateAction() != null) {
            if (dto.getStateAction().equals(EventState.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (dto.getStateAction().equals(EventState.REJECT_EVENT)) {
                event.setState(EventState.CANCELED);
            } else if (dto.getStateAction().equals(EventState.PUBLISH_EVENT)) {
                event.setState(EventState.PUBLISHED);
            } else if (dto.getStateAction().equals(EventState.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            }
        }
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            event.setTitle(dto.getTitle());
        }
    }


}
