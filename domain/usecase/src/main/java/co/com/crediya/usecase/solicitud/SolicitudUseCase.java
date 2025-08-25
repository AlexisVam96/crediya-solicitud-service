package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.estado.gateways.EstadoRepository;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final EstadoRepository estadoRepository;

    public Flux<Solicitud> getAllSolicitudes() {
        return solicitudRepository.findAll();
    }

    public Mono<Solicitud> saveSolicitud(Solicitud solicitud) {
        return solicitudRepository.save(solicitud);
    }

    public Mono<Solicitud> findSolicitudById(Integer id) {
        return solicitudRepository.findById(id);
    }

    public Mono<Solicitud> createSolicitud(Solicitud solicitud) {
        // Validate loan type exists
        return tipoPrestamoRepository.findById(solicitud.getId_tipo_prestamo())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid loan type")))
                .flatMap(tipoPrestamo ->
                        // Set initial state to "Pendiente de revisión"
                        estadoRepository.findByNombre("Pendiente de revisión")
                )
                .switchIfEmpty(Mono.error(new IllegalStateException("Initial state not found")))
                .flatMap(estado -> {
                    solicitud.setId_estado(estado.getId_estado());
                    return solicitudRepository.save(solicitud);
                });
    }
}
