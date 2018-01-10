package kwasilewski.marketplace.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class JwtTokenUtil {

    private final static String key = "something";
    private final static int expDays = 30;

    public static String createToken(Long usrId) throws JwtException {
        long expMillis = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(expDays);
        return Jwts.builder().setSubject(usrId.toString()).signWith(SignatureAlgorithm.HS512, key).setExpiration(new Date(expMillis)).compact();
    }

    public static Long getIdFromToken(String token) throws JwtException {
        return Long.parseLong(Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject());
    }

    public static Date minimumTokenDate() {
        return Date.from(LocalDateTime.now().minusDays(expDays).atZone(ZoneId.systemDefault()).toInstant());
    }

}
