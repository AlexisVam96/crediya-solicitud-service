package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SolicitudUseCase {

    private final SolicitudRepository solicitudRepository;

    public Flux<Solicitud> getAllSolicitudes() {
        return solicitudRepository.findAll();
    }

    public Mono<Solicitud> saveSolicitud(Solicitud solicitud) {
        return solicitudRepository.save(solicitud);
    }

    public Mono<Solicitud> findSolicitudById(Integer id) {
        return solicitudRepository.findById(id);
    }
}
