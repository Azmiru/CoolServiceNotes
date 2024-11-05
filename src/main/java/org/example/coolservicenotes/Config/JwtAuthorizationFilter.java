package org.example.coolservicenotes.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.coolservicenotes.Entity.Jwt;
import org.example.coolservicenotes.Entity.UsernamePasswordAuthentication;
import org.example.coolservicenotes.service.ClaimField;
import org.example.coolservicenotes.service.HeaderValues;
import org.example.coolservicenotes.service.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String authorizationKey = request.getHeader(HeaderValues.AUTHORIZATION);
        if (Optional.ofNullable(authorizationKey).isPresent() && authorizationKey.startsWith(HeaderValues.BEARER)) {
            authorizationKey = authorizationKey.replace(HeaderValues.BEARER, "");
            try {
                if (jwtService.isValidJwt(String.valueOf(new Jwt(authorizationKey)))) {
                    Claims claims = jwtService.getClaims(authorizationKey);
                    String username = String.valueOf(claims.get(ClaimField.USERNAME));
                    List roles = claims.get(ClaimField.ROLE, List.class);
                    List<GrantedAuthority> authorities =
                            (List<GrantedAuthority>) roles.stream().map(role -> new SimpleGrantedAuthority(role.toString())).collect(Collectors.toList());
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthentication(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException e) {
                logger.error(e.getMessage());
                SecurityContextHolder.getContext().setAuthentication(null);
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            }
        }
        filterChain.doFilter(request, response);
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().equals("/login");
    }
}
