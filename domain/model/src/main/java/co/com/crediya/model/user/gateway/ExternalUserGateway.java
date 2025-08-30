package co.com.crediya.model.user.gateway;

import co.com.crediya.model.user.User;
import reactor.core.publisher.Mono;

public interface ExternalUserGateway {

    Mono<User> findByDocumentNumber(String documentNumber);
}
