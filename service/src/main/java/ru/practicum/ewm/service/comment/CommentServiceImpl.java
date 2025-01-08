package ru.practicum.ewm.service.comment;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.comment.CommentCreateDto;
import ru.practicum.ewm.dto.comment.CommentResponseDto;
import ru.practicum.ewm.entity.Comment;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.QComment;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.exception.CommentValidationException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.service.event.EventService;
import ru.practicum.ewm.service.request.RequestService;
import ru.practicum.ewm.service.user.UserService;

import java.util.Collection;

@Service("commentServiceImpl")
@Slf4j
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final EventService eventService;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final RequestService requestService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public CommentServiceImpl(EventService eventService, UserService userService,
                              CommentRepository commentRepository, RequestService requestService) {
        this.eventService = eventService;
        this.userService = userService;
        this.commentRepository = commentRepository;
        this.requestService = requestService;
    }

    // Ищем все комментарии события
    @Override
    public Collection<CommentResponseDto> getAllCommentsByEventId(long eventId, Integer from, Integer size) {
        log.info("Пытаюсь получить комментарии для события с id = {}", eventId);
        Event event = eventService.getEventById(eventId);

        BooleanBuilder builder = new BooleanBuilder();
        QComment comment = QComment.comment;
        builder.and(comment.event.id.in(eventId));
        JPAQuery<Comment> query = new JPAQuery<>(entityManager);

        query.select(comment).from(comment).where(builder);
        if ((from != null && size != null) && (from >= 0 && size > 0 && from < size)) {
            query.offset(from).limit(size);
        } else log.warn("Указаны неверные параметры выборки from = {}, size = {}", from, size);

        Collection<Comment> comments = query.fetch();

        if (comments.isEmpty()) {
            log.info("Комментариев к событию id = {} нет, вернул пустую коллекцию", event.getId());
        } else log.info("Получена коллекция комментариев для события с id = {}", eventId);

        return comments.stream().map(CommentMapper::mapCommentToDto).toList();
    }

    @Override
    @Transactional
    public CommentResponseDto addCommentToEvent(CommentCreateDto dto) {
        log.info("Пытаюсь добавить новый комментарий юзера id = {} к событию id = {}", dto.getUserId(), dto.getEventId());
        Event event = eventService.getEventById(dto.getEventId());
        User user = userService.getUserById(dto.getUserId());
        checkComment(dto, user, event);

        Comment comment = commentRepository
                .save(CommentMapper.mapDtoToNewComment(user, event, dto.getText()));
        log.info("Пользователь id = {} добавил комментарий к событию id = {}", user.getId(), event.getId());

        return CommentMapper.mapCommentToDto(comment);
    }

    public void checkComment(CommentCreateDto dto, User user, Event event) {
        if (dto.getText().isEmpty() || dto.getText().isBlank()) {
            log.warn("Попытка оставить пустой комментарий");
            throw new CommentValidationException("Не добавлен текст комментария");
        }
        // Проверяем, что юзер не оставляет коммент на своё событие
        if (event.getInitiator().equals(user)) {
            log.warn("Юзер пытается комментировать своё событие");
            throw new CommentValidationException("Нельзя комментировать своё событие");
        }
        // Проверяем, что юзер был подписан на это событие
        if (!requestService.userWasInTheEvent(user.getId(), event.getId())) {
            log.warn("Юзер пытается комментировать событие, в котором не участвовал");
            throw new CommentValidationException("Нельзя комментировать событие, в котором не участвовал");
        }
    }
}
