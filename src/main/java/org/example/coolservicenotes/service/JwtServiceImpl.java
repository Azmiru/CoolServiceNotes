package org.example.coolservicenotes.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.example.coolservicenotes.reposoritory.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@Data
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.signingKey}")
    private String signingKey;
    @Value("$jwt.key.expiration")
    private Long jwtExpiration;

    private final UserRepository userRepository;

    private SecretKey key;

    private SecretKey generatedSecretKey() {
        if (key == null) {
            key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
        }
        return key;
    }


    @Override
    public String generatedJwt(Authentication authentication) {
        return "";
    }

    @Override
    public String generatedJwt(UsernamePasswordAuthenticationToken authentication) {
        return Jwts.builder()
                .setClaims(
                        Map.of(
                                ClaimField.USERNAME, authentication.getName(),
                                ClaimField.ROLE, authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()),
                                ClaimField.USER_ID, String.valueOf(userRepository.findByUsername(authentication.getName()).getAuthorities())))
                .setExpiration(new Date(new Date().getTime()+jwtExpiration))
                .setSubject(authentication.getName())
                .signWith(generatedSecretKey())
                .compact();

    }
    @Override
    public Claims getClaims(String jwt) {
        return Jwts.parser()
                .setSigningKey(generatedSecretKey())
                .build()
                .parseClaimsJws(jwt)
                .getPayload();
    }

    @Override
    public boolean isValidJwt(String jwt) {
        Claims claims = JwtService.parser()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(getSigningKey())
                .getPayload();
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(String.valueOf(claims.get(ClaimField.USERNAME))));

        return claims.getExpiration().after(new Date()) && user.isPresent();
    }
}
