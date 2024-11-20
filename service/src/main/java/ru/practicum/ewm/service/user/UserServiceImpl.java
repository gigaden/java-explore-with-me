package ru.practicum.ewm.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.user.UserRequestDto;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.exception.CategoryNotFoundException;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.exception.UserValidationException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Service("userServiceImpl")
@Slf4j
@Transactional(readOnly = true)
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
            users = userRepository.findUsersByIdInOrderById(ids);
        } else {
            users = userRepository.findUsersInterval(size, from);
        }
        log.info("Получена коллекция пользователей");
        return users;
    }

    @Override
    public User getUserById(long userId) {
        log.info("Пытаюсь получить  пользователя с id = {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не найден", userId);
                    return new CategoryNotFoundException(String.format("Пользоатель с id = %d не найден", userId));
                });
        log.info("Пользователь с id = {} получен", userId);

        return user;
    }

    @Override
    @Transactional
    public User createUser(UserRequestDto userDto) {
        log.info("Пытаюсь создать нового пользователя {}", userDto);
        if (!userRepository.findAllByEmail(userDto.getEmail()).isEmpty()) {
            log.warn("Email {} не уникален", userDto.getEmail());
            throw new UserValidationException(String.format("Email %s не уникален", userDto.getEmail()));
        }
        User user = userRepository.save(UserMapper.mapDtoToUser(userDto));
        log.info("Создан новый пользователь {}", user);
        return user;
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        checkUserIsExist(userId);
        log.info("Пытаюсь удалить пользователя id = {}", userId);
        userRepository.deleteById(userId);
        log.info("Удалён пользователь с id = {}", userId);
    }

    // Проверяем, что пользователь существует
    @Override
    public void checkUserIsExist(long id) {
        log.info("Проверяем существует ли пользователь с id={}", id);
        if (userRepository.findById(id).isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с id=%d не найден", id));
        }
        log.info("Пользователь с id = {} существует.", id);
    }
}
