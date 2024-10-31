package ru.practictum.server.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.StatisticDtoCreate;
import ru.practicum.dto.StatisticDtoResponse;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service("statisticServiceImpl")
@Slf4j
public class StatisticServiceImpl implements StatisticService {

    @Override
    @Transactional
    public StatisticDtoCreate create(StatisticDtoCreate statistic) {
        return null;
    }


    @Override
    @Transactional(readOnly = true)
    public Collection<StatisticDtoResponse> getAll(LocalDateTime start, LocalDateTime end,
                                                   List<String> uri, boolean unique) {
        return List.of();
    }
}
