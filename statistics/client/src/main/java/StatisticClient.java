import exception.ClientRequestException;
import exception.ServerRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.dto.StatisticDtoCreate;
import ru.practicum.dto.StatisticDtoResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;


@Slf4j
@Component
public class StatisticClient {

    private final RestClient restClient;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${statistic-server.url}")
    private String baseUrl;
    private final String postUri = "/hit";
    private final String getUri = "/stats";

    @Autowired
    public StatisticClient(@Value("${statistic-server.url}") String baseUrl) {
        restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }


    // Отправляем запрос на добавление статистики
    public StatisticDtoCreate createStatistic(StatisticDtoCreate statisticDtoCreate) {
        log.info("Пытаюсь отправить запрос на добавление новой статистики {}", statisticDtoCreate);
        StatisticDtoCreate statistic = restClient.post()
                .uri(postUri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(statisticDtoCreate)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new ClientRequestException(String.format("Ошибка клиента: %s %s", response.getStatusCode(),
                            response.getHeaders()));
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new ServerRequestException(String.format("Ошибка сервера: %s %s", response.getStatusCode(),
                            response.getHeaders()));
                })
                .body(StatisticDtoCreate.class);
        log.info("Запрос на добавление статистики отправлен {}.", statisticDtoCreate);

        return statistic;
    }

    // Отправляем запрос на получение статистики
    public Collection<StatisticDtoResponse> getStatistics(LocalDateTime start,
                                                          LocalDateTime end,
                                                          List<String> uris,
                                                          boolean unique) {
        log.info("Пытаюсь отправить запрос на получение статистики с параметрами: start={}, end={}, uri={}, unique={}",
                start, end, uris, unique);

        // Кодируем даты
        String formattedStart = URLEncoder.encode(start.format(formatter), StandardCharsets.UTF_8);
        String formattedEnd = URLEncoder.encode(end.format(formatter), StandardCharsets.UTF_8);

        Collection<StatisticDtoResponse> statistics = restClient.get().uri(uriBuilder -> uriBuilder
                        .path(getUri)
                        .queryParam("start", formattedStart)
                        .queryParam("end", formattedEnd)
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .header("Content-Type", "application/json")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new ClientRequestException(String.format("Ошибка клиента: %s %s", response.getStatusCode(),
                            response.getHeaders()));
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new ServerRequestException(String.format("Ошибка сервера: %s %s", response.getStatusCode(),
                            response.getHeaders()));
                })
                .body(new ParameterizedTypeReference<>() {
                });
        log.info("Получены записи статистики с параметрами: start={}, end={}, uri={}, unique={}",
                start, end, uris, unique);

        return statistics;
    }

    // Для проверки, что клиент работает
    public static void main(String[] args) {
        final StatisticClient client = new StatisticClient("http://localhost:9090");
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Collection<StatisticDtoResponse> result = client
                .getStatistics(LocalDateTime.parse("2022-09-06 11:00:23", formatter),
                        LocalDateTime.parse("2024-09-06 11:00:23", formatter), List.of(), false);

        System.out.println(result);

        StatisticDtoCreate statistic = client.createStatistic(StatisticDtoCreate.builder()
                .uri("/event")
                .app("main app")
                .ip("my.ip.address")
                .timestamp(LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter))
                .build());

        System.out.println(statistic);

    }

}
