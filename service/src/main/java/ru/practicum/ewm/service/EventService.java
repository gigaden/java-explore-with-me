package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EventRequestDto;
import ru.practicum.ewm.entity.Event;

public interface EventService {

    Event createEvent(long userId, EventRequestDto eventDto);
}
