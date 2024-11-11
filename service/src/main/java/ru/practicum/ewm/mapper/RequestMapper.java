package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.RequestCreateDto;
import ru.practicum.ewm.dto.RequestResponseDto;
import ru.practicum.ewm.entity.Request;

import java.time.LocalDateTime;

public class RequestMapper {

    public static RequestResponseDto mapRequestToDto(Request request) {
        return RequestResponseDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .created(request.getCreated())
                .build();
    }

    public static Request mapRequest(RequestCreateDto dto) {
        return Request.builder()
                .event(dto.getEvent())
                .requester(dto.getRequester())
                .created(LocalDateTime.now())
                .status(dto.getStatus())
                .build();
    }
}
