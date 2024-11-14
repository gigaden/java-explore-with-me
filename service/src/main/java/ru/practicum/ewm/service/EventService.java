package ru.practicum.ewm.service;

import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.dto.EventRequestDto;
import ru.practicum.ewm.entity.Event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventService {

    Collection<Event> getAllUsersEvents(long userId, int from, int size);

    Event createEvent(long userId, EventRequestDto eventDto);

    Event getUserEventsById(long userId, long eventId);

    Event getEventById(long eventId);

    Collection<Event> getAllEventsByCompilationId(long compId);

    Collection<Event> getAllEventsByParam(List<Integer> users,
                                          List<String> states,
                                          List<Integer> categories,
                                          LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          int from,
                                          int size);
}
