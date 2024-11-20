package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.reaction.ReactionCreateDto;
import ru.practicum.ewm.dto.reaction.ReactionResponseDto;
import ru.practicum.ewm.dto.reaction.ReactionUpdateDto;
import ru.practicum.ewm.service.reaction.ReactionService;

@RestController
@RequestMapping("/reactions")
public class ReactionController {

    @Qualifier("reactionServiceImpl")
    ReactionService reactionService;

    public ReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    @PostMapping
    public ResponseEntity<ReactionResponseDto> createReaction(@Valid @RequestBody ReactionCreateDto dto) {

        return new ResponseEntity<>(reactionService.createReaction(dto), HttpStatus.CREATED);
    }

    @PatchMapping("/{reactId}")
    public ResponseEntity<ReactionResponseDto> changeReaction(@Valid @RequestBody ReactionUpdateDto dto,
                                                              @PathVariable long reactId) {

        return new ResponseEntity<>(reactionService.changeReaction(dto, reactId), HttpStatus.OK);
    }

    @DeleteMapping("/{reactId}/{userId}")
    public ResponseEntity<String> deleteReaction(@PathVariable long reactId,
                                                              @PathVariable long userId) {

        reactionService.deleteReaction(reactId, userId);

        return new ResponseEntity<>("Реакция удалена", HttpStatus.NO_CONTENT);
    }
}
