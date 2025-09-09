package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.estado.gateways.EstadoRepository;
import co.com.crediya.model.security.JwtAuthenticationGateway;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
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
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
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

    private Solicitud solicitud;

    @BeforeEach
    void setUp() {
        solicitud = new Solicitud();
        solicitud.setIdSolicitud(1);
        solicitud.setDocumentNumber("76543210");
        solicitud.setEmail("test@gmail.com");
        solicitud.setMonto(new BigDecimal(12000.0));
        solicitud.setPlazo(12);
        solicitud.setIdTipoPrestamo(1);
        solicitud.setIdEstado(1);
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
        when(estadoRepository.findByNombre("Pendiente de revisión")).thenReturn(Mono.just(new Estado()));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(Mono.just(solicitud));
        when(transactionManager.doInTransaction(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(externalUserGateway.findByDocumentNumber(solicitud.getDocumentNumber())).thenReturn(Mono.just(new User()));
        when(jwtAuthenticationGateway.getCurrentEmail()).thenReturn(Mono.just("test@gmail.com"));

        StepVerifier.create(solicitudUseCase.createSolicitud(solicitud))
                .expectNext(solicitud)
                .verifyComplete();
    }


/*

    @Test
    void mustFailToSaveWhenRequiredFieldsAreMissing() {
        user.setFirstName(""); // or set to null to test missing field

        StepVerifier.create(userUseCase.save(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserCustomException &&
                                throwable.getMessage().equals("Required fields must not be null or empty"))
                .verify();
    }

    @Test
    void mustFailToSaveWhenEmailFormatAreMissing() {
        user.setEmail("jhon.doe1gmail.com"); // or set to null to test missing field

        StepVerifier.create(userUseCase.save(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserCustomException &&
                                throwable.getMessage().equals("Invalid email format"))
                .verify();
    }

    @Test
    void mustFailToSaveWhenRangeSalary() {
        user.setSalary(new BigDecimal(-100)); // or set to null to test missing field

        StepVerifier.create(userUseCase.save(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserCustomException &&
                                throwable.getMessage().equals("Salary must be between 0 and 15,000,000"))
                .verify();
    }

    @Test
    void mustFailToSaveWhenRoleAlreadyExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(false));
        when(roleRepository.existsByIdRole(1)).thenReturn(Mono.just(false));
        when(transactionManager.doInTransaction(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(userUseCase.save(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserCustomException &&
                                throwable.getMessage().equals("idRole does not exist"))
                .verify();
    }


 */
}
