package co.com.crediya.r2dbc;

import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.r2dbc.entity.TipoPrestamoEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

// TODO: This file is just an example, you should delete or modify it
public interface TipoPrestamoReactiveRepository extends ReactiveCrudRepository<TipoPrestamoEntity, Integer>, ReactiveQueryByExampleExecutor<TipoPrestamoEntity> {

    Mono<TipoPrestamo> findByIdTipoPrestamo(Integer idTipoPrestamo);
}
