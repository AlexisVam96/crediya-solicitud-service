package co.com.crediya.consumer.conf;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import co.com.crediya.consumer.config.RestConsumerConfig;

import static org.assertj.core.api.Assertions.assertThat;

class RestConsumerConfigTest {

    @Test
    void webClientShouldBeConfiguredCorrectly() {
        WebClient.Builder builder = WebClient.builder();
        RestConsumerConfig restConsumerConfig = new RestConsumerConfig("http://localhost:8080", 5000);
        WebClient client = restConsumerConfig.getWebClient(builder);
        assertThat(client).isNotNull();
    }

}