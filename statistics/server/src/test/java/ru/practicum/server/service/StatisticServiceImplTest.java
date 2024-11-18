package ru.practicum.server.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.StatisticDtoCreate;
import ru.practicum.dto.StatisticDtoResponse;
import ru.practicum.server.entity.Statistic;
import ru.practicum.server.exception.StatisticValidationDateException;
import ru.practicum.server.exception.StatisticValidationException;
import ru.practicum.server.repository.StatisticRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class StatisticServiceImplTest {

    @Mock
    StatisticRepository repository;

    @InjectMocks
    StatisticServiceImpl service;

    @Test
    void createWhenStatisticDtoIsValidThenCreated() {

        LocalDateTime timestamp = LocalDateTime.now();
        Statistic statistic = Statistic.builder()
                .ip("ip")
                .uri("uri")
                .app("app")
                .timestamp(timestamp)
                .build();

        Mockito
                .when(repository.save(any(Statistic.class)))
                .thenReturn(statistic);

        StatisticDtoCreate result = service.create(StatisticDtoCreate.builder()
                .ip("ip")
                .uri("uri")
                .app("app")
                .timestamp(timestamp)
                .build());

        Mockito.verify(repository, times(1)).save(any(Statistic.class));
        assertEquals(statistic.getIp(), result.getIp());
    }

    @Test
    void createWhenStatisticDtoIsNotValidThenThrowedStatisticValidationException() {

        assertThrows(StatisticValidationException.class, () -> service.create(StatisticDtoCreate.builder()
                .ip("ip")
                .app("app")
                .build()));

        Mockito.verify(repository, times(0)).save(any(Statistic.class));

    }

    @Test
    void getAllWhenDatesIsValidThenReturnedCollection() {

        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        StatisticDtoResponse statistics = StatisticDtoResponse
                .builder()
                .uri("uri")
                .app("app")
                .hits(1)
                .build();

        Mockito
                .when(repository.findStatistics(start, end, List.of(" "), false))
                .thenReturn(List.of(statistics));

        Collection<StatisticDtoResponse> result = service.getAll(start, end, List.of(" "), false);

        Mockito.verify(repository, times(1))
                .findStatistics(start, end, List.of(" "), false);

        assertEquals(1, result.size());
    }

    @Test
    void getAllWhenDatesIsNotValidThenThrowedStatisticValidationDateException() {

        LocalDateTime end = LocalDateTime.now().minusDays(1);
        LocalDateTime start = LocalDateTime.now();
        assertThrows(StatisticValidationDateException.class, () -> service.getAll(start, end, List.of(""), false));

        Mockito.verify(repository, times(0)).save(any(Statistic.class));
    }
}