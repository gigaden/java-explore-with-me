package ru.practicum.ewm.service;

import ru.practicum.ewm.entity.Request;

import java.util.Collection;

public interface RequestService {

    Collection<Request> getAllRequests(long userId);

    Request createRequest(long userId, long eventId);

    Request cancelOwnRequest(long userId, long requestId);
}
