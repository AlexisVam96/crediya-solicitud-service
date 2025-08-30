package co.com.crediya.model.solicitud.gateways;

import co.com.crediya.model.solicitud.Solicitud;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SolicitudRepository {

    Flux<Solicitud> findAll();

    Mono<Solicitud> save(Solicitud solicitud);

}
