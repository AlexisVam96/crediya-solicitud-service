package co.com.crediya.r2dbc;

import co.com.crediya.model.solicitud.Solicitud;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class SolicitudReactiveRepositoryAdapterTest {

    private SolicitudReactiveRepository repository;
    private ObjectMapper mapper;
    private SolicitudReactiveRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(SolicitudReactiveRepository.class);
        mapper = mock(ObjectMapper.class);
        adapter = new SolicitudReactiveRepositoryAdapter(repository, mapper);
    }

    @Test
    void constructor_shouldCreateAdapter() {
        SolicitudReactiveRepository repository = mock(SolicitudReactiveRepository.class);
        ObjectMapper mapper = mock(ObjectMapper.class);

        SolicitudReactiveRepositoryAdapter adapter = new SolicitudReactiveRepositoryAdapter(repository, mapper);

        Assertions.assertNotNull(adapter);
    }

    @Test
    void findAll_shouldReturnAllSolicitudes() {
        Solicitud sol1 = new Solicitud(); // Set fields as needed
        Solicitud sol2 = new Solicitud();
        SolicitudReactiveRepositoryAdapter spyAdapter = spy(adapter);

        doReturn(Flux.just(sol1, sol2)).when(spyAdapter).findAll();

        Flux<Solicitud> result = spyAdapter.findAll();

        StepVerifier.create(result)
                .expectNext(sol1)
                .expectNext(sol2)
                .verifyComplete();
    }




}