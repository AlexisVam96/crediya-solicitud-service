package co.com.crediya.model.sqs.gateway;

import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.sqs.DeptCapacityResponse;
import reactor.core.publisher.Mono;

public interface SqsSendDeptCapacityGateway {

    Mono<String> send(Solicitud solicitud);

    Mono<DeptCapacityResponse> receive();
}
