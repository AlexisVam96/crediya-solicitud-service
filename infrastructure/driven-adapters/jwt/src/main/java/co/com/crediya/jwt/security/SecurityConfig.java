package co.com.crediya.jwt.security;

import co.com.crediya.model.exception.ErrorType;
import co.com.crediya.model.exception.LoanApplicationCustomerException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http){
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Disable CSRF protection
                .authorizeExchange(authorize -> authorize
                        // Allow POST requests to /api/v1/solicitud for users with USER role
                        .pathMatchers(HttpMethod.POST,"/api/v1/solicitud").hasRole("USER")
                        .pathMatchers(HttpMethod.GET,"/api/v1/solicitud").hasRole("ADMIN")
                        .pathMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        ).permitAll()// login/register público
                        .anyExchange().authenticated() // Disable all requests
                )
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION) // Add custom JWT authentication filter
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)) // 401
                        .accessDeniedHandler(customAccessDeniedHandler()) // 403
                )
                .build();

    }

    @Bean
    public ServerAccessDeniedHandler customAccessDeniedHandler() {
        return (exchange, denied) -> {
            return Mono.error(new LoanApplicationCustomerException("Access Denied: You do not have permission to access this resource.", ErrorType.FORBIDDEN));
        };
    }

}
