package fr.pinguet62.meetall.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import static com.nimbusds.jose.JWSAlgorithm.HS256;
import static fr.pinguet62.meetall.security.SecretKeyUtils.fromString;
import static java.util.Objects.requireNonNull;

/**
 * Bijective functions to generate and parse JWT token.
 */
@Component
public class JwtTokenGenerator {

    static final JWSAlgorithm ALGORITHM = HS256;

    private final SecretKey jwtSymmetricKey;

    public JwtTokenGenerator(@Value("${spring.security.oauth2.resourceserver.jwt.key-value}") String jwtSymmetricKey) {
        this.jwtSymmetricKey = fromString(requireNonNull(jwtSymmetricKey));
    }

    // testing
    String generateToken(String userId, JWSAlgorithm algorithm, SecretKey secret) {
        JWSHeader header = new JWSHeader(algorithm);
        JWTClaimsSet claims = new Builder()
                .subject(userId)
                .build();
        Payload payload = new Payload(claims.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        JWSSigner signer;
        try {
            signer = new MACSigner(secret);
            jwsObject.sign(signer);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jwsObject.serialize();
    }

    public String generateToken(String userId) {
        return this.generateToken(userId, ALGORITHM, jwtSymmetricKey);
    }

}
