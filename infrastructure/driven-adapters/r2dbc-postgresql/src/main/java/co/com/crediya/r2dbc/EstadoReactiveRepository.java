package co.com.crediya.r2dbc;

import co.com.crediya.model.estado.Estado;
import co.com.crediya.r2dbc.entity.EstadoEntity;
import co.com.crediya.r2dbc.entity.SolicitudEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

// TODO: This file is just an example, you should delete or modify it
public interface EstadoReactiveRepository extends ReactiveCrudRepository<EstadoEntity, Integer>, ReactiveQueryByExampleExecutor<EstadoEntity> {

    Mono<Estado> findByNombre(String nombre);
}
