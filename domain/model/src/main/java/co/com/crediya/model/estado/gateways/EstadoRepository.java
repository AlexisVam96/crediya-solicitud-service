package co.com.crediya.model.estado.gateways;

import co.com.crediya.model.estado.Estado;
import reactor.core.publisher.Mono;

public interface EstadoRepository {

    Mono<Estado> findById(Integer id);

    Mono<Estado> findByNombre(String nombre);
}
