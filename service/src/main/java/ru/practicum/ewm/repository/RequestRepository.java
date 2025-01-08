package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.entity.Request;
import ru.practicum.ewm.entity.RequestStatus;

import java.util.Collection;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Collection<Request> findAllByRequesterId(long userId);

    // Проверяем есть ли такой запрос уже в базе
    boolean existsByRequesterIdAndEventId(long userId, long eventId);

    // Получаем количество запросов на участие в событии по Id;
    Collection<Request> findAllByEventIdAndStatus(long eventId, RequestStatus status);

    // Получаем запрос пользователя
    Optional<Request> findByIdAndRequesterId(long requestId, long userId);

    //
    @Query(value = """
                        SELECT r
                        FROM Request r
                        JOIN r.event e
                        WHERE e.initiator.id = :userId AND e.id = :eventId
            """)
    Collection<Request> getAllUserEventsRequests(long userId, long eventId);

    // Проверяем принимал ли участием пользователь в данном событии
//    @Query(value = """
//                        SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
//                        FROM Request r
//                        WHERE r.event.id = :eventId AND r.requester.id = :userId AND r.status = :status
//            """)
//    boolean checkTheUserWasInTheEvent(long userId, long eventId, RequestStatus status);

    boolean existsByRequesterIdAndEventIdAndStatus(long userId, long eventId, RequestStatus status);
}
