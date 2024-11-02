package ru.practictum.server.mapper;

import ru.practictum.server.entity.Statistic;
import ru.practicum.dto.StatisticDtoCreate;
import ru.practicum.dto.StatisticDtoResponse;

public class StatisticMapper {

    public static Statistic mapToStatistic(StatisticDtoCreate dto) {
        return Statistic.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(dto.getTimestamp())
                .build();
    }

    public static StatisticDtoCreate maToStatisticDtoCreate(Statistic stat) {
        return StatisticDtoCreate.builder()
                .app(stat.getApp())
                .uri(stat.getUri())
                .ip(stat.getIp())
                .timestamp(stat.getTimestamp())
                .build();
    }

    public static StatisticDtoResponse maToStatisticDtoResponse(Statistic stat) {
        return StatisticDtoResponse.builder()
                .app(stat.getApp())
                .uri(stat.getUri())
                .hits(1)
                .build();
    }
}
