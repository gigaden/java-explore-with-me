package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.RequestsAfterChangesDto;
import ru.practicum.ewm.dto.RequestsToChangeDto;
import ru.practicum.ewm.entity.Request;
import ru.practicum.ewm.entity.RequestStatus;

import java.util.Collection;

public interface RequestService {

    Collection<Request> getAllRequests(long userId);

    Request createRequest(long userId, long eventId);

    Request cancelOwnRequest(long userId, long requestId);

    Collection<Request> getAllRequestToUsersEvent(long userId, long eventId);

    RequestsAfterChangesDto changeRequestsStatus(long userId, long eventId, RequestsToChangeDto dto);
}
