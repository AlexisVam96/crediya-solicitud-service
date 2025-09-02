package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.estado.gateways.EstadoRepository;
import co.com.crediya.model.exception.ErrorType;
import co.com.crediya.model.exception.LoanApplicationCustomerException;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.crediya.model.user.gateway.ExternalUserGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final EstadoRepository estadoRepository;
    private final ExternalUserGateway externalUserGateway;

    public Flux<Solicitud> getAllSolicitudes() {
        return solicitudRepository.findAll();
    }

    public Mono<Solicitud> createSolicitud(Solicitud solicitud) {
        return externalUserGateway.findByDocumentNumber(solicitud.getDocumentNumber())
                .switchIfEmpty(Mono.error(new LoanApplicationCustomerException("User's document number not found", ErrorType.NOT_FOUND)))
                .flatMap(user -> {
                    solicitud.setDocumentNumber(user.getDocumentNumber());
                    return tipoPrestamoRepository.findByIdTipoPrestamo(solicitud.getIdTipoPrestamo());
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid loan type")))
                .flatMap(tipoPrestamo ->
                        estadoRepository.findByNombre("Pendiente de revisión")
                )
                .switchIfEmpty(Mono.error(new IllegalStateException("Initial state not found")))
                .flatMap(estado -> {
                    solicitud.setIdEstado(estado.getIdEstado());
                    return solicitudRepository.save(solicitud);
                });
    }
}
