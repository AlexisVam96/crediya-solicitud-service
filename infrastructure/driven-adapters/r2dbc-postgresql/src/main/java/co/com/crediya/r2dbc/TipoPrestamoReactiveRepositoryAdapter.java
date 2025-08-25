package co.com.crediya.r2dbc;

import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.crediya.r2dbc.entity.SolicitudEntity;
import co.com.crediya.r2dbc.entity.TipoPrestamoEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TipoPrestamoReactiveRepositoryAdapter extends ReactiveAdapterOperations<TipoPrestamo, TipoPrestamoEntity, Integer, TipoPrestamoReactiveRepository>
 implements TipoPrestamoRepository
{
    public TipoPrestamoReactiveRepositoryAdapter(TipoPrestamoReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, TipoPrestamo.class));
    }

}
