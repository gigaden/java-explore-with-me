package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.entity.Reaction;

import java.util.List;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    boolean existsByIdAndUserId(long reactId, long userId);

    List<Reaction> findAllByEventId(long eventId);
}
