package co.com.crediya.model.solicitud.gateways;

import co.com.crediya.model.solicitud.Solicitud;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SolicitudRepository {

    Flux<Solicitud> findAll();

    Flux<Solicitud> findByIdEstado(Integer page, Integer size, String idEstado);

    Mono<Solicitud> save(Solicitud solicitud);

    Mono<Solicitud> findByIdSolicitud(Integer idSolicitud);

}
