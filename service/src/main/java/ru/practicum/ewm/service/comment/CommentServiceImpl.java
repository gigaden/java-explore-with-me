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
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.service.event.EventService;

import java.util.Collection;

@Service("commentServiceImpl")
@Slf4j
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final EventService eventService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public CommentServiceImpl(EventService eventService) {
        this.eventService = eventService;
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
    public CommentResponseDto addCommentToEvent(CommentCreateDto dto) {
        return null;
    }
}
