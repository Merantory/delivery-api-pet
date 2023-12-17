package com.merantory.dostavim.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.merantory.dostavim.exception.JWTExpiredTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZonedDateTime;

@Component
public class JwtUtil {
    @Value("${jwt_secret}")
    private String secret;

    public String generateToken(String email) {
        Instant expirationDateTime = ZonedDateTime.now().plusMinutes(60).toInstant();

        return JWT.create()
                .withSubject("User details")
                .withClaim("email", email)
                .withIssuedAt(Instant.now())
                .withIssuer("dostavim")
                .withExpiresAt(expirationDateTime)
                .sign(Algorithm.HMAC256(secret));
    }

    public String validateTokenAndRetrieveClaim(String jwtToken) throws JWTVerificationException, JWTExpiredTokenException {
        return verifyToken(jwtToken).getClaim("email").asString();
    }

    private DecodedJWT verifyToken(String jwtToken) throws JWTVerificationException, JWTExpiredTokenException {
        DecodedJWT decodedJWT = JWT.decode(jwtToken);
        try {
            return verifyTokenBuild().verify(jwtToken);
        } catch (JWTVerificationException jwtVerificationException) {
            if (decodedJWT.getExpiresAtAsInstant().isBefore(Instant.now())) {
                throw new JWTExpiredTokenException();
            }
            throw jwtVerificationException;
        }
    }
    private JWTVerifier verifyTokenBuild() {
        return JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User details")
                .withIssuer("dostavim")
                .build();
    }
}
