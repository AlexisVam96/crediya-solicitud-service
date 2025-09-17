package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.estado.gateways.EstadoRepository;
import co.com.crediya.model.exception.ErrorType;
import co.com.crediya.model.exception.LoanApplicationCustomerException;
import co.com.crediya.model.security.JwtAuthenticationGateway;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.sqs.DeptCapacityResponse;
import co.com.crediya.model.sqs.gateway.SqsSendDeptCapacityGateway;
import co.com.crediya.model.sqs.gateway.SqsSendEmailGateway;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.crediya.model.transaction.TransactionManager;
import co.com.crediya.model.user.User;
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

    private final JwtAuthenticationGateway jwtAuthenticationGateway;

    private final SqsSendEmailGateway sqsSendEmailGateway;

    private final SqsSendDeptCapacityGateway sqsSendDeptCapacityGateway;

    public Flux<Solicitud> getAllSolicitudes() {
        log.info("SolicitudUseCase.getAllSolicitudes: Starting getAllSolicitudes for solicitud");
        return transactionManager.doInTransaction(solicitudRepository.findAll());
    }

    public Mono<Solicitud> calculateDebtCapacity(Solicitud solicitud) {
        log.info("SolicitudUseCase.calculateDebtCapacity: Starting calculateDebtCapacity for solicitud " + solicitud);
        return transactionManager.doInTransaction(
                externalUserGateway.findByDocumentNumber(solicitud.getDocumentNumber())
                        .switchIfEmpty(Mono.error(new LoanApplicationCustomerException("User not found for document number", ErrorType.NOT_FOUND)))
                        .flatMap(user -> tipoPrestamoRepository.findByIdTipoPrestamo(solicitud.getIdTipoPrestamo())
                                .switchIfEmpty(Mono.error(new LoanApplicationCustomerException("Invalid loan type", ErrorType.VALIDATION)))
                                .filter(TipoPrestamo::getValidacionAutomatica)
                                .switchIfEmpty(Mono.error(new LoanApplicationCustomerException("The loan type does not allow automatic validation", ErrorType.VALIDATION)))
                                .flatMap(tipoPrestamo -> {
                                    solicitud.setIdEstado(1); // status in pending
                                    return solicitudRepository.save(solicitud)
                                            .map(savedSolicitud -> {
                                                savedSolicitud.setBaseSalary(user.getSalary());
                                                savedSolicitud.setInterestRate(tipoPrestamo.getTasaInteres());
                                                savedSolicitud.setNameUser(user.getFirstName() + " " + user.getLastName());
                                                return savedSolicitud;
                                            })
                                            .flatMap(savedSolicitud ->
                                                    sqsSendDeptCapacityGateway.send(savedSolicitud)
                                                            .thenReturn(savedSolicitud)
                                            );
                                })
                        )
        );
    }

    public Mono<Solicitud> handleDebtCapacityResponse(DeptCapacityResponse response) {
        return solicitudRepository.findByIdSolicitud(response.getIdSolicitud())
                .switchIfEmpty(Mono.error(new LoanApplicationCustomerException("Solicitud no encontrada", ErrorType.NOT_FOUND)))
                .flatMap(solicitud -> {
                    if ("APROBADO".equalsIgnoreCase(response.getResultado())) {
                        solicitud.setIdEstado(2); // aprobado
                    } else if ("RECHAZADO".equalsIgnoreCase(response.getResultado())) {
                        solicitud.setIdEstado(3); // rechazado
                    } else {
                        solicitud.setIdEstado(1); // pendiente
                    }
                    return solicitudRepository.save(solicitud);
                })
                .doOnSuccess(saved -> log.info("Solicitud actualizada con estado " + saved.getIdEstado()));
    }


    public Mono<Solicitud> updateSolicitud(Solicitud solicitud) {
        log.info("SolicitudUseCase.updateSolicitud: Starting updateSolicitud for solicitud " + solicitud);
        return transactionManager.doInTransaction(
                externalUserGateway.findByDocumentNumber(solicitud.getDocumentNumber())
                        .switchIfEmpty(Mono.error(new LoanApplicationCustomerException("User not found for document number", ErrorType.NOT_FOUND)))
                        .flatMap(user ->
                                solicitudRepository.findByIdSolicitud(solicitud.getIdSolicitud())
                                        .switchIfEmpty(Mono.error(new LoanApplicationCustomerException("Loan application not found", ErrorType.NOT_FOUND)))
                                        .map(existingSolicitud -> {
                                            existingSolicitud.setIdEstado(solicitud.getIdEstado());
                                            existingSolicitud.setDocumentNumber(user.getDocumentNumber());
                                            return existingSolicitud;
                                        })
                                        .flatMap(solicitudRepository::save)
                                        .map(savedSolicitud -> {
                                            savedSolicitud.setNameUser(user.getFirstName() + " " + user.getLastName());
                                            savedSolicitud.setBaseSalary(user.getSalary());
                                            return savedSolicitud;
                                        })
                                        .flatMap(savedSolicitud ->
                                                sqsSendEmailGateway.send(savedSolicitud)
                                                        .thenReturn(savedSolicitud)
                                        )
                        )
        );
    }

    public Flux<Solicitud> getLoanApplicationByStatus(Integer page, Integer size, String idEstado) {
        log.info("SolicitudUseCase.getSolicitudesByEstado: Starting getLoanApplicationByStatus for status " + idEstado);
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
