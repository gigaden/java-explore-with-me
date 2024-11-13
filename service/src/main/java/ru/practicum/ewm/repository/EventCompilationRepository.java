package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.entity.Compilation;
import ru.practicum.ewm.entity.EventCompilation;

public interface EventCompilationRepository extends JpaRepository<EventCompilation, Long> {

    void deleteAllByCompilationId(long compId);

}
