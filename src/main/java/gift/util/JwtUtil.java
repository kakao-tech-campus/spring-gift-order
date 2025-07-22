package gift.util;

import gift.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret.key}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(Member member) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(member.getId().toString())
                .claim("role", member.getRole())
                .issuedAt(new Date(now))
                .expiration(new Date(now + 1000 * 60 * 60)) // 1시간 유효
                .signWith(secretKey)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        String userIdStr = getClaims(token).getSubject();
        return Long.parseLong(userIdStr);
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}