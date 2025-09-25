package co.com.crediya.sqs.listener.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.metrics.LoggingMetricPublisher;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SQSConfigTest {

    /*
    @Mock
    private SqsAsyncClient asyncClient;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        // Stub the async method to avoid NullPointerException
        when(asyncClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(ReceiveMessageResponse.builder().build()));
    }

    @Test
    void configTest() {
        SQSProperties sqsProperties = new SQSProperties();
        sqsProperties.setNumberOfThreads(1);
        sqsProperties.setRegion("Region");
        LoggingMetricPublisher loggingMetricPublisher = LoggingMetricPublisher.create();
        SQSConfig sqsConfig = new SQSConfig();

        assertNotNull(sqsConfig.sqsListener(asyncClient, sqsProperties, message -> Mono.empty()));
        assertNotNull(sqsConfig.configSqs(sqsProperties, loggingMetricPublisher));

    }

     */
}