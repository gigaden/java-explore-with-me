package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.User;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = """
                SELECT *
                FROM events e
                WHERE e.initiator = ?3
                LIMIT ?2 OFFSET ?1
            """, nativeQuery = true)
    List<Event> findUserEventsBetweenFromAndSize(int from, int size, long userId);

    Optional<Event> findEventByIdAndInitiatorId(long eventId, long userId);
}
