package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EventRequestDto;
import ru.practicum.ewm.entity.Event;

import java.util.Collection;

public interface EventService {

    Collection<Event> getAllUsersEvents(long userId, int from, int size);

    Event createEvent(long userId, EventRequestDto eventDto);

    Event getUserEventsById(long userId, long eventId);

    Event getEventById(long eventId);

    Collection<Event> getAllEventsByCompilationId(long compId);
}
