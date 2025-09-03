package co.com.crediya.r2dbc;

import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class TipoPrestamoReactiveRepositoryAdapterTest {

    private TipoPrestamoReactiveRepository repository;
    private ObjectMapper mapper;
    private TipoPrestamoReactiveRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(TipoPrestamoReactiveRepository.class);
        mapper = mock(ObjectMapper.class);
        adapter = new TipoPrestamoReactiveRepositoryAdapter(repository, mapper);
    }

    @Test
    void constructor_shouldCreateAdapter() {
        TipoPrestamoReactiveRepository repository = mock(TipoPrestamoReactiveRepository.class);
        ObjectMapper mapper = mock(ObjectMapper.class);

        TipoPrestamoReactiveRepositoryAdapter adapter = new TipoPrestamoReactiveRepositoryAdapter(repository, mapper);

        Assertions.assertNotNull(adapter);
    }

    @Test
    void existsByEmail_shouldDelegateToRepository() {
        Integer idPrestamo = 1;
        TipoPrestamo tipoPrestamo = new TipoPrestamo();
        tipoPrestamo.setNombre("Prestamo Personal");
        tipoPrestamo.setIdTipoPrestamo(1);

        when(repository.findByIdTipoPrestamo(idPrestamo)).thenReturn(Mono.just(tipoPrestamo));


        Mono<TipoPrestamo> result = adapter.findByIdTipoPrestamo(idPrestamo);

        StepVerifier.create(result)
                .expectNext(tipoPrestamo)
                .verifyComplete();

        verify(repository, times(1)).findByIdTipoPrestamo(idPrestamo);
    }

}