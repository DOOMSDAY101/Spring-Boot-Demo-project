package com.example.demo.service;

// import java.security.NoSuchAlgorithmException;
// import java.util.Base64;
import java.util.Date;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final String SCOPE_ADD_PRODUCT = "ADD_PRODUCT";
    private static final long EXPIRY_MILLIS = 5 * 60 * 1000; // 5 minutes

    // public JWTService() {
    // try {
    // KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");

    // SecretKey sk = keyGen.generateKey();
    // secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
    // } catch (NoSuchAlgorithmException e) {
    // e.printStackTrace();
    // }
    // }
    public JWTService(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = secretKey;
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + Duration.ofMinutes(15).toMillis()))
                .and()
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // SHORT LIVED TOKEN
    public String generateActionToken(String username) {
        return Jwts.builder()
                .claims()
                .add("scope", SCOPE_ADD_PRODUCT)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRY_MILLIS))
                .and()
                .signWith(getKey())
                .compact();
    }

    /**
     * Returns true only if the token is valid, unexpired, has the right scope,
     * and belongs to the given username.
     */
    public boolean validateActionToken(String token, String username) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String subject = claims.getSubject();
            String scope = claims.get("scope", String.class);

            return subject != null
                    && subject.equals(username)
                    && SCOPE_ADD_PRODUCT.equals(scope);

        } catch (JwtException e) {
            return false;
        }
    }

}
