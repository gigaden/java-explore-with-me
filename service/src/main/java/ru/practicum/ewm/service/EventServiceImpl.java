package ru.practicum.ewm.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EventAdminRequestDto;
import ru.practicum.ewm.dto.EventRequestDto;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.EventState;
import ru.practicum.ewm.entity.QEvent;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.exception.EventNotFoundException;
import ru.practicum.ewm.exception.EventValidationException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.repository.EventRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service("eventServiceImpl")
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    EventRepository eventRepository;
    UserService userService;
    CategoryService categoryService;

    // Ограничение для публикации события
    // Дата начала изменяемого события должна быть не ранее чем за час от даты публикации.
    int secondsBeforePublish = 3600;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository,
                            UserService userService,
                            CategoryService categoryService) {
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.categoryService = categoryService;
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
        Event event = eventRepository
                .save(EventMapper.mapRequestDtoToEvent(eventDto,
                        userService.getUserById(userId),
                        categoryService.getById(eventDto.getCategory())));


        log.info("Новое событие создано {}", event);

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
                    return new EventNotFoundException("");
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
        //Collection<Event> events = eventRepository.getAllEventsByParam(users, states, categories, rangeStart, rangeEnd, from, size);
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
    public Event updateEventById(long eventId, EventAdminRequestDto dto) {
        log.info("Пытаюсь обновить событие с id = {}", eventId);
        Event event = getEventById(eventId);

        checkEventBeforeUpdate(event, dto);
        updateEventsFieldFromDto(event, dto);

        eventRepository.save(event);
        log.info("Обновлено событие с id = {}", eventId);

        return event;
    }

    // Проверяем событие и дто перед обновлением
    public void checkEventBeforeUpdate(Event event, EventAdminRequestDto dto) {
        if (Duration.between(LocalDateTime.now(), event.getEventDate()).toSeconds() <= secondsBeforePublish) {
            log.warn("дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
            throw new EventValidationException("Событие не удовлетворяет правилам редактирования.");
        }
        if (dto.getStateAction() == EventState.REJECT_EVENT && event.getState() == EventState.PUBLISHED) {
            log.warn("Событие можно отклонить, только если оно еще не опубликовано");
            throw new EventValidationException("Событие не удовлетворяет правилам редактирования.");
        }
        if (!event.getState().equals(EventState.PENDING)) {
            log.warn("Событие можно публиковать, только если оно в состоянии ожидания публикации");
            throw new EventValidationException("Событие не удовлетворяет правилам редактирования.");
        }
    }

    private void updateEventsFieldFromDto(Event event, EventAdminRequestDto dto) {
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
            if (dto.getStateAction().equals(EventState.PUBLISH_EVENT)) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (dto.getStateAction().equals(EventState.REJECT_EVENT)) {
                event.setState(EventState.CANCELED);
            }
        }
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            event.setTitle(dto.getTitle());
        }
    }


}
