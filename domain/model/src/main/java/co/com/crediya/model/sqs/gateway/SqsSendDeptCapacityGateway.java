package co.com.crediya.model.sqs.gateway;

import co.com.crediya.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

public interface SqsSendDeptCapacityGateway {

    Mono<String> send(Solicitud solicitud);
}
