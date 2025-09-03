package co.com.crediya.jwt;

import co.com.crediya.model.security.JwtAuthenticationGateway;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationAdapter implements JwtAuthenticationGateway {

    @Override
    public Mono<String> getCurrentEmail() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName()); // Assuming the username is the email
    }
}
