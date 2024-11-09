package ru.practicum.ewm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EventRequestDto;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.repository.EventRepository;

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
    @Transactional
    public Event createEvent(long userId, EventRequestDto eventDto) {
        log.info("Пытаюсь создать новое событие {}", eventDto);
        Event event = eventRepository
                .save(EventMapper.mapRequestDtoToEvent(eventDto,
                        userService.getUserById(userId),
                        categoryService.getById(userId)));


        log.info("Новое событие создано {}", event);

        return event;
    }
}
