package ru.practicum.server.mapper;

import ru.practicum.dto.StatisticDtoCreate;
import ru.practicum.server.entity.Statistic;

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
}
