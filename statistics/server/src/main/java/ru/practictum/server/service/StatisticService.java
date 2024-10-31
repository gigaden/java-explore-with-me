package ru.practictum.server.service;

import ru.practicum.dto.StatisticDtoCreate;
import ru.practicum.dto.StatisticDtoResponse;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatisticService {

    StatisticDtoCreate create(StatisticDtoCreate statistic);

    Collection<StatisticDtoResponse> getAll(LocalDateTime start, LocalDateTime end,
                                            List<String> uri, boolean unique);
}
