package ru.practicum.ewm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EventRequestDto;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.exception.EventNotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.repository.EventRepository;

import java.util.Collection;
import java.util.List;

@Service("eventServiceImpl")
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    EventRepository eventRepository;
    UserService userService;
    CategoryService categoryService;

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


}
