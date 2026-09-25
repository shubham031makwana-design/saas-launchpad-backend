package saas_launchpad_backend.security;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final String SECRET =
            "mysecretkeymysecretkeymysecretkey123456";

    private final Key key =
            Keys.hmacShaKeyFor(SECRET.getBytes());

    // Generate JWT Token
    public String generateToken(
            String email,
            String role) {

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 86400000
                        )
                )
                .signWith(key)
                .compact();
    }

    // Extract email
    public String extractEmail(String token) {

        Jws<Claims> claims =
                Jwts.parser()
                        .verifyWith(
                                (javax.crypto.SecretKey) key)
                        .build()
                        .parseSignedClaims(token);

        return claims.getPayload().getSubject();
    }

    // Validate token
    public boolean validateToken(
            String token,
            String email) {

        return extractEmail(token).equals(email);
    }

    // Check token expiry
    public boolean isTokenExpired(String token) {

        Jws<Claims> claims =
                Jwts.parser()
                        .verifyWith(
                                (javax.crypto.SecretKey) key)
                        .build()
                        .parseSignedClaims(token);

        return claims.getPayload()
                .getExpiration()
                .before(new Date());
    }
}