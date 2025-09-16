package co.com.crediya.sqs.sender;

import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.sqs.gateway.SqsSendEmailGateway;
import co.com.crediya.sqs.sender.config.SQSSenderProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements SqsSendEmailGateway {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;

    @Override
    public Mono<String> send(Solicitud solicitud) {
        return Mono.fromCallable(() -> buildRequest(solicitud))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.info("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(Solicitud solicitud) {
        String message = String.format("{\"idSolicitud\": \"%s\", \"documentNumber\": \"%s\", \"idEstado\": \"%s\"" +
                        ", \"nameUser\": \"%s\"}",
                solicitud.getIdSolicitud(), solicitud.getDocumentNumber(), solicitud.getIdEstado(), solicitud.getNameUser());
        return SendMessageRequest.builder()
                .queueUrl(properties.getQueueUrl())
                .messageBody(message)
                .build();
    }
}
