package co.com.crediya.r2dbc.transaction;

import co.com.crediya.model.transaction.TransactionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class R2dbcTransactionManager implements TransactionManager {

    private final TransactionalOperator operator;

    @Override
    public <T> Mono<T> doInTransaction(Mono<T> action) {
        return action.as(operator::transactional);
    }

    @Override
    public <T> Flux<T> doInTransaction(Flux<T> action) {
        return action.as(operator::transactional);
    }
}