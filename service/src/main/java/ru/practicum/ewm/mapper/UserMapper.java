package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.UserRequestDto;
import ru.practicum.ewm.dto.UserResponseDto;
import ru.practicum.ewm.entity.User;

public class UserMapper {

    public static UserResponseDto mapToResponseUserDto(User user) {

        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User mapDtoToUser(UserRequestDto dto) {

        return User.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .build();
    }
}
