package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.server.entity.Statistic;
import ru.practicum.dto.StatisticDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticRepository extends JpaRepository<Statistic, Long> {

    @Query(value = """
            SELECT new ru.practicum.dto.StatisticDtoResponse(s.app, s.uri,
            CASE WHEN ?4 = true THEN COUNT(DISTINCT s.ip) ELSE COUNT(s.ip) END
            )
            FROM Statistic s
            WHERE timestamp BETWEEN ?1 AND ?2
            AND (?3 IS NULL OR s.uri IN ?3)
            GROUP BY s.uri, s.app
            """)
    List<StatisticDtoResponse> getAllStatisticDtoResponse(LocalDateTime start,
                                                          LocalDateTime end,
                                                          List<String> uri, boolean unique);


}