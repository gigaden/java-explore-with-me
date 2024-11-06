package ru.practicum.ewm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.RequestUserDto;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Service("userServiceImpl")
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public Collection<User> getAllUsers(List<Long> ids, int from, int size) {
        log.info("Пытаюсь получить коллекцию всех пользователей");
        List<User> users;
        if (ids != null && !ids.isEmpty()) {
           users = userRepository.findUsersByIdIn(ids);
        } else {
            users = userRepository.findUsersInterval(size, from);
        }
        log.info("Получена коллекция пользователей");
        return users;
    }

    @Override
    public User createUser(RequestUserDto userDto) {
        log.info("Пытаюсь создать нового пользователя {}", userDto);
        User user = userRepository.save(UserMapper.mapDtoToUser(userDto));
        log.info("Создан новый пользователь {}", user);
        return user;
    }

    @Override
    public void deleteUser(long userId) {
        checkUserIsExist(userId);
        log.info("Пытаюсь удалить пользователя id = {}", userId);
        userRepository.deleteById(userId);
        log.info("Удалён пользователь с id = {}", userId);
    }

    // Проверяем, что пользователь существует
    public void checkUserIsExist(Long id) {
        log.info("Проверяем существует ли пользователь с id={}", id);
        if (userRepository.findById(id).isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с id=%d не найден", id));
        }
        log.info("Пользователь с id = {} существует.", id);
    }
}
