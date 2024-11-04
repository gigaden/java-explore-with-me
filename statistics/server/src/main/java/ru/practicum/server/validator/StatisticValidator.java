package ru.practicum.server.validator;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.server.exception.StatisticValidationDateException;
import ru.practicum.server.exception.StatisticValidationException;
import ru.practicum.dto.StatisticDtoCreate;

import java.time.LocalDateTime;

@Slf4j
public class StatisticValidator {

    // Проверяем поля перед добавление статистики
    public static void checkStatisticsParam(StatisticDtoCreate statistic) {

        if (statistic.getIp() == null || statistic.getIp().isEmpty()
                || statistic.getApp() == null || statistic.getApp().isEmpty() || statistic.getTimestamp() == null ||
                statistic.getUri() == null || statistic.getUri().isEmpty()) {
            log.warn("Не все обязательные параметры переданы: ip:{}, app:{}, timestamp:{}, uri:{}", statistic.getIp(),
                    statistic.getApp(), statistic.getTimestamp(), statistic.getUri());
            throw new StatisticValidationException(String.format("Не все обязательные параметры переданы: ip:%s, " +
                            "app:%s, timestamp:%s, uri:%s", statistic.getIp(), statistic.getApp(),
                    statistic.getTimestamp(), statistic.getUri()));
        }
        if (statistic.getTimestamp().isAfter(LocalDateTime.now())) {
            log.warn("Дата события не может быть в будущем {}", statistic.getTimestamp());
            throw new StatisticValidationDateException(String.format("Дата события не может быть в будущем %s",
                    statistic.getTimestamp()));
        }
    }

    // Проверяем даты начала и окончания выборки статистики
    public static void checkStatisticsDates(LocalDateTime start, LocalDateTime end) {

        if (start.isAfter(end)) {
            log.warn("Дата начала {} больше окончания {}", start, end);
            throw new StatisticValidationDateException(String.format("Дата начала %s больше окончания %s", start, end));
        } else if (start.isAfter(LocalDateTime.now())) {
            log.warn("Дата начала {} ещё не наступила", start);
            throw new StatisticValidationDateException(String.format("Дата начала %s, или окончания %s ещё не наступили",
                    start, end));
        }
    }
}
