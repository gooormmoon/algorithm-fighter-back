package gooroommoon.algofi_core.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final String ISSUER;

    private final long EXPIRE_TIME;

    private final SecretKey secretKey;

    public JwtUtil(@Value("${spring.application.name}") String issuer,
                   @Value("${jwt.expire-time}") long expireTime,
                   @Value("${jwt.secret-key}") String secretKey) {
        this.ISSUER = issuer;
        this.EXPIRE_TIME = expireTime;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }

    public String createToken(String loginId) {

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .subject(loginId)
                .issuer(ISSUER)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(secretKey)
                .compact();

    }

    public Authentication getAuthentication(String token) {
        Claims claims = extractPayload(token);
        return new UsernamePasswordAuthenticationToken(
                claims.getSubject(), null, AuthorityUtils.createAuthorityList(claims.get("role", String.class)));
    }

    public boolean isExpired(String token) {

        return extractPayload(token)
                .getExpiration()
                .before(new Date());
    }

    private Claims extractPayload(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
