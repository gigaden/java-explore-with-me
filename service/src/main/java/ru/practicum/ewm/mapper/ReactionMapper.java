package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.reaction.ReactionCreateDto;
import ru.practicum.ewm.dto.reaction.ReactionResponseDto;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.Reaction;
import ru.practicum.ewm.entity.User;

import java.time.LocalDateTime;

public class ReactionMapper {

    public static Reaction mapDtoToReaction(ReactionCreateDto dto,
                                            Event event,
                                            User user) {
        return Reaction.builder()
                .event(event)
                .user(user)
                .reaction(dto.getReaction())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static ReactionResponseDto mapReactionToDtoResponse(Reaction reaction) {
        return ReactionResponseDto.builder()
                .id(reaction.getId())
                .user(reaction.getUser().getId())
                .event(reaction.getEvent().getId())
                .createdAt(reaction.getCreatedAt())
                .build();
    }
}
