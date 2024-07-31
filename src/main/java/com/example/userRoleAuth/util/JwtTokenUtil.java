package com.example.userRoleAuth.util;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        if (secret.length() < 32) {
            throw new IllegalArgumentException("Secret key must be at least 256 bits (32 characters)");
        }
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        return doGenerateToken(userDetails.getUsername());
    }

    private String doGenerateToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 5 * 60 * 60 * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateOtpToken(String otp) {
        return Jwts.builder()
                .setSubject(otp)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Boolean validateOtpToken(String token, String otp) {
        final String tokenOtp = getUsernameFromToken(token);
        return (tokenOtp.equals(otp)) && !isTokenExpired(token);
    }

    private Map<String, String> otpStore = new ConcurrentHashMap<>();

    public void storeOtp(String username, String otp) {
        otpStore.put(username, otp);
        // Remove OTP after 5 minutes
        scheduleOtpRemoval(username, otp);
    }

    public boolean validateOtp(String username, String otp) {
        return otp.equals(otpStore.get(username));
    }

    @Scheduled(fixedRate = 60000) // Check every minute
    public void cleanUpExpiredOtps() {
        otpStore.clear();
    }

    private void scheduleOtpRemoval(String username, String otp) {
        // Remove OTP after 5 minutes
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            otpStore.remove(username, otp);
        }, 5, TimeUnit.MINUTES);
    }
}
