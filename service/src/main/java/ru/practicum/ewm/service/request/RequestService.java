package ru.practicum.ewm.service.request;

import ru.practicum.ewm.dto.request.RequestsAfterChangesDto;
import ru.practicum.ewm.dto.request.RequestsToChangeDto;
import ru.practicum.ewm.entity.Request;

import java.util.Collection;

public interface RequestService {

    Collection<Request> getAllRequests(long userId);

    Request createRequest(long userId, long eventId);

    Request cancelOwnRequest(long userId, long requestId);

    Collection<Request> getAllRequestToUsersEvent(long userId, long eventId);

    RequestsAfterChangesDto changeRequestsStatus(long userId, long eventId, RequestsToChangeDto dto);
}
