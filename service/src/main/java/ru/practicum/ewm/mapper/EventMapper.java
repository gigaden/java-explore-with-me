package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.event.EventRequestDto;
import ru.practicum.ewm.dto.event.EventResponseDto;
import ru.practicum.ewm.dto.reaction.ReactionResponseDto;
import ru.practicum.ewm.entity.*;

import java.time.LocalDateTime;
import java.util.List;

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
                .state(EventState.PENDING)
                .title(dto.getTitle())
                .createdOn(LocalDateTime.now())
                .build();
    }

    public static EventResponseDto mapEventToResponseDto(Event event, List<ReactionResponseDto> reactions) {

        return EventResponseDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.mapToResponseUserDto(event.getInitiator()))
                .location(Location.builder()
                        .lon(event.getLocationLon())
                        .lat(event.getLocationLat())
                        .build())
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .reactions(reactions)
                .build();
    }

    public static EventResponseDto mapEventToResponseDto(Event event) {

        return EventResponseDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.mapToResponseUserDto(event.getInitiator()))
                .location(Location.builder()
                        .lon(event.getLocationLon())
                        .lat(event.getLocationLat())
                        .build())
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }
}
