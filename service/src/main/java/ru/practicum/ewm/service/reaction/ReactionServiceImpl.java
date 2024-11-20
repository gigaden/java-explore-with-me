package ru.practicum.ewm.service.reaction;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.reaction.ReactionCreateDto;
import ru.practicum.ewm.dto.reaction.ReactionResponseDto;

@Service("reactionServiceImpl")
public class ReactionServiceImpl implements ReactionService {

    @Override
    public ReactionResponseDto createReaction(ReactionCreateDto dto) {
        return null;
    }
}
