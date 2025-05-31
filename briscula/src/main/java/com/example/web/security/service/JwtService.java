package com.example.web.security.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final String secretKey;

  public JwtService() {
    try {
      KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
      SecretKey sk = keyGenerator.generateKey();
      secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public String generateToken(String email) {
    Map<String, Object> claims = new HashMap<>();

    Date threeHoursFromNow = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 3);

    return Jwts.builder()
        .claims()
        .add(claims)
        .subject(email)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(threeHoursFromNow)
        .and()
        .signWith(getKey())
        .compact();
  }

  private SecretKey getKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String extractEmail(String token) {
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

  public boolean validateToken(String token, String email) {
    final String userEmail = extractEmail(token);
    return (userEmail.equals(email) && !isTokenExpired(token));
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }
}
