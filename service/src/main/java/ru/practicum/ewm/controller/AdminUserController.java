package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.UserRequestDto;
import ru.practicum.ewm.dto.UserResponseDto;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.service.UserService;
import ru.practicum.statistics.StatisticClient;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    StatisticClient statisticClient;

    @Qualifier("userServiceImpl")
    private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    // Добавляем нового пользователя
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userDto) {
        UserResponseDto user = UserMapper.mapToResponseUserDto(userService.createUser(userDto));

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    /*Возвращает информацию обо всех пользователях (учитываются параметры ограничения выборки),
     либо о конкретных (учитываются указанные идентификаторы) В случае, если по заданным фильтрам не найдено ни одного
     пользователя, возвращает пустой список*/
    @GetMapping
    public ResponseEntity<Collection<UserResponseDto>> getAllUsers(@RequestParam(defaultValue = "") List<Long> ids,
                                                                   @RequestParam(defaultValue = "0") int from,
                                                                   @RequestParam(defaultValue = "10") int size) {
        Collection<UserResponseDto> users = userService.getAllUsers(ids, from, size).stream()
                .map(UserMapper::mapToResponseUserDto).toList();

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Удаляем пользователя по id
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable long userId) {
        userService.deleteUser(userId);

        return new ResponseEntity<>("Пользователь удален", HttpStatus.NO_CONTENT);
    }
}
