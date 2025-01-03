package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.comment.CommentResponseDto;
import ru.practicum.ewm.entity.Comment;

public class CommentMapper {
    public static CommentResponseDto mapCommentToDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .build();
    }
}
