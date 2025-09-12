package co.com.crediya.r2dbc;

import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.r2dbc.entity.SolicitudEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class SolicitudReactiveRepositoryAdapter extends ReactiveAdapterOperations<Solicitud, SolicitudEntity, Integer, SolicitudReactiveRepository>
 implements SolicitudRepository
{
    public SolicitudReactiveRepositoryAdapter(SolicitudReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Solicitud.class));
    }

    @Override
    public Flux<Solicitud> findByIdEstado(Integer page, Integer size, String idEstado) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);

        return repository.findByIdEstado(idEstado)
                .skip(pageable.getOffset())
                .take(pageable.getPageSize())
                .map(solicitudEntity -> mapper.map(solicitudEntity, Solicitud.class));
    }

    @Override
    public Mono<Solicitud> findByIdSolicitud(Integer idSolicitud) {
        return repository.findByIdSolicitud(idSolicitud)
                .map(entity -> mapper.map(entity, Solicitud.class));
    }

}
