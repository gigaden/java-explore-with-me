package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.UserRequestDto;
import ru.practicum.ewm.entity.User;

import java.util.Collection;
import java.util.List;

public interface UserService {

    Collection<User> getAllUsers(List<Long> ids, int from, int size);

    User createUser(UserRequestDto user);

    void deleteUser(long userId);
}
