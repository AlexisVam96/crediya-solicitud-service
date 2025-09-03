package co.com.crediya.model.security;

import reactor.core.publisher.Mono;

public interface JwtAuthenticationGateway {

    public Mono<String> getCurrentEmail();
}
