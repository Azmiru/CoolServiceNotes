package org.example.coolservicenotes.service;
import io.jsonwebtoken.Claims;

import io.jsonwebtoken.JwtParserBuilder;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public interface JwtService {
    static JwtParserBuilder parser() {
        return null;
    }

    String generatedJwt(Authentication authentication);

    String generatedJwt(UsernamePasswordAuthenticationToken authentication);
    Claims getClaims(String jwt);
    boolean isValidJwt(String jwt);
}
