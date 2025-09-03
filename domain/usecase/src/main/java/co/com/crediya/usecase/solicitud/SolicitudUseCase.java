package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.estado.gateways.EstadoRepository;
import co.com.crediya.model.exception.ErrorType;
import co.com.crediya.model.exception.LoanApplicationCustomerException;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.crediya.model.transaction.TransactionManager;
import co.com.crediya.model.user.gateway.ExternalUserGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class SolicitudUseCase {

    private static final Logger log = Logger.getLogger(SolicitudUseCase.class.getName());

    private final SolicitudRepository solicitudRepository;

    private final TipoPrestamoRepository tipoPrestamoRepository;

    private final EstadoRepository estadoRepository;

    private final TransactionManager transactionManager;

    private final ExternalUserGateway externalUserGateway;

    public Flux<Solicitud> getAllSolicitudes() {
        log.info("SolicitudUseCase.getAllSolicitudes: Starting getAllSolicitudes for solicitud");
        return transactionManager.doInTransaction(solicitudRepository.findAll());
    }

    public Mono<Solicitud> createSolicitud(Solicitud solicitud) {
        log.info("SolicitudUseCase.createSolicitud: Starting createSolicitud for solicitud " + solicitud);
        return transactionManager.doInTransaction(externalUserGateway.findByDocumentNumber(solicitud.getDocumentNumber())
                .switchIfEmpty(Mono.error(new LoanApplicationCustomerException("User not found for document number", ErrorType.NOT_FOUND)))
                .flatMap(user -> {
                    solicitud.setDocumentNumber(user.getDocumentNumber());
                    return tipoPrestamoRepository.findByIdTipoPrestamo(solicitud.getIdTipoPrestamo());
                })
                .switchIfEmpty(Mono.error(new LoanApplicationCustomerException("Invalid loan type", ErrorType.VALIDATION)))
                .flatMap(tipoPrestamo ->
                        estadoRepository.findByNombre("Pendiente de revisión")
                )
                .switchIfEmpty(Mono.error(new LoanApplicationCustomerException("Initial state not found", ErrorType.VALIDATION)))
                .flatMap(estado -> {
                    solicitud.setIdEstado(estado.getIdEstado());
                    return solicitudRepository.save(solicitud);
                }));
    }
}
