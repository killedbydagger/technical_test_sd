package com.temp.demo.security;

import com.temp.demo.entity.Staff;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.DefaultClock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -3301605591108950415L;
    private final Clock clock = DefaultClock.INSTANCE;

    @Value("${jwt.signing.key.secret}")
    private String jwtSecret;

    @Value("${jwt.token.expiration.in.seconds}")
    private String jwtTokenExpiration;

    public String generateToken(Staff staff) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", staff.getId());
        claims.put("password", staff.getPassword());
        return doGenerateToken(claims, staff);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        return (!isTokenExpired(token) && userDetails.isEnabled());
    }

    public String getUsernameFromToken(String jwtToken) {
        return getClaimFromToken(jwtToken, Claims::getSubject);
    }

    private String doGenerateToken(Map<String, Object> claims, UserDetails userDetails) {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate);
        String subject = userDetails.getUsername();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    private Date calculateExpirationDate(Date createdDate) {
        try {
            return new Date(createdDate.getTime() + Long.parseLong(jwtTokenExpiration) * 1000);
        } catch (NullPointerException e) {
            return new Date(createdDate.getTime() + 3600 * 1000);
        }
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(clock.now());
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws SignatureException {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) throws SignatureException {
        try {
            return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
