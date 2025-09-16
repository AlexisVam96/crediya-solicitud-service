package co.com.crediya.sqsdebtcapacity;

import co.com.crediya.model.exception.ErrorType;
import co.com.crediya.model.exception.LoanApplicationCustomerException;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.sqs.DeptCapacityResponse;
import co.com.crediya.model.sqs.gateway.SqsSendDeptCapacityGateway;
import co.com.crediya.model.sqs.gateway.SqsSendEmailGateway;
import co.com.crediya.sqsdebtcapacity.config.SQSSenderDeptCapacityProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.*;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSenderDeptCapacity implements SqsSendDeptCapacityGateway {
    private final SQSSenderDeptCapacityProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<String> send(Solicitud solicitud) {
        return Mono.fromCallable(() -> buildRequest(solicitud))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.info("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(Solicitud solicitud) {
        try {
            String message = objectMapper.writeValueAsString(solicitud);
            return SendMessageRequest.builder()
                    .queueUrl(properties.getQueueUrl())
                    .messageBody(message)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error serializing solicitud", e);
        }

    }

    @Override
    public Mono<DeptCapacityResponse> receive() {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(properties.getReplyQueueUrl())
                .maxNumberOfMessages(1)
                .waitTimeSeconds(5)
                .build();

        return Mono.fromFuture(client.receiveMessage(request))
                .flatMapMany(response -> Flux.fromIterable(response.messages()))
                .next()
                .switchIfEmpty(Mono.error(new LoanApplicationCustomerException(
                        "No messages available in SQS",
                        ErrorType.NOT_FOUND
                )))
                .flatMap(message ->
                        Mono.fromCallable(() -> objectMapper.readValue(message.body(), DeptCapacityResponse.class))
                                .doOnSuccess(body -> client.deleteMessage(DeleteMessageRequest.builder()
                                        .queueUrl(properties.getReplyQueueUrl())
                                        .receiptHandle(message.receiptHandle())
                                        .build()))
                )
                .onErrorMap(e -> new LoanApplicationCustomerException(
                        "Error parsing message body: " + e.getMessage(),
                        ErrorType.SYSTEM
                ));
    }
}
