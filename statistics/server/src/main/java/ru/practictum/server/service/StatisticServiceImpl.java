package ru.practictum.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practictum.server.entity.Statistic;
import ru.practictum.server.mapper.StatisticMapper;
import ru.practictum.server.repository.StatisticRepository;
import ru.practicum.dto.StatisticDtoCreate;
import ru.practicum.dto.StatisticDtoResponse;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service("statisticServiceImpl")
@Slf4j
public class StatisticServiceImpl implements StatisticService {

    private final StatisticRepository repository;

    @Autowired
    public StatisticServiceImpl(StatisticRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public StatisticDtoCreate create(StatisticDtoCreate statistic) {
        Statistic statisticDtoCreate = repository.save(StatisticMapper.mapToStatistic(statistic));
        return StatisticMapper.maToStatisticDtoCreate(statisticDtoCreate);
    }


    @Override
    @Transactional(readOnly = true)
    public Collection<StatisticDtoResponse> getAll(LocalDateTime start,
                                                   LocalDateTime end,
                                                   List<String> uri, boolean unique) {
        /* Если список с ури не передан, то делаем его специально налл для передачи в запрос репозитория
        именно нал и получения списка всех объектов */
        List<String> uriFilter = uri.isEmpty() ? null : uri;

        List<StatisticDtoResponse> statistics = repository
                .getAllStatisticDtoResponse(start, end, uriFilter, unique);

        return statistics;
    }
}
