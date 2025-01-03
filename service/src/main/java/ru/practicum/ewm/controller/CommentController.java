package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.comment.CommentCreateDto;
import ru.practicum.ewm.dto.comment.CommentResponseDto;
import ru.practicum.ewm.service.comment.CommentService;

import java.util.Collection;

@RestController
@RequestMapping("/comments")
public class CommentController {
    @Qualifier("commentServiceImpl")
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<CommentResponseDto> getAllEventsComments(@PathVariable long eventId,
                                                               @RequestParam(required = false) Integer from,
                                                               @RequestParam(required = false) Integer size) {
        return commentService.getAllCommentsByEventId(eventId, from, size);
    }

    @PostMapping
    public CommentResponseDto addComment(@Valid @RequestBody CommentCreateDto dto) {
        return commentService.addCommentToEvent(dto);
    }


}
