package co.com.crediya.model.transaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionManager {
    <T> Mono<T> doInTransaction(Mono<T> action);
    <T> Flux<T> doInTransaction(Flux<T> action);
}
