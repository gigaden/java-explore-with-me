package ru.practicum.ewm.service.comment;

import ru.practicum.ewm.dto.comment.CommentCreateDto;
import ru.practicum.ewm.dto.comment.CommentResponseDto;

import java.util.Collection;

public interface CommentService {
    Collection<CommentResponseDto> getAllCommentsByEventId(long eventId, Integer from, Integer size);

    CommentResponseDto addCommentToEvent(CommentCreateDto dto);
}
