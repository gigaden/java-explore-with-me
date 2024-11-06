package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.RequestUserDto;
import ru.practicum.ewm.dto.ResponseUserDto;
import ru.practicum.ewm.entity.User;

public class UserMapper {

    public static ResponseUserDto mapToResponseUserDto(User user) {

        return ResponseUserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static User mapDtoToUser(RequestUserDto dto) {

        return User.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .build();
    }
}
