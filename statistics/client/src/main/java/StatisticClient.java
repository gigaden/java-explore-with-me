import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import ru.practicum.dto.StatisticDtoCreate;
import ru.practicum.dto.StatisticDtoResponse;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


public class StatisticClient {

    private final RestClient restClient;

    @Value("${statistic-server.url}")
    private String baseUrl;
    private final String postUri = "/hit";
    private final String getUri = "/stats";

    public StatisticClient() {
        restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }


    public StatisticDtoCreate createStatistic(StatisticDtoCreate statisticDtoCreate) {
        StatisticDtoCreate statistic = restClient.post()
                .uri(postUri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(statisticDtoCreate)
                .retrieve()
                .body(StatisticDtoCreate.class);

        return statistic;
    }

    public Collection<StatisticDtoResponse> getStatistics(LocalDateTime start,
                                                          LocalDateTime end,
                                                          List<String> uris,
                                                          boolean unique) {
        Collection<StatisticDtoResponse> statistics = restClient.get().uri(uriBuilder -> uriBuilder
                        .path(getUri)
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .header("Content-Type", "application/json")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        return statistics;
    }

}
