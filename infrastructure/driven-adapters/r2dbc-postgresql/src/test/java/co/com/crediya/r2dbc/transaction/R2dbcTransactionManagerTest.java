package co.com.crediya.r2dbc.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class R2dbcTransactionManagerTest {

    private TransactionalOperator operator;
    private R2dbcTransactionManager manager;

    @BeforeEach
    void setUp() {
        operator = mock(TransactionalOperator.class);
        manager = new R2dbcTransactionManager(operator);
    }

    @Test
    void doInTransaction_withMono_shouldApplyTransactionalOperator() {
        Mono<String> action = Mono.just("test");
        when(operator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mono<String> result = manager.doInTransaction(action);

        StepVerifier.create(result)
                .expectNext("test")
                .verifyComplete();

        verify(operator, times(1)).transactional(any(Mono.class));
    }

    @Test
    void doInTransaction_withFlux_shouldApplyTransactionalOperator() {
        Flux<String> action = Flux.just("a", "b");
        when(operator.transactional(any(Flux.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Flux<String> result = manager.doInTransaction(action);

        StepVerifier.create(result)
                .expectNext("a", "b")
                .verifyComplete();

        verify(operator, times(1)).transactional(any(Flux.class));
    }
}