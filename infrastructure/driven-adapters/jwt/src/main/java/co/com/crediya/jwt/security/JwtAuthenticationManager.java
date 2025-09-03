package co.com.crediya.jwt.security;

import co.com.crediya.model.exception.ErrorType;
import co.com.crediya.model.exception.LoanApplicationCustomerException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        return Mono.just(authentication.getCredentials().toString())
                .map(token -> {
                    SecretKey key = Keys.hmacShaKeyFor(Base64.getEncoder().encode(secretKey.getBytes()));
                    var claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
                    claims.put("token", token);
                    return claims;
                })
                .onErrorResume(error -> Mono.error(new LoanApplicationCustomerException(
                        "Error process token: " + error.getMessage(), ErrorType.AUTH)))
                .map(claims -> {
                    String username = claims.get("email", String.class);
                    String role = claims.get("role", String.class);
                    String token = claims.get("token", String.class);
                    Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                    return new UsernamePasswordAuthenticationToken(username, token, authorities);
                });
    }

    /*
    private final JwtTokenProviderAdapter jwtUtil;

    public JwtAuthenticationManager(JwtTokenProviderAdapter jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();

        return jwtUtil.validateToken(authToken)
                .flatMap(valid -> {
                    if (!valid) {
                        return Mono.empty(); // token inválido
                    }

                    // Aquí sigo de forma reactiva
                    return jwtUtil.getUsernameFromToken(authToken)
                            .zipWith(jwtUtil.getRoleFromToken(authToken))
                            .map(tuple -> {
                                String username = tuple.getT1();
                                String role = tuple.getT2();

                                return new UsernamePasswordAuthenticationToken(
                                        username,
                                        null,
                                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                                );
                            });
                });
    }

     */

}

