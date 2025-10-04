package com.eco.EcoChain.service;

import com.eco.EcoChain.entity.User;
import com.eco.EcoChain.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
@Data
public class JwtService {
    private final TokenRepository tokenRepository;

    @Value("${jwt.secret_key}")
    private String secretKey;

    @Value("${jwt.access_token_expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh_token_expiration}")
    private long refreshTokenExpiration;

    public JwtService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    private SecretKey getSigningKey(){
        byte[] bytes = Decoders.BASE64URL.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    private String generateToken(User user, long expiryTime){
        JwtBuilder jwtBuilder = Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiryTime))
                .signWith(getSigningKey());
        return jwtBuilder.compact();
    }

    public String generateAccessToken(User user){
        return generateToken(user, accessTokenExpiration);
    }

    public String generateRefreshToken(User user){
        return generateToken(user, refreshTokenExpiration);
    }

    private Claims extractAllClaims(String token){
        JwtParserBuilder jwtParserBuilder = Jwts.parser();
        jwtParserBuilder.verifyWith(getSigningKey());

        return jwtParserBuilder.build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public <T> T extractClaim(String token, Function<Claims, T> resolver){
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isAccessTokenExpired(String token){
        return !extractExpiration(token).before(new Date());
    }

    public boolean isValid(String token, UserDetails user){
        String username = extractUsername(token);

        boolean isValidToken = tokenRepository.findByAccessToken(token)
                .map(t -> !t.isLoggedOut()).orElse(false);

        return username.equals(user.getUsername())
                && isAccessTokenExpired(token) && isValidToken;
    }

    public boolean isValidRefresh(String token, UserDetails user){
        String username = extractUsername(token);

        boolean isValidRefreshToken = tokenRepository.findByRefreshToken(token)
                .map(t -> !t.isLoggedOut()).orElse(false);

        return username.equals(user.getUsername())
                && isAccessTokenExpired(token) && isValidRefreshToken;
    }
}