package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.EventRequestDto;
import ru.practicum.ewm.dto.EventResponseDto;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.Location;
import ru.practicum.ewm.entity.User;

import java.time.LocalDateTime;

public class EventMapper {

    public static Event mapRequestDtoToEvent(EventRequestDto dto,
                                             User user,
                                             Category category) {
        return Event.builder()
                .annotation(dto.getAnnotation())
                .category(category)
                .initiator(user)
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .locationLat(dto.getLocation().getLat())
                .locationLon(dto.getLocation().getLon())
                .paid(dto.isPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.isRequestModeration())
                .title(dto.getTitle())
                .createdOn(LocalDateTime.now())
                .build();
    }

    public static EventResponseDto mapEventToResponseDto(Event event) {

        return EventResponseDto.builder()
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .confirmedRequests(5) // Добавить расчёт
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(event.getInitiator())
                .location(Location.builder()
                        .lon(event.getLocationLon())
                        .lat(event.getLocationLat())
                        .build())
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(LocalDateTime.now()) // Добавить
                .requestModeration(event.isRequestModeration())
                .state("PUBLISHED") // Добавить, возможно enum
                .title(event.getTitle())
                .views(999) // Добавить расчёт
                .build();
    }
}
