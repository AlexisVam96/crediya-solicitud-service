package co.com.crediya.r2dbc;

import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.solicitud.Solicitud;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class EstadoReactiveRepositoryAdapterTest {

    private EstadoReactiveRepository repository;
    private ObjectMapper mapper;
    private EstadoReactiveRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(EstadoReactiveRepository.class);
        mapper = mock(ObjectMapper.class);
        adapter = new EstadoReactiveRepositoryAdapter(repository, mapper);
    }

    @Test
    void constructor_shouldCreateAdapter() {
        EstadoReactiveRepository repository = mock(EstadoReactiveRepository.class);
        ObjectMapper mapper = mock(ObjectMapper.class);

        EstadoReactiveRepositoryAdapter adapter = new EstadoReactiveRepositoryAdapter(repository, mapper);

        Assertions.assertNotNull(adapter);
    }

    @Test
    void existsByEmail_shouldDelegateToRepository() {
        String statusName = "Pendiente de Revisión";
        Estado estado = new Estado();
        estado.setNombre(statusName);
        estado.setIdEstado(1);
        estado.setDescripcion("Estado inicial de la solicitud");
        when(repository.findByNombre(statusName)).thenReturn(Mono.just(estado));

        Mono<Estado> result = adapter.findByNombre(statusName);

        StepVerifier.create(result)
                .expectNext(estado)
                .verifyComplete();

        verify(repository, times(1)).findByNombre(statusName);
    }

}