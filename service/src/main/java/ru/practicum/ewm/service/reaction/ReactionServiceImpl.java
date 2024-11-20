package ru.practicum.ewm.service.reaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.reaction.ReactionCreateDto;
import ru.practicum.ewm.dto.reaction.ReactionResponseDto;
import ru.practicum.ewm.dto.reaction.ReactionUpdateDto;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.EventState;
import ru.practicum.ewm.entity.Reaction;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.exception.ReactionNotFoundException;
import ru.practicum.ewm.exception.ReactionValidationException;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.mapper.ReactionMapper;
import ru.practicum.ewm.repository.ReactionRepository;
import ru.practicum.ewm.service.event.EventService;
import ru.practicum.ewm.service.user.UserService;

@Service("reactionServiceImpl")
@Slf4j
@Transactional
public class ReactionServiceImpl implements ReactionService {

    private final ReactionRepository reactionRepository;
    private final UserService userService;
    private final EventService eventService;

    @Autowired
    public ReactionServiceImpl(ReactionRepository reactionRepository, UserService userService, EventService eventService) {
        this.reactionRepository = reactionRepository;
        this.userService = userService;
        this.eventService = eventService;
    }


    // Добавляем новую реакцию в бд
    @Override
    public ReactionResponseDto createReaction(ReactionCreateDto dto) {
        log.info("Пытаюсь добавит новую реакцию пользователя id = {} на событие id = {}", dto.getUserId(), dto.getEventId());
        final User user = userService.getUserById(dto.getUserId());
        final Event event = eventService.getEventById(dto.getEventId());

        final Reaction reaction = reactionRepository.save(ReactionMapper.mapDtoToReaction(dto, event, user));
        log.info("Реакция пользователя id = {} на событие id = {} добавлена", dto.getUserId(), dto.getEventId());

        return ReactionMapper.mapReactionToDtoResponse(reaction);
    }


    // Обновляем реакцию пользователя
    @Override
    public ReactionResponseDto changeReaction(ReactionUpdateDto dto, long reactId) {
        log.info("Пытаюсь изменить реакцию с id = {} на значение = {}", reactId, dto.getReaction());

        final Reaction reaction = getReactionById(reactId);
        reaction.setReaction(dto.getReaction());
        reactionRepository.save(reaction);
        log.info("Реакция с id = {} на значение = {}", reactId, dto.getReaction());

        return ReactionMapper.mapReactionToDtoResponse(reaction);
    }


    // Удаляем реакцию пользователя
    @Override
    public void deleteReaction(long reactId, long userId) {
        log.info("Пытаюсь удалить реакцию с id = {} пользователя с id = {}", reactId, userId);
        checkReactionIsExist(reactId);
        userService.checkUserIsExist(userId);
        checkUserMadeThisReaction(reactId, userId);

        reactionRepository.deleteById(reactId);
        log.info("Реакция с id = {} пользователя с id = {} удалена", reactId, userId);
    }


    // Получаем реакцию по id
    @Transactional(readOnly = true)
    public Reaction getReactionById(long reactId) {
        log.info("Пытаюсь получить реакцию с id = {}", reactId);

        Reaction reaction = reactionRepository.findById(reactId).orElseThrow(() -> {
            log.warn("Реакция с id = {} не найдена", reactId);
            return new ReactionNotFoundException("String.format(\"Реакция с id = %d не найдена\", reactId)");
        });
        log.info("Реакция с id = {} получена", reactId);

        return reaction;
    }


    // Проверяем что именно этот пользователь поставил реакция
    public void checkUserMadeThisReaction(long reactId, long userId) {
        if (!reactionRepository.existsByIdAndUserId(reactId, userId)) {
            log.warn("Пользователь с id = {} не ставил реакцию с id = {}", userId, reactId);
            throw new ReactionValidationException(String.format("Пользователь с id = %d не ставил реакцию с id = %d",
                    userId, reactId));
        }
    }


    // Проверяем, что пользователь существует
    public void checkReactionIsExist(long id) {
        log.info("Проверяем существует ли реакция с id={}", id);
        if (reactionRepository.findById(id).isEmpty()) {
            throw new ReactionNotFoundException(String.format("Реакция с id=%d не найдена", id));
        }
        log.info("Реакция с id = {} существует.", id);
    }


}
