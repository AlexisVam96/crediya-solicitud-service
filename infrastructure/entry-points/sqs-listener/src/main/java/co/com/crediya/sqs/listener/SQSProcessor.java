package co.com.crediya.sqs.listener;

import co.com.crediya.model.sqs.DeptCapacityResponse;
import co.com.crediya.usecase.solicitud.SolicitudUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SolicitudUseCase solicitudUseCase;

    @Override
    public Mono<Void> apply(Message message) {
        return Mono.fromCallable(() -> objectMapper.readValue(message.body(), DeptCapacityResponse.class))
                .flatMap(response -> {
                    log.info("📩 Mensaje recibido de SQS: {}", response);
                    // call use case to process the response
                    return solicitudUseCase.handleDebtCapacityResponse(response);
                })
                .doOnError(e -> log.error("Error procesando mensaje SQS: {}", e.getMessage(), e))
                .then();
    }
}
