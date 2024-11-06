package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.RequestUserDto;
import ru.practicum.ewm.entity.User;

import java.util.Collection;
import java.util.List;

public interface UserService {

    Collection<User> getAllUsers(List<Long> ids, int from, int size);

    User createUser(RequestUserDto user);

    void deleteUser(long userId);
}
