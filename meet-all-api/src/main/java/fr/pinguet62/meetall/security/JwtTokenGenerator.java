package fr.pinguet62.meetall.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Bijective functions to generate and parse JWT token.
 */
@Component
public class JwtTokenGenerator {

    static final Function<String, Algorithm> ALGORITHM = Algorithm::HMAC256;

    private final String secret;

    public JwtTokenGenerator(@Value("${spring.security.oauth2.jwt.secret}") String secret) {
        this.secret = requireNonNull(secret);
    }

    // testing
    String generateToken(String userId, Function<String, Algorithm> algorithm, String secret) {
        return JWT.create().withSubject(userId).sign(algorithm.apply(secret));
    }

    public String generateToken(String userId) {
        return this.generateToken(userId, ALGORITHM, secret);
    }

    public DecodedJWT verifyAndConvertToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(ALGORITHM.apply(secret)).build();
        return verifier.verify(token);
    }

}
