package ru.practicum.ewm.service.reaction;

import ru.practicum.ewm.dto.reaction.ReactionCreateDto;
import ru.practicum.ewm.dto.reaction.ReactionResponseDto;

public interface ReactionService {

    ReactionResponseDto createReaction(ReactionCreateDto dto);
}
