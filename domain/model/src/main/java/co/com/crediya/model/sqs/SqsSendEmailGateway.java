package co.com.crediya.model.sqs;

import co.com.crediya.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

public interface SqsSendEmailGateway {

    Mono<String> send(Solicitud solicitud);
}
