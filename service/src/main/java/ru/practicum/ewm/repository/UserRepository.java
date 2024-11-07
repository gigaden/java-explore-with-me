package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.entity.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findUsersByIdIn(List<Long> ids);

    @Query(value = """
                SELECT *
                FROM users u
                LIMIT ?1 OFFSET ?2
            """, nativeQuery = true)
    List<User> findUsersInterval(int size, int from);
}
