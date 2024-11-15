import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import ru.practicum.statistics.StatisticClient;

@ExtendWith(MockitoExtension.class)
class StatisticClientTest {

    @InjectMocks
    private StatisticClient client;

    @Mock
    private RestClient restClient;

    @Test
    void createStatistic() {

    }

    @Test
    void getStatistics() {
    }
}
