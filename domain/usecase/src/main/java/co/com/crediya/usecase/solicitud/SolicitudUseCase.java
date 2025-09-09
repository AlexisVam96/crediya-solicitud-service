package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.estado.gateways.EstadoRepository;
import co.com.crediya.model.exception.ErrorType;
import co.com.crediya.model.exception.LoanApplicationCustomerException;
import co.com.crediya.model.security.JwtAuthenticationGateway;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.crediya.model.transaction.TransactionManager;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateway.ExternalUserGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class SolicitudUseCase {

    private static final Logger log = Logger.getLogger(SolicitudUseCase.class.getName());

    private final SolicitudRepository solicitudRepository;

    private final TipoPrestamoRepository tipoPrestamoRepository;

    private final EstadoRepository estadoRepository;

    private final TransactionManager transactionManager;

    private final ExternalUserGateway externalUserGateway;

    private final JwtAuthenticationGateway jwtAuthenticationGateway;

    public Flux<Solicitud> getAllSolicitudes() {
        log.info("SolicitudUseCase.getAllSolicitudes: Starting getAllSolicitudes for solicitud");
        return transactionManager.doInTransaction(solicitudRepository.findAll());
    }

    public Flux<Solicitud> getLoanApplicationByStatus(Integer page, Integer size, String idEstado) {
        log.info("SolicitudUseCase.getSolicitudesByEstado: Starting getLoanApplicationByStatus for status " + idEstado);
        if(size == null || size <= 0) size = 10;
        if(page == null || page < 0) page = 0;
        return transactionManager.doInTransaction(
                solicitudRepository.findByIdEstado(page, size, idEstado)
                        .flatMap(solicitud ->
                                Mono.zip(
                                        externalUserGateway.findByDocumentNumber(solicitud.getDocumentNumber()),
                                        tipoPrestamoRepository.findByIdTipoPrestamo(solicitud.getIdTipoPrestamo())
                                ).map(tuple -> {
                                    User user = tuple.getT1();
                                    TipoPrestamo tipoPrestamo = tuple.getT2();
                                    solicitud.setNameUser(user.getFirstName() + " " + user.getLastName());
                                    solicitud.setBaseSalary(user.getSalary());
                                    solicitud.setInterestRate(tipoPrestamo.getTasaInteres());
                                    return solicitud;
                                })
                        )
        );
    }

    public Mono<Solicitud> createSolicitud(Solicitud solicitud) {
        log.info("SolicitudUseCase.createSolicitud: Starting createSolicitud for solicitud " + solicitud);


        return jwtAuthenticationGateway.getCurrentEmail()
                .flatMap(email -> {
                    if (!email.equals(solicitud.getEmail())) {
                        return Mono.error(new LoanApplicationCustomerException("The email token is different from loan application email", ErrorType.VALIDATION));
                    }
                    // Solo si el token es válido, inicia la transacción
                    return transactionManager.doInTransaction(
                            externalUserGateway.findByDocumentNumber(solicitud.getDocumentNumber())
                                .switchIfEmpty(Mono.error(new LoanApplicationCustomerException("User not found for document number", ErrorType.NOT_FOUND)))
                                .flatMap(user -> {
                                    solicitud.setDocumentNumber(user.getDocumentNumber());
                                    return tipoPrestamoRepository.findByIdTipoPrestamo(solicitud.getIdTipoPrestamo());
                                })
                                .switchIfEmpty(Mono.error(new LoanApplicationCustomerException("Invalid loan type", ErrorType.VALIDATION)))
                                .flatMap(tipoPrestamo -> estadoRepository.findByNombre("Pendiente de revisión"))
                                .switchIfEmpty(Mono.error(new LoanApplicationCustomerException("Initial state not found", ErrorType.VALIDATION)))
                                .flatMap(estado -> {
                                    solicitud.setIdEstado(estado.getIdEstado());
                                    return solicitudRepository.save(solicitud);
                                })
                    );
                });
    }
}
