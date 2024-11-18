package ru.practicum.ewm.service.user;

import ru.practicum.ewm.dto.user.UserRequestDto;
import ru.practicum.ewm.entity.User;

import java.util.Collection;
import java.util.List;

public interface UserService {

    User getUserById(long userId);

    Collection<User> getAllUsers(List<Long> ids, int from, int size);

    User createUser(UserRequestDto user);

    void deleteUser(long userId);

    void checkUserIsExist(long id);
}
