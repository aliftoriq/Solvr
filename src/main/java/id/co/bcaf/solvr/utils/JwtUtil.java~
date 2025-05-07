package id.co.bcaf.solvr.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtil {

    private final Key SECRET_KEY;
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
//        System.out.println("SECRET_KEY (Raw): " + secret);
        this.SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
//        System.out.println("SECRET_KEY (Base64 Encoded): " + SECRET_KEY);
    }

    private Key getSigningKey() {
        return SECRET_KEY;
    }

    // Generate Token
    public String generateToken(String username, String role, UUID id) {
        Date issuedAt = new Date();
        Date expiration = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

        System.out.println("Token Generated:");
        System.out.println("Issued At: " + issuedAt);
        System.out.println("Expires At: " + expiration);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("id", id.toString())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Get username from token
    public String extractusername(String token) {
        try {
            // Debugging token sebelum diproses
            System.out.println("Received Token: [" + token + "]");

            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token.trim())
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired at: " + e.getClaims().getExpiration());
        } catch (MalformedJwtException e) {
            throw new RuntimeException("Malformed JWT: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // Use the same key used in signing
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract Role dari Token
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // Extract ID dari Token
    public UUID extractId(String token) {
        String idStr = extractAllClaims(token).get("id", String.class);
        return UUID.fromString(idStr);
    }

    // Validate token
    public boolean validateToken(String token, String username) {
//        System.out.println("Validating token for username: " + username);
//        System.out.println(username.equals(extractusername(token)) && !isTokenExpired(token));
        return username.equals(extractusername(token)) && !isTokenExpired(token);
    }

    // Verify token
    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

}