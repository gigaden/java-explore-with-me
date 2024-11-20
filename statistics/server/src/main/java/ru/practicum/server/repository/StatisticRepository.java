package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.StatisticDtoResponse;
import ru.practicum.server.entity.Statistic;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticRepository extends JpaRepository<Statistic, Long> {

    @Query(value = """
    SELECT new ru.practicum.dto.StatisticDtoResponse(
        s.app,
        s.uri,
        CASE WHEN :unique = true THEN COUNT(DISTINCT s.ip) ELSE COUNT(s.ip) END
    )
    FROM Statistic s
    WHERE (:uris IS NULL OR s.uri IN :uris)
      AND s.timestamp BETWEEN :start AND :end
    GROUP BY s.app, s.uri
    ORDER BY CASE WHEN :unique = true THEN COUNT(DISTINCT s.ip) ELSE COUNT(s.ip) END DESC
""")
    List<StatisticDtoResponse> findStatistics(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end,
                                              @Param("uris") List<String> uris,
                                              @Param("unique") boolean unique);


}