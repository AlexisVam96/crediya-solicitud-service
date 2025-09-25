package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.estado.gateways.EstadoRepository;
import co.com.crediya.model.exception.ErrorType;
import co.com.crediya.model.exception.LoanApplicationCustomerException;
import co.com.crediya.model.security.JwtAuthenticationGateway;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.sqs.DeptCapacityResponse;
import co.com.crediya.model.sqs.gateway.SqsSendDeptCapacityGateway;
import co.com.crediya.model.sqs.gateway.SqsSendEmailGateway;
import co.com.crediya.model.sqs.gateway.SqsSendEstadoSolicitudGateway;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.crediya.model.transaction.TransactionManager;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateway.ExternalUserGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SolicitudUseCaseTest {

    @InjectMocks
    private SolicitudUseCase solicitudUseCase;

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private TransactionManager transactionManager;

    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;

    @Mock
    private EstadoRepository estadoRepository;

    @Mock
    private ExternalUserGateway externalUserGateway;

    @Mock
    private JwtAuthenticationGateway jwtAuthenticationGateway;

    @Mock
    private SqsSendEmailGateway sqsSendEmailGateway;

    @Mock
    private SqsSendDeptCapacityGateway sqsSendDeptCapacityGateway;

    @Mock
    private SqsSendEstadoSolicitudGateway sqsSendEstadoSolicitudGateway;


    private Solicitud solicitud;
    private User user;
    private TipoPrestamo tipoPrestamo;
    private Estado estado;

    @BeforeEach
    void setUp() {
        solicitud = new Solicitud();
        solicitud.setIdSolicitud(1);
        solicitud.setDocumentNumber("123");
        solicitud.setEmail("test@gmail.com");
        solicitud.setMonto(BigDecimal.valueOf(1000));
        solicitud.setPlazo(12);
        solicitud.setIdTipoPrestamo(1);
        solicitud.setIdEstado(1);

        user = new User();
        user.setDocumentNumber("123");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setSalary(BigDecimal.valueOf(5000));

        tipoPrestamo = new TipoPrestamo();
        tipoPrestamo.setTasaInteres(BigDecimal.valueOf(0.1));
        tipoPrestamo.setValidacionAutomatica(true);

        estado = new Estado();
        estado.setIdEstado(1);
    }

    @Test
    void calculateDebtCapacity_success() {
        when(transactionManager.doInTransaction(any(Mono.class))).thenAnswer(i -> i.getArgument(0));
        when(externalUserGateway.findByDocumentNumber(anyString())).thenReturn(Mono.just(user));
        when(tipoPrestamoRepository.findByIdTipoPrestamo(anyInt())).thenReturn(Mono.just(tipoPrestamo));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(Mono.just(solicitud));
        when(sqsSendDeptCapacityGateway.send(any(Solicitud.class))).thenReturn(Mono.empty());

        StepVerifier.create(solicitudUseCase.calculateDebtCapacity(solicitud))
                .expectNextMatches(s -> s.getBaseSalary().equals(user.getSalary()) && s.getInterestRate().equals(tipoPrestamo.getTasaInteres()))
                .verifyComplete();
    }

    @Test
    void calculateDebtCapacity_userNotFound() {
        when(transactionManager.doInTransaction(any(Mono.class))).thenAnswer(i -> i.getArgument(0));
        when(externalUserGateway.findByDocumentNumber(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(solicitudUseCase.calculateDebtCapacity(solicitud))
                .expectErrorMatches(e -> e instanceof LoanApplicationCustomerException && ((LoanApplicationCustomerException) e).getType() == ErrorType.NOT_FOUND)
                .verify();
    }

    @Test
    void calculateDebtCapacity_invalidLoanType() {
        when(transactionManager.doInTransaction(any(Mono.class))).thenAnswer(i -> i.getArgument(0));
        when(externalUserGateway.findByDocumentNumber(anyString())).thenReturn(Mono.just(user));
        when(tipoPrestamoRepository.findByIdTipoPrestamo(anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(solicitudUseCase.calculateDebtCapacity(solicitud))
                .expectErrorMatches(e -> e instanceof LoanApplicationCustomerException && ((LoanApplicationCustomerException) e).getType() == ErrorType.VALIDATION)
                .verify();
    }

    @Test
    void calculateDebtCapacity_noAutomaticValidation() {
        tipoPrestamo.setValidacionAutomatica(false);
        when(transactionManager.doInTransaction(any(Mono.class))).thenAnswer(i -> i.getArgument(0));
        when(externalUserGateway.findByDocumentNumber(anyString())).thenReturn(Mono.just(user));
        when(tipoPrestamoRepository.findByIdTipoPrestamo(anyInt())).thenReturn(Mono.just(tipoPrestamo));

        StepVerifier.create(solicitudUseCase.calculateDebtCapacity(solicitud))
                .expectErrorMatches(e -> e instanceof LoanApplicationCustomerException && ((LoanApplicationCustomerException) e).getType() == ErrorType.VALIDATION)
                .verify();
    }

    @Test
    void handleDebtCapacityResponse_aprobado() {
        DeptCapacityResponse response = new DeptCapacityResponse();
        response.setIdSolicitud(1);
        response.setResultado("APROBADO");
        when(solicitudRepository.findByIdSolicitud(1)).thenReturn(Mono.just(solicitud));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(Mono.just(solicitud));
        when(sqsSendEstadoSolicitudGateway.send(any(Solicitud.class))).thenReturn(Mono.empty());

        StepVerifier.create(solicitudUseCase.handleDebtCapacityResponse(response))
                .expectNext(solicitud)
                .verifyComplete();
    }

    @Test
    void handleDebtCapacityResponse_rechazado() {
        DeptCapacityResponse response = new DeptCapacityResponse();
        response.setIdSolicitud(1);
        response.setResultado("RECHAZADO");
        when(solicitudRepository.findByIdSolicitud(1)).thenReturn(Mono.just(solicitud));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(Mono.just(solicitud));

        StepVerifier.create(solicitudUseCase.handleDebtCapacityResponse(response))
                .expectNext(solicitud)
                .verifyComplete();
    }

    @Test
    void handleDebtCapacityResponse_notFound() {
        DeptCapacityResponse response = new DeptCapacityResponse();
        response.setIdSolicitud(1);
        when(solicitudRepository.findByIdSolicitud(1)).thenReturn(Mono.empty());

        StepVerifier.create(solicitudUseCase.handleDebtCapacityResponse(response))
                .expectErrorMatches(e -> e instanceof LoanApplicationCustomerException && ((LoanApplicationCustomerException) e).getType() == ErrorType.NOT_FOUND)
                .verify();
    }

    @Test
    void handleDebtCapacityResponse_pendiente() {
        DeptCapacityResponse response = new DeptCapacityResponse();
        response.setIdSolicitud(1);
        response.setResultado("OTRO");
        when(solicitudRepository.findByIdSolicitud(1)).thenReturn(Mono.just(solicitud));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(Mono.just(solicitud));

        StepVerifier.create(solicitudUseCase.handleDebtCapacityResponse(response))
                .expectNext(solicitud)
                .verifyComplete();
    }

    @Test
    void updateSolicitud_success() {
        when(transactionManager.doInTransaction(any(Mono.class))).thenAnswer(i -> i.getArgument(0));
        when(externalUserGateway.findByDocumentNumber(anyString())).thenReturn(Mono.just(user));
        when(solicitudRepository.findByIdSolicitud(anyInt())).thenReturn(Mono.just(solicitud));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(Mono.just(solicitud));
        when(sqsSendEmailGateway.send(any(Solicitud.class))).thenReturn(Mono.empty());

        StepVerifier.create(solicitudUseCase.updateSolicitud(solicitud))
                .expectNextMatches(s -> s.getNameUser().equals("Test User") && s.getBaseSalary().equals(user.getSalary()))
                .verifyComplete();
    }

    @Test
    void updateSolicitud_userNotFound() {
        when(transactionManager.doInTransaction(any(Mono.class))).thenAnswer(i -> i.getArgument(0));
        when(externalUserGateway.findByDocumentNumber(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(solicitudUseCase.updateSolicitud(solicitud))
                .expectErrorMatches(e -> e instanceof LoanApplicationCustomerException && ((LoanApplicationCustomerException) e).getType() == ErrorType.NOT_FOUND)
                .verify();
    }

    @Test
    void updateSolicitud_solicitudNotFound() {
        when(transactionManager.doInTransaction(any(Mono.class))).thenAnswer(i -> i.getArgument(0));
        when(externalUserGateway.findByDocumentNumber(anyString())).thenReturn(Mono.just(user));
        when(solicitudRepository.findByIdSolicitud(anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(solicitudUseCase.updateSolicitud(solicitud))
                .expectErrorMatches(e -> e instanceof LoanApplicationCustomerException && ((LoanApplicationCustomerException) e).getType() == ErrorType.NOT_FOUND)
                .verify();
    }

    @Test
    void getLoanApplicationByStatus_success() {
        when(solicitudRepository.findByIdEstado(anyInt(), anyInt(), anyString())).thenReturn(Flux.just(solicitud));
        when(transactionManager.doInTransaction(any(Flux.class))).thenAnswer(i -> i.getArgument(0));
        when(externalUserGateway.findByDocumentNumber(anyString())).thenReturn(Mono.just(user));
        when(tipoPrestamoRepository.findByIdTipoPrestamo(anyInt())).thenReturn(Mono.just(tipoPrestamo));

        StepVerifier.create(solicitudUseCase.getLoanApplicationByStatus(0, 10, "1"))
                .expectNextMatches(s -> s.getNameUser().equals("Test User") && s.getBaseSalary().equals(user.getSalary()))
                .verifyComplete();
    }

    @Test
    void createSolicitud_emailMismatch() {
        when(jwtAuthenticationGateway.getCurrentEmail()).thenReturn(Mono.just("other@gmail.com"));

        StepVerifier.create(solicitudUseCase.createSolicitud(solicitud))
                .expectErrorMatches(e -> e instanceof LoanApplicationCustomerException && ((LoanApplicationCustomerException) e).getType() == ErrorType.VALIDATION)
                .verify();
    }

    @Test
    void createSolicitud_invalidLoanType() {
        when(jwtAuthenticationGateway.getCurrentEmail()).thenReturn(Mono.just("test@gmail.com"));
        when(transactionManager.doInTransaction(any(Mono.class))).thenAnswer(i -> i.getArgument(0));
        when(externalUserGateway.findByDocumentNumber(anyString())).thenReturn(Mono.just(user));
        when(tipoPrestamoRepository.findByIdTipoPrestamo(anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(solicitudUseCase.createSolicitud(solicitud))
                .expectErrorMatches(e -> e instanceof LoanApplicationCustomerException && ((LoanApplicationCustomerException) e).getType() == ErrorType.VALIDATION)
                .verify();
    }

    @Test
    void createSolicitud_estadoNotFound() {
        when(jwtAuthenticationGateway.getCurrentEmail()).thenReturn(Mono.just("test@gmail.com"));
        when(transactionManager.doInTransaction(any(Mono.class))).thenAnswer(i -> i.getArgument(0));
        when(externalUserGateway.findByDocumentNumber(anyString())).thenReturn(Mono.just(user));
        when(tipoPrestamoRepository.findByIdTipoPrestamo(anyInt())).thenReturn(Mono.just(tipoPrestamo));
        when(estadoRepository.findByNombre(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(solicitudUseCase.createSolicitud(solicitud))
                .expectErrorMatches(e -> e instanceof LoanApplicationCustomerException && ((LoanApplicationCustomerException) e).getType() == ErrorType.VALIDATION)
                .verify();
    }

    @Test
    void mustfindAllSolicitudSuccessfully() {
        when(solicitudRepository.findAll()).thenReturn(Flux.just(solicitud));
        when(transactionManager.doInTransaction(any(Flux.class))).thenReturn(Flux.just(solicitud));

        StepVerifier.create(solicitudUseCase.getAllSolicitudes())
                .expectNext(solicitud)
                .verifyComplete();
    }

    @Test
    void mustSaveSolicitudSuccessfully() {
        when(tipoPrestamoRepository.findByIdTipoPrestamo(solicitud.getIdTipoPrestamo())).thenReturn(Mono.just(new TipoPrestamo()));
        when(estadoRepository.findByNombre(anyString())).thenReturn(Mono.just(new Estado()));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(Mono.just(solicitud));
        when(transactionManager.doInTransaction(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(externalUserGateway.findByDocumentNumber(solicitud.getDocumentNumber())).thenReturn(Mono.just(new User()));
        when(jwtAuthenticationGateway.getCurrentEmail()).thenReturn(Mono.just("test@gmail.com"));

        StepVerifier.create(solicitudUseCase.createSolicitud(solicitud))
                .expectNext(solicitud)
                .verifyComplete();
    }
}
