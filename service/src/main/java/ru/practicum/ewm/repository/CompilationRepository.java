package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.entity.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query(value = """
                SELECT *
                FROM compilations c
                WHERE c.pinned = :pinned
                LIMIT :size OFFSET :from
            """, nativeQuery = true)
    List<Compilation> findCompilationsBetweenFromAndSize(Boolean pinned, int from, int size);
}
