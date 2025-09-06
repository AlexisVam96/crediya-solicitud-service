package co.com.crediya.r2dbc;

import co.com.crediya.r2dbc.entity.SolicitudEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

// TODO: This file is just an example, you should delete or modify it
public interface SolicitudReactiveRepository extends ReactiveCrudRepository<SolicitudEntity, Integer>, ReactiveQueryByExampleExecutor<SolicitudEntity> {

    Flux<SolicitudEntity> findByIdEstado(String idEstado);
}
