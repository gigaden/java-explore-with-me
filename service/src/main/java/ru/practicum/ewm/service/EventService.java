package ru.practicum.ewm.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.dto.EventAdminRequestDto;
import ru.practicum.ewm.dto.EventParamRequest;
import ru.practicum.ewm.dto.EventRequestDto;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.EventState;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventService {

    Collection<Event> getAllUsersEvents(long userId, int from, int size);

    Event createEvent(long userId, EventRequestDto eventDto);

    Event getUserEventsById(long userId, long eventId);

    Event getEventById(long eventId);

    Collection<Event> getAllEventsByCompilationId(long compId);

    Collection<Event> getAllEventsByParam(List<Long> users,
                                          List<EventState> states,
                                          List<Long> categories,
                                          LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          int from,
                                          int size);

    Event updateEventById(long eventId, EventAdminRequestDto dto);

    Collection<Event> getAllEventsPublic(EventParamRequest param, HttpServletRequest request);

    Event getEventByIdPublic(long id, HttpServletRequest request);

    Event updateEventByCurrentUser(long userId, long eventId, EventAdminRequestDto dto);
}
