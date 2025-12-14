package com.ohgiraffers.user.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JWTUtil {
    private final Key key;

    public JWTUtil(@Value("${jwt.secret}")String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
    //토큰생성
    private String createToken(String username,String role, long expireTime) {
        Claims claims=Jwts.claims();
        claims.put("username",username);
        claims.put("role",role);


        Date now = new Date();
        Date validity = new Date(now.getTime() + expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    // Access토큰 생성
    public String createAccessToken(String username,String role){
        return createToken(username,role, TimeUnit.MINUTES.toMillis(30));
    }

    // Refresh토큰 생성
    public String createRefreshToken(String username,String role){
        return createToken(username,role, TimeUnit.DAYS.toMillis(1));
    }

    // 토큰 만료 여부
    public boolean isTokenExpired(String token){
        try{
            return Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token).getBody().getExpiration().before(new Date());
        }catch(ExpiredJwtException e){
            return true;
        }catch(Exception e){
            return true;
        }
    }

    public long getExpriation(String token){
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getExpiration().getTime();
    }

    // 토큰 검증
    public Boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
            //문자열이 잘렸거나 변종됐다면
        }catch(SecurityException | MalformedJwtException e){
            //지원되지 않는 토큰
        }catch(UnsupportedJwtException e){

        }catch(IllegalArgumentException e){

        }
        return false;
    }


    public String getUsername(String token){
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody()
                .get("username",String.class);
    }


    public String getRole(String token){
        String role=Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody()
                .get("role",String.class);

        return "ROLE_"+role;
    }


}
