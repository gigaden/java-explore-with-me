package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.comment.CommentCreateDto;
import ru.practicum.ewm.dto.comment.CommentResponseDto;
import ru.practicum.ewm.entity.Comment;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentResponseDto mapCommentToDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .build();
    }

    public static Comment mapDtoToNewComment(User user, Event event, String text) {
        return Comment.builder()
                .user(user)
                .event(event)
                .text(text)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
