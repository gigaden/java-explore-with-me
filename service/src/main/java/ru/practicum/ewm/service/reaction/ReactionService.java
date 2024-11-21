package ru.practicum.ewm.service.reaction;

import ru.practicum.ewm.dto.reaction.ReactionCreateDto;
import ru.practicum.ewm.dto.reaction.ReactionResponseDto;
import ru.practicum.ewm.dto.reaction.ReactionUpdateDto;

public interface ReactionService {

    ReactionResponseDto createReaction(ReactionCreateDto dto);

    ReactionResponseDto changeReaction(ReactionUpdateDto dto, long userId, long reactId);

    void deleteReaction(long reactId, long userId);
}
